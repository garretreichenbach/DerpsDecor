package thederpgamer.decor.element.blocks;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.game.common.data.element.ElementInformation;
import thederpgamer.decor.DerpsDecor;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 04/25/2021
 */
public abstract class Block {
	protected ElementInformation blockInfo;

	public Block(String name, ElementCategory category) {
		blockInfo = BlockConfig.newElement(DerpsDecor.getInstance(), name, new short[6]);
		BlockConfig.setElementCategory(blockInfo, category);
	}

	public final ElementInformation getBlockInfo() {
		return blockInfo;
	}

	public final short getId() {
		return blockInfo.getId();
	}

	public abstract void initialize();
}
