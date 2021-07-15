package thederpgamer.decor.element.blocks.decor;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.element.FactoryResource;
import org.schema.schine.graphicsengine.core.GraphicsContext;
import thederpgamer.decor.element.blocks.Block;
import thederpgamer.decor.manager.LogManager;
import thederpgamer.decor.manager.ResourceManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/15/2021
 */
public class TextProjector  extends Block {

    public TextProjector() {
        super("Text Projector", ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getType());
    }

    @Override
    public void initialize() {
        if(GraphicsContext.initialized) {
            try {
                blockInfo.setTextureId(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getTextureIds());
                blockInfo.setTextureId(0, (short) ResourceManager.getTexture("text-projector-front").getTextureId());
                blockInfo.setBuildIconNum(ResourceManager.getTexture("text-projector-icon").getTextureId());
            } catch(Exception exception) {
                LogManager.logException("Encountered an exception while trying to load textures for Text Projector! This will result in missing textures in-game!", exception);
            }
        }
        blockInfo.setInRecipe(true);
        blockInfo.setShoppable(true);
        blockInfo.setCanActivate(true);
        blockInfo.setPrice(ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).price);
        blockInfo.setOrientatable(true);

        blockInfo.controlledBy.add((short) 405);
        blockInfo.controlledBy.add((short) 993);
        blockInfo.controlledBy.add((short) 666);
        blockInfo.controlledBy.add((short) 399);

        ElementKeyMap.getInfo(405).controlling.add(getId());
        ElementKeyMap.getInfo(993).controlling.add(getId());
        ElementKeyMap.getInfo(666).controlling.add(getId());
        ElementKeyMap.getInfo(399).controlling.add(getId());

        BlockConfig.addRecipe(blockInfo, ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getProducedInFactoryType(), (int) ElementKeyMap.getInfo(ElementKeyMap.TEXT_BOX).getFactoryBakeTime(),
                new FactoryResource(1, ElementKeyMap.TEXT_BOX),
                new FactoryResource(50, (short) 440)
        );
        BlockConfig.add(blockInfo);
    }
}