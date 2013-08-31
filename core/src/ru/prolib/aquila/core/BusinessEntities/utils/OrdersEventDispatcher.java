package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Диспетчер событий набора заявок.
 * <p>
 * Диспетчер абстрагирует набор от набора задействованных типов событий.
 * Имеет фиксированную внутреннюю структуру (создается при инстанцировании), что
 * позволяет избегать комплексных операций проверки элементов событийной системы
 * в рамках набора. Так же предоставляет интерфейс для генерации конкретных
 * событий и выполняет ретрансляцию событий подчиненных позиций.
 */
public class OrdersEventDispatcher implements EventListener {
	private final EventDispatcher dispatcher;
	private final EventType onRegistered, onRegisterFailed, onCancelled,
		onCancelFailed, onFilled, onPartiallyFilled, onChanged, onDone,
		onFailed, onTrade, onAvailable;
	
	public OrdersEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher("Orders");
		onAvailable = dispatcher.createType("Available");
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
	 * Получить тип события: доступна новая заявка.
	 * <p>
	 * @return тип события
	 */
	public EventType OnAvailable() {
		return onAvailable;
	}
	
	/**
	 * Генератор события: доступна новая заявка.
	 * <p>
	 * @param order заявка
	 */
	public void fireAvailable(Order order) {
		dispatcher.dispatch(new OrderEvent(onAvailable, order));
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof OrderTradeEvent ) {
			OrderTradeEvent e = (OrderTradeEvent) event;
			dispatcher.dispatch(new OrderTradeEvent(onTrade, e.getOrder(),
					e.getTrade()));
		} else if ( event instanceof OrderEvent ) {
			Order order = ((OrderEvent) event).getOrder();
			EventType map[][] = {
					{ order.OnRegistered(), onRegistered },
					{ order.OnRegisterFailed(), onRegisterFailed },
					{ order.OnCancelled(), onCancelled },
					{ order.OnCancelFailed(), onCancelFailed },
					{ order.OnFilled(), onFilled },
					{ order.OnPartiallyFilled(), onPartiallyFilled },
					{ order.OnChanged(), onChanged },
					{ order.OnDone(), onDone },
					{ order.OnFailed(), onFailed },
			};
			for ( int i = 0; i < map.length; i ++ ) {
				if ( event.isType(map[i][0]) ) {
					dispatcher.dispatch(new OrderEvent(map[i][1], order));
					break;
				}
			}
		}
	}
	
	/**
	 * Начать ретрансляцию событий заявки.
	 * <p>
	 * @param order заявка
	 */
	public void startRelayFor(Order order) {
		order.OnCancelFailed().addListener(this);
		order.OnCancelled().addListener(this);
		order.OnChanged().addListener(this);
		order.OnDone().addListener(this);
		order.OnFailed().addListener(this);
		order.OnFilled().addListener(this);
		order.OnPartiallyFilled().addListener(this);
		order.OnRegistered().addListener(this);
		order.OnRegisterFailed().addListener(this);
		order.OnTrade().addListener(this);
	}
	
	/**
	 * Прекратить ретрансляцию событий заявки.
	 * <p>
	 * @param order заявка
	 */
	public void stopRelayFor(Order order) {
		order.OnCancelFailed().removeListener(this);
		order.OnCancelled().removeListener(this);
		order.OnChanged().removeListener(this);
		order.OnDone().removeListener(this);
		order.OnFailed().removeListener(this);
		order.OnFilled().removeListener(this);
		order.OnPartiallyFilled().removeListener(this);
		order.OnRegistered().removeListener(this);
		order.OnRegisterFailed().removeListener(this);
		order.OnTrade().removeListener(this);
	}

}
