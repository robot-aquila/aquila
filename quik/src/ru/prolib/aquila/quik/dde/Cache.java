package ru.prolib.aquila.quik.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;

/**
 * Фасад подсистемы кэша DDE.
 */
public class Cache {
	private final OrdersCache orders;
	private final TradesCache trades;
	private final SecuritiesCache securities;
	
	public Cache(OrdersCache orders, TradesCache trades,
			SecuritiesCache securities)
	{
		super();
		this.orders = orders;
		this.trades = trades;
		this.securities = securities;
	}
	
	/**
	 * Создать экземпляр кэша DDE.
	 * <p>
	 * @param es фасад событийной системы
	 * @return кэш DDE
	 */
	public static Cache createCache(EventSystem es) {
		EventDispatcher dispatcher = es.createEventDispatcher("Cache");
		return new Cache(
				new OrdersCache(dispatcher,
						es.createGenericType(dispatcher, "Orders")),
				new TradesCache(dispatcher,
						es.createGenericType(dispatcher, "MyTrades")),
				new SecuritiesCache(dispatcher,
						es.createGenericType(dispatcher, "Securities")));
	}
	
	/**
	 * Получить кэш таблицы заявок.
	 * <p>
	 * @return кэш
	 */
	public synchronized OrdersCache getOrdersCache() {
		return orders;
	}
	
	/**
	 * Получить кэш таблицы собственных сделок.
	 * <p>
	 * @return кэш
	 */
	public synchronized TradesCache getTradesCache() {
		return trades;
	}
	
	/**
	 * Получить кэш таблицы инструментов.
	 * <p>
	 * @return кэш
	 */
	public synchronized SecuritiesCache getSecuritiesCache() {
		return securities;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( other.getClass() != Cache.class ) {
			return false;
		}
		Cache o = (Cache) other;
		return new EqualsBuilder()
			.append(orders, o.orders)
			.append(trades, o.trades)
			.append(securities, o.securities)
			.isEquals();
	}

}
