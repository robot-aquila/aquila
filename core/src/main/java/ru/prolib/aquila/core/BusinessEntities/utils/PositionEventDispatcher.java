package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Диспетчер событий позиции.
 * <p>
 * Диспетчер абстрагирует позицию от набора задействованных типов событий.
 * Имеет фиксированную внутреннюю структуру (создается при инстанцировании), что
 * позволяет избегать комплексных операций проверки элементов событийной системы
 * в рамках позиции. Так же предоставляет интерфейс для генерации конкретных
 * событий.
 */
public class PositionEventDispatcher {
	private final EventDispatcher dispatcher;
	private final EventTypeSI onChanged;
	
	public PositionEventDispatcher(EventSystem es, Account account, Symbol symbol) {
		super();
		dispatcher = es.createEventDispatcher("Position[" + account + ":" + symbol + "]");
		onChanged = dispatcher.createType("Changed");
	}
	
	/**
	 * Получить тип события: при изменении атрибутов позиции.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged() {
		return onChanged;
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
	 * Генератор события: при изменении позиции.
	 * <p>
	 * @param position экземпляр позиции
	 */
	public void fireChanged(Position position) {
		dispatcher.dispatch(new PositionEvent(onChanged, position));
	}

}
