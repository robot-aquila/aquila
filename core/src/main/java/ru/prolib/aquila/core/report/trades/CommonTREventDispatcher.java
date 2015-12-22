package ru.prolib.aquila.core.report.trades;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.report.*;

/**
 * Диспетчер событий списка трейд-отчетов.
 * <p>
 * Диспетчер абстрагирует список трейд-отчетов от набора задействованных типов
 * событий. Имеет фиксированную внутреннюю структуру (создается при
 * инстанцировании), что позволяет избегать комплексных операций проверки
 * элементов событийной системы в рамках набора. Так же предоставляет интерфейс
 * для генерации конкретных событий.
 */
public class CommonTREventDispatcher {
	private final EventDispatcher dispatcher;
	private final EventType onEnter, onExit, onChanged;
	
	public CommonTREventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher("Report");
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
	 * @param index индекс отчета в списке
	 */
	public void fireEnter(RTrade report, int index) {
		dispatcher.dispatch(new TradeReportEvent(onEnter, report, index));
	}
	
	/**
	 * Генератор события: выход из трейда.
	 * <p>
	 * @param report трейд-отчет
	 * @param index индекс отчета в списке
	 */
	public void fireExit(RTrade report, int index) {
		dispatcher.dispatch(new TradeReportEvent(onExit, report, index));
	}
	
	/**
	 * Генератор события: изменение трейда.
	 * <p>
	 * @param report трейд-отчет
	 * @param index индекс отчета в списке
	 */
	public void fireChanged(RTrade report, int index) {
		dispatcher.dispatch(new TradeReportEvent(onChanged, report, index));
	}

}
