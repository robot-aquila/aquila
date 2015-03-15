package ru.prolib.aquila.quik.assembler.cache;

import ru.prolib.aquila.core.*;

/**
 * Конструктор кэша данных.
 */
public class CacheBuilder {
	
	public CacheBuilder() {
		super();
	}
	
	/**
	 * Создать DDE-кэш.
	 * <p>
	 * @param es фасад событийной системы
	 * @return кэш
	 */
	public Cache createCache(EventSystem es) {
		EventDispatcher d = es.createEventDispatcher("Cache");
		return new Cache(
				new DescriptorsCache(d, d.createType("Descriptors")),
				new PositionsCache(d, d.createType("Positions")),
				new OrdersCache(d, d.createType("Orders")),
				new OwnTradesCache(d, d.createType("OwnTrades")),
				new TradesCache(d, d.createType("Trades")));
	}

}
