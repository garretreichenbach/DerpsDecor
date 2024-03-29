package thederpgamer.decor.data.system.crew;

import api.common.GameClient;
import api.common.GameServer;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.client.view.character.CharactersDrawer;
import org.schema.game.client.view.character.DrawableAIHumanCharacterNew;
import org.schema.game.common.controller.ai.AIGameCreatureConfiguration;
import org.schema.game.common.controller.ai.Types;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.creature.AICharacter;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.server.data.CreatureSpawn;
import org.schema.game.server.data.CreatureType;
import org.schema.schine.graphicsengine.animation.structure.classes.AnimationIndex;
import org.schema.schine.graphicsengine.animation.structure.classes.AnimationIndexElement;
import org.schema.schine.graphicsengine.core.settings.StateParameterNotFoundException;
import org.schema.schine.network.objects.Sendable;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.systems.modules.CrewStationModule;
import thederpgamer.decor.utils.AnimationUtils;
import thederpgamer.decor.utils.SegmentPieceUtils;

import java.util.Objects;

/**
 * [Description]
 *
 * @author TheDerpGamer
 */
public class CrewData {
	public long entityID;
	public long indexAndOrientation;
	public String crewName = "Crew Member";
	public String animationName = AnimationIndex.IDLING_FLOATING.toString();
	public Vector3i offset = new Vector3i();
	public boolean looping = true;
	public boolean needsUpdate = true;
	public boolean active = true;
	public Transform transform = new Transform();
	private transient DrawableAIHumanCharacterNew drawer;

	public CrewData(SegmentPiece segmentPiece) {
		entityID = segmentPiece.getSegmentController().getDbId();
		indexAndOrientation = ElementCollection.getIndex4(segmentPiece.getAbsoluteIndex(), segmentPiece.getOrientation());
		segmentPiece.getTransform(transform);
		transform.origin.add(offset.toVector3f());
		if(segmentPiece.getSegmentController().isOnServer()) {
			updateCrew();
			recall();
		}
	}

	public void spawn() {
		assert getSegmentPiece() != null && getSegmentPiece().getSegmentController().isOnServer();
		if(getCrewMember() != null) getCrewMember().setMarkedForDeleteVolatile(true); //Remove old crew member (if exists)
		if(isAlreadySpawned()) recall();
		else {
			SegmentPiece segmentPiece = getSegmentPiece();
			if(transform == null) transform = new Transform();
			segmentPiece.getTransform(transform);
			Transform tempTransform = new Transform(transform);
			tempTransform.origin.add(offset.toVector3f());
			CreatureSpawn spawn = new CreatureSpawn(segmentPiece.getSegmentController().getSector(new Vector3i()), tempTransform, crewName, CreatureType.CHARACTER) {
				@Override
				public void initAI(AIGameCreatureConfiguration<?, ?> aiConfiguration) {
					try {
						assert aiConfiguration != null;
						aiConfiguration.get(Types.ORIGIN_X).switchSetting(String.valueOf(Integer.MIN_VALUE), true);
						aiConfiguration.get(Types.ORIGIN_Y).switchSetting(String.valueOf(Integer.MIN_VALUE), true);
						aiConfiguration.get(Types.ORIGIN_Z).switchSetting(String.valueOf(Integer.MIN_VALUE), true);
						aiConfiguration.get(Types.ORDER).switchSetting(AIGameCreatureConfiguration.BEHAVIOR_IDLING, true);
					} catch(StateParameterNotFoundException exception) {
						DerpsDecor.getInstance().logException("Failed to initialize AI", exception);
					}
				}
			};
			GameServer.getServerState().getController().queueCreatureSpawn(spawn);
			needsUpdate = true;
		}
	}

