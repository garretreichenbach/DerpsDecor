package thederpgamer.decor.utils;

import api.common.GameClient;
import api.common.GameServer;
import api.utils.game.SegmentControllerUtils;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import org.schema.common.FastMath;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.common.util.linAlg.Vector3i;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ElementCollectionManager;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.Element;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.world.SegmentData;
import org.schema.schine.network.objects.Sendable;

import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/10/2021
 */
public class SegmentPieceUtils {
	// Rotation Helpers
	private static final Matrix3f mY = new Matrix3f();
	private static final Matrix3f mYB = new Matrix3f();
	private static final Matrix3f mYC = new Matrix3f();
	private static final Matrix3f mX = new Matrix3f();
	private static final Matrix3f mXB = new Matrix3f();
	private static final Matrix3f mXC = new Matrix3f();

	static {
		mY.setIdentity();
		mY.rotY(FastMath.HALF_PI);
		mYB.setIdentity();
		mYB.rotY(-FastMath.HALF_PI);
		mYC.setIdentity();
		mYC.rotY(FastMath.PI);
		mX.setIdentity();
		mX.rotX(FastMath.HALF_PI);
		mXB.setIdentity();
		mXB.rotX(-FastMath.HALF_PI);
		mXC.setIdentity();
		mXC.rotX(FastMath.PI);
	}

	public static int getDistance(SegmentPiece pieceA, SegmentPiece pieceB) {
		return (int) Math.ceil(Vector3fTools.distance(pieceA.x, pieceA.y, pieceA.z, pieceB.x, pieceB.y, pieceB.z));
	}

	public static boolean withinSameAxisAndAngle(SegmentPiece pieceA, SegmentPiece pieceB, float maxAngle) {
		Transform pieceATransform = new Transform();
		pieceA.getTransform(pieceATransform);
		Transform pieceBTransform = new Transform();
		pieceB.getTransform(pieceBTransform);
		Vector3f pieceAForward = new Vector3f();
		Vector3f pieceBForward = new Vector3f();
		Element.getRelativeForward(pieceA.getOrientation(), Element.FRONT, pieceAForward);
		Element.getRelativeForward(pieceB.getOrientation(), Element.FRONT, pieceBForward);
		return pieceAForward.dot(pieceBForward) <= 90 - maxAngle;
	}

	/**
	 * Gets the full Transform of a Projector.
	 *
	 * @param segmentPiece The SegmentPiece
	 *
	 * @return The full transform of the Projector
	 */
	public static Transform getFaceTransform(SegmentPiece segmentPiece, Vector3i offset, Vector3i rotation, Transform out) {
		if(out == null) out = new Transform();
		out.setIdentity();
		segmentPiece.getTransform(out);
		ElementCollection.getPosFromIndex(segmentPiece.getAbsoluteIndex(), out.origin);
		out.origin.x -= SegmentData.SEG_HALF;
		out.origin.y -= SegmentData.SEG_HALF;
		out.origin.z -= SegmentData.SEG_HALF;
		float sNormalDir = 0.51f;
		float sVertical = 0.5f;
		float sHorizontal = 0.5f;
		out.origin.add(offset.toVector3f());
		Quat4f currentRot = new Quat4f();
		out.getRotation(currentRot);
		Quat4f addRot = new Quat4f();
		QuaternionUtil.setEuler(addRot, rotation.y / 100.0f, rotation.z / 100.0f, rotation.x / 100.0f);
		currentRot.mul(addRot);
		MathUtils.roundQuat(currentRot);
		out.setRotation(currentRot);
		int orientation = segmentPiece.getOrientation();
		switch(orientation) {
			case (Element.FRONT):
				out.basis.mul(mYC);
				out.origin.x -= sHorizontal;
				out.origin.y += sVertical;
				out.origin.z += sNormalDir;
				break;
			case (Element.BACK):
				out.origin.x += sHorizontal;
				out.origin.y += sVertical;
				out.origin.z -= sNormalDir;
				break;
			case (Element.TOP):
				out.basis.mul(mX);
				out.origin.x += sHorizontal;
				out.origin.y += sNormalDir;
				out.origin.z += sVertical;
				break;
			case (Element.BOTTOM):
				out.basis.mul(mYC);
				out.basis.mul(mXB);
				out.origin.x -= sHorizontal;
				out.origin.y -= sNormalDir;
				out.origin.z += sVertical;
				break;
			case (Element.RIGHT):
				out.basis.mul(mY);
				out.origin.x -= sNormalDir;
				out.origin.y += sVertical;
				out.origin.z -= sHorizontal;
				break;
			case (Element.LEFT):
				out.basis.mul(mYB);
				out.origin.x += sNormalDir;
				out.origin.y += sVertical;
				out.origin.z += sHorizontal;
				break;
		}
		segmentPiece.getSegmentController().getWorldTransform().transform(out.origin);
		return out;
	}

	public static ElementCollection<?, ?, ?> getElementCollectionFromPiece(SegmentPiece piece) {
		if(piece == null) return null;
		SegmentController segmentController = piece.getSegmentController();
		if(segmentController == null) return null;
		if(segmentController instanceof ManagedUsableSegmentController) {
			ManagedUsableSegmentController<?> managedUsableSegmentController = (ManagedUsableSegmentController<?>) segmentController;
			for(ElementCollectionManager collectionManager : SegmentControllerUtils.getAllCollectionManagers(managedUsableSegmentController)) {
				ElementCollectionManager<?, ?, ?> elementCollectionManager = (ElementCollectionManager<?, ?, ?>) collectionManager;
				for(ElementCollection<?, ?, ?> elementCollection : elementCollectionManager.getElementCollections()) {
					if(elementCollection.contains(piece.getAbsoluteIndex())) return elementCollection;
				}
			}
		}
		System.err.println("Could not find ElementCollection for SegmentPiece " + piece);
		return null;
	}

	public static SegmentController getEntityFromDbId(long entityId) {
		for(SegmentController controller : getSegmentControllers()) {
			if(controller.getDbId() == entityId) return controller;
		}
		return null;
	}

	private static ArrayList<SegmentController> getSegmentControllers() {
		ArrayList<SegmentController> controllers = new ArrayList<>();
		if(GameClient.getClientState() != null) {
			for(Sendable sendable : GameClient.getClientState().getLocalAndRemoteObjectContainer().getLocalObjects().values()) {
				if(sendable instanceof SegmentController) controllers.add((SegmentController) sendable);
			}
		} else {
			for(Sendable sendable : GameServer.getServerState().getLocalAndRemoteObjectContainer().getLocalObjects().values()) {
				if(sendable instanceof SegmentController) controllers.add((SegmentController) sendable);
			}
		}
		return controllers;
	}

	public static Vector4f getConnectedColor(SegmentPiece table) {
		if(table == null) return null;
		for(short s : ElementKeyMap.lightTypes) {
			if(api.utils.SegmentPieceUtils.getControlledPiecesMatching(table, s).size() > 0) {
				ElementInformation info = ElementKeyMap.getInfo(s);
				if(!info.isLightSource()) continue;
				return info.getLightSourceColor();
			}
		}
		return new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
