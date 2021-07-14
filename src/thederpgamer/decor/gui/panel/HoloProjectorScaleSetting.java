package thederpgamer.decor.gui.panel;

import org.schema.game.client.controller.manager.ingame.AbstractSizeSetting;
import thederpgamer.decor.manager.ConfigManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 07/09/2021
 */
public class HoloProjectorScaleSetting extends AbstractSizeSetting {

    public HoloProjectorScaleSetting() {
        set(1);
    }

    @Override
    public int getMin() {
        return 1;
    }

    @Override
    public int getMax() {
        return ConfigManager.getMainConfig().getInt("max-image-scale");
    }
}
