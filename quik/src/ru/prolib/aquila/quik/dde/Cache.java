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
	private final PortfoliosFCache portfolios_F;
	private final PositionsFCache positions_F;
	private final StopOrdersCache stopOrders;
	
	public Cache(OrdersCache orders, TradesCache trades,
			SecuritiesCache securities,
			PortfoliosFCache portfolios_F,
			PositionsFCache positions_F,
			StopOrdersCache stopOrders)
	{
		super();
		this.orders = orders;
		this.trades = trades;
		this.securities = securities;
		this.portfolios_F = portfolios_F;
		this.positions_F = positions_F;
		this.stopOrders = stopOrders;
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
				es.createGenericType(dispatcher, "Securities")),
			new PortfoliosFCache(dispatcher,
				es.createGenericType(dispatcher, "PortfoliosFORTS")),
			new PositionsFCache(dispatcher,
				es.createGenericType(dispatcher, "PositionsFORTS")),
			new StopOrdersCache(dispatcher,
				es.createGenericType(dispatcher, "StopOrders")));
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
	
	/**
	 * Получить кэш таблицы портфелей ФОРТС.
	 * <p>
	 * @return кэш
	 */
	public synchronized PortfoliosFCache getPortfoliosFCache() {
		return portfolios_F;
	}
	
	/**
	 * Получить кэш таблицы позиций по деривативам.
	 * <p>
	 * @return кэш
	 */
	public synchronized PositionsFCache getPositionsFCache() {
		return positions_F;
	}
	
	/**
	 * Получить кэш таблицы стоп-заявок.
	 * <p>
	 * @return кэш
	 */
	public synchronized StopOrdersCache getStopOrdersCache() {
		return stopOrders;
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
			.append(portfolios_F, o.portfolios_F)
			.append(positions_F, o.positions_F)
			.append(stopOrders, o.stopOrders)
			.isEquals();
	}

}
