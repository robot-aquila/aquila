package ru.prolib.aquila.core.report.trades;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.report.*;

/**
 * Диспетчер событий набора открытых трейдов.
 * <p>
 * Диспетчер абстрагирует набор текущих открытых отчетов от набора
 * задействованных типов событий. Имеет фиксированную внутреннюю структуру
 * (создается при инстанцировании), что позволяет избегать комплексных операций
 * проверки элементов событийной системы в рамках набора. Так же предоставляет
 * интерфейс для генерации конкретных событий.
 */
public class ActiveTradesEventDispatcher {
	private static final String ID = "ActiveTrades";
	private final EventDispatcher dispatcher;
	private final EventTypeSI onEnter, onExit, onChanged;
	
	public ActiveTradesEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher(ID);
		onEnter = dispatcher.createSyncType("Enter");
		onExit = dispatcher.createSyncType("Exit");
		onChanged = dispatcher.createSyncType("Changed");
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
	 * Получить тип события: вход в новый трейд.
	 * <p>
	 * @return тип события
	 */
	public EventType OnEnter() {
		return onEnter;
	}
	
	/**
	 * Получить тип события: выход из трейда.
	 * <p>
	 * @return тип события
	 */
	public EventType OnExit() {
		return onExit;
	}
	
	/**
	 * Получить тип события: изменение трейда.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged() {
		return onChanged;
	}
	
	/**
	 * Генератор события: вход в трейд.
	 * <p>
	 * @param report трейд-отчет
	 */
	public void fireEnter(RTrade report) {
		dispatcher.dispatch(new TradeReportEvent(onEnter, report));
	}
	
	/**
	 * Генератор события: выход из трейда.
	 * <p>
	 * @param report трейд-отчет
	 */
	public void fireExit(RTrade report) {
		dispatcher.dispatch(new TradeReportEvent(onExit, report));
	}
	
	/**
	 * Генератор события: изменение трейда.
	 * <p>
	 * @param report трейд-отчет
	 */
	public void fireChanged(RTrade report) {
		dispatcher.dispatch(new TradeReportEvent(onChanged, report));
	}

}
