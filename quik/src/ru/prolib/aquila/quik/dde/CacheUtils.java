package ru.prolib.aquila.quik.dde;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Методы-утилиты кэша DDE.
 */
public class CacheUtils {
	private final Cache cache;
	private final OrderFactory orderFactory;
	private final TradeFactory tradeFactory;
	
	public CacheUtils(Cache cache, OrderFactory orderFactory,
			TradeFactory tradeFactory)
	{
		super();
		this.cache = cache;
		this.orderFactory = orderFactory;
		this.tradeFactory = tradeFactory;
	}
	
	/**
	 * Создать заявку, на основании кэш записи заявки.
	 * <p>
	 * @param orderCache кэш-запись таблицы заявок
	 * @return экземпляр заявки
	 */
	public EditableOrder createOrder(OrderCache orderCache) {
		EditableOrder order = orderFactory.createOrder();
		order.setId(orderCache.getId());
		
		return null;
	}

}
