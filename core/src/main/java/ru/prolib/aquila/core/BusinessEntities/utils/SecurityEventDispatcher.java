package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityTradeEvent;
import ru.prolib.aquila.core.BusinessEntities.Trade;

/**
 * Диспетчер событий инструмента.
 * <p>
 * Диспетчер абстрагирует инструмент от набора задействованных типов событий.
 * Имеет фиксированную внутреннюю структуру (создается при инстанцировании), что
 * позволяет избегать комплексных операций проверки элементов событийной системы
 * в рамках инструмента. Так же предоставляет интерфейс для генерации конкретных
 * событий.
 */
public class SecurityEventDispatcher {
	private final EventDispatcher dispatcher;
	private final EventTypeSI onChanged, onTrade;
	
	public SecurityEventDispatcher(EventSystem es, Symbol symbol) {
		super();
		dispatcher = es.createEventDispatcher("Security[" + symbol + "]");
		onChanged = dispatcher.createType("Changed");
		onTrade = dispatcher.createType("Trade");
	}
	
	/**
	 * Получить тип события: при изменении атрибутов инструмента.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged() {
		return onChanged;
	}
	
	/**
	 * Получить тип события: анонимная сделка по инструменту.
	 * <p>
	 * @return тип события
	 */
	public EventType OnTrade() {
		return onTrade;
	}
	
	/**
	 * Получить подчиненный диспетчер событий.
	 * <p>
	 * Служебный метод. Только для тестов.
	 * <p>
	 * @return диспетчер событий
	 */
	EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Генератор события: при изменении инструмента.
	 * <p>
	 * @param security инструмент
	 */
	public void fireChanged(Security security) {
		dispatcher.dispatch(new SecurityEvent(onChanged, security));
	}
	
	/**
	 * Генератор события: новая сделка по инструменту.
	 * <p>
	 * @param security инструмент
	 * @param trade экземпляр сделки
	 */
	public void fireTrade(Security security, Trade trade) {
		dispatcher.dispatch(new SecurityTradeEvent(onTrade, security, trade));
	}

}
