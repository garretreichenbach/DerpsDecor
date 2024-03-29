package thederpgamer.decor.utils;

import api.utils.game.module.ModManagerContainerModule;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.SpaceStation;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.world.SimpleTransformableSendableObject;
import thederpgamer.decor.systems.modules.HoloProjectorModule;
import thederpgamer.decor.systems.modules.HoloTableModule;
import thederpgamer.decor.systems.modules.TextProjectorModule;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 08/26/2021
 */
public class ProjectorUtils {
	public static Object getDrawData(SegmentPiece segmentPiece) {
		SegmentController segmentController = segmentPiece.getSegmentController();
		ManagerContainer<?> managerContainer = null;
		if(segmentController.getType() == SimpleTransformableSendableObject.EntityType.SHIP) managerContainer = ((Ship) segmentController).getManagerContainer();
		else if(segmentController.getType() == SimpleTransformableSendableObject.EntityType.SPACE_STATION) managerContainer = ((SpaceStation) segmentController).getManagerContainer();
		if(managerContainer != null) {
			ModManagerContainerModule module = managerContainer.getModMCModule(segmentPiece.getType());
			if(module instanceof HoloProjectorModule) return ((HoloProjectorModule) module).getDrawData(segmentPiece);
			else if(module instanceof TextProjectorModule) return ((TextProjectorModule) module).getDrawData(segmentPiece);
			else if(module instanceof HoloTableModule) return ((HoloTableModule) module).getDrawData(segmentPiece);
		}
		return null;
	}

	public static Object getModule(SegmentPiece segmentPiece) {
		SegmentController segmentController = segmentPiece.getSegmentController();
		ManagerContainer<?> managerContainer = null;
		if(segmentController.getType() == SimpleTransformableSendableObject.EntityType.SHIP) managerContainer = ((Ship) segmentController).getManagerContainer();
		else if(segmentController.getType() == SimpleTransformableSendableObject.EntityType.SPACE_STATION) managerContainer = ((SpaceStation) segmentController).getManagerContainer();
		if(managerContainer != null) return managerContainer.getModMCModule(segmentPiece.getType());
		else return null;
	}
}
