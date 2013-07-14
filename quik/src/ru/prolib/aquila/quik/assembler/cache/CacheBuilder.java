package ru.prolib.aquila.quik.assembler.cache;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Конструктор кэша.
 */
public class CacheBuilder {
	
	public CacheBuilder() {
		super();
	}
	
	/**
	 * Создать DDE-кэш для терминала.
	 * <p>
	 * @param terminal терминал
	 * @return кэш
	 */
	public Cache createCache(EditableTerminal terminal) {
		EventDispatcher d = terminal.getEventSystem()
			.createEventDispatcher("Cache");
		return new Cache(
				new DescriptorsCache(d, d.createType("Descriptors")),
				new PositionsCache(d, d.createType("Positions")),
				new OrdersCache(d, d.createType("Orders")));
	}

}
