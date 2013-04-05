package ru.prolib.aquila.ib.subsys.api;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEventError;

/**
 * Интерфейс запроса деталей контракта.
 * <p>
 * 2012-11-27<br>
 * $Id: IBRequestContract.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public interface IBRequestContract extends IBRequest {
	
	/**
	 * Получить тип события: в случае ошибки.
	 * <p>
	 * Данный тип позволяет отслеживать события типа {@link IBEventError}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnError();
	
	/**
	 * Получить тип события: при поступлении ответа на запрос.
	 * <p>
	 * @return тип события
	 */
	public EventType OnResponse();
	
}
