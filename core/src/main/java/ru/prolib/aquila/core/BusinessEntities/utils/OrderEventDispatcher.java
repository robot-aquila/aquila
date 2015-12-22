package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Диспетчер событий заявки.
 * <p>
 * Диспетчер абстрагирует заявку от набора задействованных типов событий.
 * Имеет фиксированную внутреннюю структуру (создается при инстанцировании), что
 * позволяет избегать комплексных операций проверки элементов событийной системы
 * в рамках заявки. Так же предоставляет интерфейс для генерации конкретных
 * событий.
 */
public class OrderEventDispatcher {
	private final EventDispatcher dispatcher;
	private final EventType onRegistered, onRegisterFailed, onCancelled,
		onCancelFailed, onFilled, onPartiallyFilled, onChanged, onDone,
		onFailed, onTrade;
	
	public OrderEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher("Order");
		onRegistered = dispatcher.createType("Registered");
		onRegisterFailed = dispatcher.createType("RegisterFailed");
		onCancelled = dispatcher.createType("Cancelled");
		onCancelFailed = dispatcher.createType("CancelFailed");
		onFilled = dispatcher.createType("Filled");
		onPartiallyFilled = dispatcher.createType("PartiallyFilled");
		onChanged = dispatcher.createType("Changed");
		onDone = dispatcher.createType("Done");
		onFailed = dispatcher.createType("Failed");
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
	 * Получить тип события: заявка зарегистрирована в торговой системе.
	 * <p> 
	 * @return тип события
	 */
	public EventType OnRegistered() {
		return onRegistered;
	}
	
	/**
	 * Получить тип события: ошибка регистрации заявки.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRegisterFailed() {
		return onRegisterFailed;
	}
	
	/**
	 * Получить тип события: отмена заявки.
	 * <p>
	 * @return тип события
	 */
	public EventType OnCancelled() {
		return onCancelled;
	}
	
	/**
	 * Получить тип события: ошибка отмены заявки.
	 * <p>
	 * @return тип события
	 */
	public EventType OnCancelFailed() {
		return onCancelFailed;
	}
	
	/**
	 * Получить тип события: заявка полностью исполнена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnFilled() {
		return onFilled;
	}
	
	/**
	 * Получить тип события: заявка исполнена частично.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPartiallyFilled() {
		return onPartiallyFilled;
	}
	
	/**
	 * Получить тип события: изменение атрибутов заявки.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged() {
		return onChanged;
	}
	
	/**
	 * Получить тип события: работа по заявке завершена (финализация).
	 * <p>
	 * @return тип события
	 */
	public EventType OnDone() {
		return onDone;
	}
	
	/**
	 * Получить тип события: ошибка регистрации или отмены.
	 * <p>
	 * @return тип события
	 */
	public EventType OnFailed() {
		return onFailed;
	}
	
	/**
	 * Получить тип события: новая сделка по заявке.
	 * <p> 
	 * @return тип события
	 */
	public EventType OnTrade() {
		return onTrade;
	}
	
	/**
	 * Генератор события: сделка по заявке.
	 * <p>
	 * @param order заявка
	 * @param trade экземпляр сделки
	 */
	public void fireTrade(Order order, Trade trade) {
		dispatcher.dispatch(new OrderTradeEvent(onTrade, order, trade));
	}
	
	/**
	 * Очистить списки наблюдателей всех типов событий. 
	 */
	public void removeListeners() {
		onRegistered.removeListeners();
		onRegisterFailed.removeListeners();
		onCancelled.removeListeners();
		onCancelFailed.removeListeners();
		onFilled.removeListeners();
		onPartiallyFilled.removeListeners();
		onChanged.removeListeners();
		onDone.removeListeners();
		onFailed.removeListeners();
		onTrade.removeListeners();
	}
	
	/**
	 * Генератор произвольного события.
	 * <p>
	 * Делегат к подчененному диспетчеру событий.
	 * <p>
	 * @param event экземпляр события
	 */
	public void dispatch(Event event) {
		dispatcher.dispatch(event);
	}
	
}
 