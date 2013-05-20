package ru.prolib.aquila.quik.dde;

import ru.prolib.aquila.core.EventDispatcher;
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
				new PartiallyKnownObjects(terminal),
				new OrdersCache(d, d.createType("Orders")),
				new TradesCache(d, d.createType("MyTrades")),
				new SecuritiesCache(d, d.createType("Securities")),
				new PortfoliosFCache(d, d.createType("PortfoliosFORTS")),
				new PositionsFCache(d, d.createType("PositionsFORTS")),
				new StopOrdersCache(d, d.createType("StopOrders")));
	}

}
