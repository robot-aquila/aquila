package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Диспетчер событий портфеля.
 * <p>
 * Диспетчер абстрагирует портфель от набора задействованных типов событий.
 * Имеет фиксированную внутреннюю структуру (создается при инстанцировании), что
 * позволяет избегать комплексных операций проверки элементов событийной системы
 * в рамках портфеля. Так же предоставляет интерфейс для генерации конкретных
 * событий.
 */
public class PortfolioEventDispatcher {
	private final EventDispatcher dispatcher;
	private final EventType onChanged;
	
	public PortfolioEventDispatcher(EventSystem es, Account account) {
		super();
		dispatcher = es.createEventDispatcher("Portfolio[" + account + "]");
		onChanged = dispatcher.createType("Changed");
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
	 * Получить тип события: при изменении атрибутов портфеля.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged() {
		return onChanged;
	}
	
	/**
	 * Генератор события: при изменении атрибутов портфеля.
	 * <p>
	 * @param portfolio экземпляр портфеля
	 */
	public void fireChanged(Portfolio portfolio) {
		dispatcher.dispatch(new PortfolioEvent(onChanged, portfolio));
	}

}
