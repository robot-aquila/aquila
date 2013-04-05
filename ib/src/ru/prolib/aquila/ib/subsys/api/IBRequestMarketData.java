package ru.prolib.aquila.ib.subsys.api;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.event.IBEventTick;

/**
 * Интерфейс запроса рыночных данных.
 * <p>
 * 2012-12-23<br>
 * $Id: IBRequestMarketData.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public interface IBRequestMarketData extends IBRequest {
	
	/**
	 * Получить тип события: в случае ошибки.
	 * <p>
	 * Данный тип позволяет отслеживать события типа {@link IBEventError}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnError();
	
	/**
	 * Получить тип события: при поступлении тиковых данных.
	 * <p>
	 * Данный тип позволяет отслеживать события типа {@link IBEventTick}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnTick();

}
