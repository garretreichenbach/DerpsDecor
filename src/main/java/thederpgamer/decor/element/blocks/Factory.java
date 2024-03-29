package thederpgamer.decor.element.blocks;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.game.common.data.element.ElementInformation;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.element.ElementManager;
import thederpgamer.decor.manager.ResourceManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 04/26/2021
 */
public abstract class Factory {
	protected ElementInformation blockInfo;

	public Factory(String name, ElementCategory category, String... sideNames) {
		short[] textureIds = new short[6];
		String replace = name.toLowerCase().trim().replace(" ", "-");
		int i;
		for(i = 0; i < textureIds.length && i < sideNames.length; i++) {
			String sideName = sideNames[i].toLowerCase().trim().replace(" ", "-");
			String textureName = replace + "-" + sideName;
			textureIds[i] = (short) ResourceManager.getTexture(textureName).getTextureId();
		}
		if(i < 5) {
			for(int j = 0; i < textureIds.length && j < sideNames.length; i++) {
				String sideName = sideNames[j].toLowerCase().trim().replace(" ", "-");
				String textureName = replace + "-" + sideName;
				textureIds[i] = (short) ResourceManager.getTexture(textureName).getTextureId();
				j++;
			}
		}
		blockInfo = BlockConfig.newFactory(DerpsDecor.getInstance(), name, textureIds);
		BlockConfig.setElementCategory(blockInfo, category);
		ElementManager.addFactory(this);
	}

	public final ElementInformation getBlockInfo() {
		return blockInfo;
	}

	public final short getId() {
		return blockInfo.getId();
	}

	public abstract void initialize();
}
