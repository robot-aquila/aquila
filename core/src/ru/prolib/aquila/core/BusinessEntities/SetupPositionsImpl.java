package ru.prolib.aquila.core.BusinessEntities;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Набор спецификаций позиций.
 * <p>
 * 2013-01-11<br>
 * $Id: SetupPositionsImpl.java 406 2013-01-11 10:08:56Z whirlwind $
 */
public class SetupPositionsImpl implements SetupPositions {
	private final Map<SecurityDescriptor, SetupPositionImpl> map;
	
	/**
	 * Конструктор.
	 */
	public SetupPositionsImpl() {
		super();
		map = new LinkedHashMap<SecurityDescriptor, SetupPositionImpl>();
	}

	@Override
	public synchronized SetupPosition getPosition(SecurityDescriptor descr) {
		SetupPositionImpl setup = map.get(descr);
		if ( setup == null ) {
			setup = new SetupPositionImpl(descr);
			map.put(descr, setup);
		}
		return setup;
	}

	@Override
	public synchronized void removePosition(SecurityDescriptor descr) {
		map.remove(descr);
	}

	@Override
	public synchronized List<SetupPosition> getPositions() {
		return new Vector<SetupPosition>(map.values());
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SetupPositionsImpl.class ) {
			return false;
		}
		SetupPositionsImpl o = (SetupPositionsImpl) other;
		return map.equals(o.map);
	}
	
	@Override
	public synchronized SetupPositionsImpl clone() {
		SetupPositionsImpl copy = new SetupPositionsImpl();
		Iterator<Map.Entry<SecurityDescriptor, SetupPositionImpl>> iterator;
		Map.Entry<SecurityDescriptor, SetupPositionImpl> entry;
		iterator = map.entrySet().iterator();
		while ( iterator.hasNext() ) {
			entry = iterator.next();
			copy.map.put(entry.getKey(), entry.getValue().clone());
		}
		return copy;
	}

}
