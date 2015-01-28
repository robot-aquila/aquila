package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Диспетчер событий набора позиций.
 * <p>
 * Диспетчер абстрагирует набор от набора задействованных типов событий.
 * Имеет фиксированную внутреннюю структуру (создается при инстанцировании), что
 * позволяет избегать комплексных операций проверки элементов событийной системы
 * в рамках набора. Так же предоставляет интерфейс для генерации конкретных
 * событий и выполняет ретрансляцию событий подчиненных позиций.
 * <p>
 * <i>2014-04-09 Архитектурная проблема</i> (см. одноименный параграф в
 * документации к {@link OrdersEventDispatcher}).
 * <p>
 */
public class PositionsEventDispatcher implements EventListener {
	private final EventDispatcher dispatcher, sync_disp;
	private final EventType onAvailable, onChanged;
	
	public PositionsEventDispatcher(EventSystem es, Account account) {
		super();
		String id = "Positions[" + account + "]";
		dispatcher = es.createEventDispatcher(id);
		onAvailable = dispatcher.createType("Available");
		sync_disp = createSyncDispatcher(id);
		onChanged = sync_disp.createType("Changed");
	}
	
	private final EventDispatcher createSyncDispatcher(String id) {
		 return new EventDispatcherImpl(new SimpleEventQueue(), id);
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
	 * Получить тип события: доступна новая позиция.
	 * <p>
	 * @return тип события
	 */
	public EventType OnAvailable() {
		return onAvailable;
	}
	
	/**
	 * Получить тип события: при изменении позиции из набора.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged() {
		return onChanged;
	}
	
	/**
	 * Генератор события: доступна новая позицияs.
	 * <p>
	 * @param position экземпляр позиции
	 */
	public void fireAvailable(Position position) {
		dispatcher.dispatch(new PositionEvent(onAvailable, position));
	}
	
	/**
	 * Ретранслирует события позиции.
	 */
	@Override
	public void onEvent(Event event) {
		Position position = ((PositionEvent) event).getPosition();
		sync_disp.dispatch(new PositionEvent(onChanged, position));
	}

}
