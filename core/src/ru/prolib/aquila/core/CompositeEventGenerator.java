package ru.prolib.aquila.core;

import java.util.LinkedHashMap;

/**
 * Интерфейс генератора события при срабатывании правила генерации.
 * <p>
 * 2012-04-29<br>
 * $Id: CompositeEventGenerator.java 219 2012-05-20 12:16:45Z whirlwind $
 */
@Deprecated
public interface CompositeEventGenerator {
	
	/**
	 * Сгенерировать событие.
	 * <p>
	 * Метод вызывается в случае соответствия состояния композитного типа
	 * условию генерации композитного события.
	 * <p>
	 * @param type тип события
	 * @param state состояние на момент определения условия
	 * @param event событие-инициатор генерации
	 * @return экземпляр нового события, которое должно быть отправлено
	 * наблюдателям
	 */
	public Event generateEvent(CompositeEventType type,
							   LinkedHashMap<EventType, Event> state,
							   Event event);

}
