package thederpgamer.decor.utils;

import api.common.GameClient;
import api.common.GameCommon;
import thederpgamer.decor.DerpsDecor;
import thederpgamer.decor.manager.LogManager;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 06/09/2021
 */
public class DataUtils {

	public static String getResourcesPath() {
		return DerpsDecor.getInstance().getSkeleton().getResourcesFolder().getPath().replace('\\', '/');
	}

	public static String getWorldDataPath() {
		String universeName = GameCommon.getUniqueContextId();
		if(!universeName.contains(":")) {
			return getResourcesPath() + "/data/" + universeName;
		} else {
			try {
				LogManager.logMessage(MessageType.ERROR, "Client " + GameClient.getClientPlayerState().getName() + " attempted to illegally access server data.");
			} catch(Exception ignored) {
			}
			return null;
		}
	}
}
