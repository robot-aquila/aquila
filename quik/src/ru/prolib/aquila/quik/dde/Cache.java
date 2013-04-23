package ru.prolib.aquila.quik.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;

/**
 * Фасад подсистемы кэша DDE.
 */
public class Cache {
	private final OrdersCache orders;
	
	public Cache(OrdersCache orders) {
		super();
		this.orders = orders;
	}
	
	/**
	 * Создать экземпляр кэша DDE.
	 * <p>
	 * @param es фасад событийной системы
	 * @return кэш DDE
	 */
	public static Cache createCache(EventSystem es) {
		EventDispatcher dispatcher = es.createEventDispatcher("Cache");
		return new Cache(new OrdersCache(dispatcher,
				es.createGenericType(dispatcher, "Orders")));
	}
	
	/**
	 * Получить кэш таблицы заявок.
	 * <p>
	 * @return кэш
	 */
	public OrdersCache getOrdersCache() {
		return orders;
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
			.isEquals();
	}

}
