package thederpgamer.decor.element.blocks.inventory;

import api.config.BlockConfig;
import api.listener.events.block.SegmentPieceActivateEvent;
import org.schema.game.client.view.cubes.shapes.BlockStyle;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.inventory.InventoryHolder;
import org.schema.game.common.data.player.inventory.StashInventory;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.blocks.InventoryBlock;
import thederpgamer.decor.manager.ResourceManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/15/2022]
 */
public class StorageCapsule extends InventoryBlock {
	public StorageCapsule() {
		super("Storage Capsule", ElementKeyMap.getInfo(120).getType());
	}

	@Override
	public void initialize() {
		blockInfo.setDescription(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getDescription());
		blockInfo.setCanActivate(true);
		blockInfo.setInRecipe(true);
		blockInfo.setShoppable(true);
		blockInfo.setPrice(ElementKeyMap.getInfo(120).price);
		blockInfo.setOrientatable(true);
		blockInfo.setIndividualSides(6);
		blockInfo.setBlockStyle(BlockStyle.NORMAL24.id);
		blockInfo.lodShapeStyle = 1;
		blockInfo.sideTexturesPointToOrientation = false;
		blockInfo.controlling.addAll(ElementKeyMap.getInfo(120).controlling);
		blockInfo.controlledBy.addAll(ElementKeyMap.getInfo(120).controlledBy);
		for(short id : ElementKeyMap.getInfo(120).controlledBy) blockInfo.controlledBy.add(id);
		for(short id : ElementKeyMap.getInfo(120).controlling) blockInfo.controlling.add(id);
		if(GraphicsContext.initialized) {
			try {
				blockInfo.setBuildIconNum(ResourceManager.getTexture("storage-capsule-icon").getTextureId());
				BlockConfig.assignLod(blockInfo, DerpsDecor.getInstance(), "storage_capsule_closed", "storage_capsule_open");
			} catch(Exception ignored) {}
		}
		BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(120).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(120).getFactoryBakeTime(), new FactoryResource(1, (short) 120), new FactoryResource(1, (short) 976));
		BlockConfig.add(blockInfo);
	}

	@Override
	public Inventory createInventory(InventoryHolder holder, SegmentPiece segmentPiece) {
		return new StashInventory(holder, segmentPiece.getAbsoluteIndex());
	}

	@Override
	public void onLogicActivation(SegmentPieceActivateEvent event) {
	}
}
