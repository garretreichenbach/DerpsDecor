package thederpgamer.decor.data.system.strut;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @version 1.0 - [03/16/2022]
 */
public class StrutSystemData {
	public ConcurrentHashMap<MutablePair<Long, Long>, StrutData> map;

	public StrutSystemData() {
		map = new ConcurrentHashMap<>();
	}
}
