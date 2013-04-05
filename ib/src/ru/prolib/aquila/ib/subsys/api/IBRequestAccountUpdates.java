package ru.prolib.aquila.ib.subsys.api;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс запроса: подписка на обновления счета.
 * <p>
 * @link <a href="http://www.interactivebrokers.com/en/software/api/apiguide/java/updateportfolio.htm">reqAccountUpdates</a>
 * <p>
 * 2012-11-27<br>
 * $Id: IBRequestAccountUpdates.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public interface IBRequestAccountUpdates extends IBRequest {

	/**
	 * Получить тип события: обновление атрибута счета.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUpdateAccount();
	
	/**
	 * Получить тип события: обновления позиции.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUpdatePortfolio();

}