	public void updateCrew() {
		try {
			if(!isSegmentPieceValid() && getCrewMember() != null) {
				getCrewMember().setMarkedForDeleteVolatile(true);
				if(getSegmentPiece() != null) {
					CrewStationModule module = (CrewStationModule) ((ManagedSegmentController<?>) getSegmentPiece().getSegmentController()).getManagerContainer().getModMCModule(ElementManager.getBlock("NPC Station").getId());
					if(module != null) module.removeCrewBlock(indexAndOrientation);
				}
				return;
			}
			recall();
			AICharacter crewMember = getCrewMember();
			crewMember.getAiConfiguration().getAiEntityState().start();
			DrawableAIHumanCharacterNew drawer = getDrawer();
			crewMember.setFactionId(getSegmentPiece().getSegmentController().getFactionId());
			if(crewMember.forcedAnimation != null && (!crewMember.forcedAnimation.animation.equals(getAnimation()))) {
				if(active) AnimationUtils.setAnimation(crewMember, drawer, getAnimation(), looping);
				else AnimationUtils.setAnimation(crewMember, drawer, AnimationIndex.IDLING_FLOATING, true);
			}
		} catch(Exception exception) {
			DerpsDecor.getInstance().logException("Failed to update crew member", exception);
		}
		needsUpdate = false;
	}

	private boolean isSegmentPieceValid() {
		return getSegmentPiece() != null && getSegmentPiece().getSegmentController().getSegmentBuffer().existsPointUnsave(ElementCollection.getPosIndexFrom4(indexAndOrientation)) && getSegmentPiece().getSegmentController().getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(indexAndOrientation)).getType() == getSegmentPiece().getType() && getSegmentPiece().getSegmentController().getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(indexAndOrientation)).equals(getSegmentPiece());
	}

	public AICharacter getCrewMember() {
		AICharacter crewMember = null;
		try {
			boolean deleteOld = false;
			for(Sendable sendable : GameServer.getServerState().getLocalAndRemoteObjectContainer().getLocalObjects().values()) {
				if(sendable instanceof AICharacter) {
					AICharacter character = (AICharacter) sendable;
					if(character.getName().equals(crewName)) {
						if(deleteOld) character.setMarkedForDeleteVolatile(true);
						else {
							deleteOld = true;
							crewMember = character;
						}
					}
				}
			}
		} catch(Exception exception) {
			DerpsDecor.getInstance().logException("Failed to initialize crew member as it hasn't been spawned yet", exception);
		}
		return crewMember;
	}

	public SegmentPiece getSegmentPiece() {
		try {
			return Objects.requireNonNull(SegmentPieceUtils.getEntityFromDbId(entityID)).getSegmentBuffer().getPointUnsave(ElementCollection.getPosIndexFrom4(indexAndOrientation));
		} catch(NullPointerException exception) {
			DerpsDecor.getInstance().logException("Failed to get segment piece", exception);
			return null;
		}
	}

	public AnimationIndexElement getAnimation() {
		for(AnimationIndexElement animation : AnimationIndex.animations) {
			if(animation.toString().equalsIgnoreCase(animationName)) return animation;
		}
		return AnimationIndex.IDLING_FLOATING;
	}

	public DrawableAIHumanCharacterNew getDrawer() {
		if(drawer == null) {
			if(GameClient.getClientState().getWorldDrawer().getCharacterDrawer().getPlayerCharacters().containsKey(getCrewMember().getId())) {
				drawer = (DrawableAIHumanCharacterNew) GameClient.getClientState().getWorldDrawer().getCharacterDrawer().getPlayerCharacters().get(getCrewMember().getId());
			} else drawer = (DrawableAIHumanCharacterNew) CharactersDrawer.getDrawer(getCrewMember(), GameClient.getClientState().getGraphicsContext().timer, GameClient.getClientState());
		}
		return drawer;
	}

	public boolean isAlreadySpawned() {
		return getCrewMember() != null && getCrewMember().isHasSpawnedOnServer();
	}

	public void recall() {
		try {
			if(!isAlreadySpawned()) spawn();
			if(getCrewMember() == null) throw new NullPointerException("Crew member is null!");
			transform.setIdentity();
			getSegmentPiece().getTransform(transform);
			transform.origin.add(offset.toVector3f());
			getCrewMember().getWorldTransform().set(transform);
		} catch(NullPointerException exception) {
			DerpsDecor.getInstance().logException("Failed to recall crew member", exception);
		}
		needsUpdate = true;
	}

	public void removeCrew() {
		if(getCrewMember() != null) getCrewMember().setMarkedForDeleteVolatile(true);
	}

	public void setCrewName(String crewName) {
		if(getCrewMember() == null) spawn();
		if(getCrewMember() != null) {
			getCrewMember().setRealName(crewName);
			this.crewName = crewName;
		}
	}
}