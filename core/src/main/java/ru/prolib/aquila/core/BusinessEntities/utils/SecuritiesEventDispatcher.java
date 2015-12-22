package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Диспетчер событий набора инструментов.
 * <p>
 * Диспетчер абстрагирует набор инструментов от набора задействованных типов
 * событий. Имеет фиксированную внутреннюю структуру (создается при
 * инстанцировании), что позволяет избегать комплексных операций проверки
 * элементов событийной системы в рамках набора. Так же предоставляет интерфейс
 * для генерации конкретных событий и выполняет ретрансляцию событий подчиненных
 * позиций.
 * <p>
 * См. примечания {@link OrdersEventDispatcher}.
 */
public class SecuritiesEventDispatcher implements EventListener {
	private final EventDispatcher dispatcher;
	private final EventType onAvailable, onChanged, onTrade;
	
	public SecuritiesEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher("Securities");
		onAvailable = dispatcher.createType("Available");
		onChanged = dispatcher.createType("Changed");
		onTrade = dispatcher.createType("Trade");
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
	 * Получить тип события: доступен новый инструмент.
	 * <p>
	 * @return тип события
	 */
	public EventType OnAvailable() {
		return onAvailable;
	}
	
	/**
	 * Получить тип события: изменение атрибутов инструмента.
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
	 * Генератор события: доступен новый инструмент.
	 * <p>
	 * @param security экземпляр инструмента
	 */
	public void fireAvailable(Security security) {
		dispatcher.dispatch(new SecurityEvent(onAvailable, security));
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof SecurityTradeEvent ) {
			SecurityTradeEvent e = (SecurityTradeEvent) event;
			dispatcher.dispatch(new SecurityTradeEvent(onTrade, e.getSecurity(),
					e.getTrade()));
		} else if ( event instanceof SecurityEvent ) {
			SecurityEvent e = (SecurityEvent) event;
			Security security = e.getSecurity();
			if ( e.isType(security.OnChanged()) ) {
				dispatcher.dispatch(new SecurityEvent(onChanged, security));
			}
		}
	}
	
	/**
	 * Начать ретрансляцию событий инструмента.
	 * <p>
	 * @param security инструмент
	 */
	public void startRelayFor(Security security) {
		security.OnChanged().addSyncListener(this);
		security.OnTrade().addSyncListener(this);
	}
	
	/**
	 * Прекратить ретрансляцию событий инструмента.
	 * <p>
	 * @param security инструмент
	 */
	public void stopRelayFor(Security security) {
		security.OnChanged().removeListener(this);
		security.OnTrade().removeListener(this);
	}

}
