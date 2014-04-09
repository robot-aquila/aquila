package ru.prolib.aquila.core.BusinessEntities.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <p>
 * <i>2014-04-09 Архитектурная проблема</i>
 * <p>
 * Обнаруженная проблема рассинхронизации последовательности событий
 * при работе через непосредственно события объекта и при работе через
 * аналогичные ретрансляционные-события хранилища серьезно влияет на
 * работоспособность пользовательского кода. Для решения данной проблемы, все
 * события подчиненных объектов должны перенаправляться обозревателям
 * события ретрансляторного типа синхронно в момент получения исходного
 * события непосредственно от объекта-источника.
 * <p>
 * Например, проблемная ситуация с отчетом по трейдам, который учитывает
 * собственные сделки, полученные через соответствующий ретрансляторный тип
 * события терминала. Если реакция (например стратегии) связана с
 * {@link Order#OnDone()}, а отчет собирает сделки по
 * {@link Terminal#OnOrderTrade()}, то в момент поступления в обработчик события
 * {@link Order#OnDone()} трейд-отчет не будет содержать корректную информацию
 * об открытой позиции, так как на этот момент трейд-отчет еще не получит
 * перенаправленное события типа {@link Terminal#OnOrderTrade()}. Такого эффекта
 * не будет, если отлавливать сделки напрямую через {@link Order#OnTrade()}. Но
 * при этом смысл организации ретрансляции через хранилище полностью теряется.
 * Данный фикс исправляет вышеописанную ситуацию.
 * <p>
 */
public class OrdersEventDispatcher implements EventListener {
	@SuppressWarnings("unused")
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(OrdersEventDispatcher.class);
	}
	
	private final EventDispatcher dispatcher, sync_disp;
	private final EventType onRegistered, onRegisterFailed, onCancelled,
		onCancelFailed, onFilled, onPartiallyFilled, onChanged, onDone,
		onFailed, onTrade, onAvailable;
	
	public OrdersEventDispatcher(EventSystem es) {
		super();
		dispatcher = es.createEventDispatcher("Orders");
		onAvailable = dispatcher.createType("Available");
		sync_disp = createSyncDispatcher();
		onRegistered = sync_disp.createType("Registered");
		onRegisterFailed = sync_disp.createType("RegisterFailed");
		onCancelled = sync_disp.createType("Cancelled");
		onCancelFailed = sync_disp.createType("CancelFailed");
		onFilled = sync_disp.createType("Filled");
		onPartiallyFilled = sync_disp.createType("PartiallyFilled");
		onChanged = sync_disp.createType("Changed");
		onDone = sync_disp.createType("Done");
		onFailed = sync_disp.createType("Failed");
		onTrade = sync_disp.createType("Trade");
	}
	
	private final EventDispatcher createSyncDispatcher() {
		 return new EventDispatcherImpl(new SimpleEventQueue(), "Orders");
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
			OrderTradeEvent e = (OrderTradeEvent) event,
				ne = new OrderTradeEvent(onTrade, e.getOrder(), e.getTrade());
			sync_disp.dispatch(ne);
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
					sync_disp.dispatch(new OrderEvent(map[i][1], order));
					break;
				}
			}
			// TODO: прекратить трансляцию при финальном событии?
			// Сначала нужно определиться какое будет действительно финальным.  
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
