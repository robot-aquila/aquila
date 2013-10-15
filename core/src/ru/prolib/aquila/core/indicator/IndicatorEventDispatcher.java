package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.*;

/**
 * Диспетчер событий индикатора.
 * <p>
 * Диспетчер абстрагирует индикатор от набора задействованных типов событий.
 * Может использоваться как для индикаторов с постоянным хранением ряда, так
 * и для индикаторов с расчетом значений на-лету. В случае постоянного ряда,
 * диспетчер подписывается на события исходного ряда с целью последующей
 * ретрансляции его событий. Для индикаторов с расчетом на-лету, генерация
 * событий должна выполняться явно, посредством вызовов соответствующего метода
 * диспетчера. 
 */
public class IndicatorEventDispatcher implements EventListener {
	private final EventDispatcher dispatcher;
	private final EventType onStarted, onStopped, onAdd, onUpd;
	private Series<?> relayed;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param es фасад событийной системы
	 * @param id идентификатор (может быть null)
	 */
	public IndicatorEventDispatcher(EventSystem es, String id) {
		super();
		if ( id == null ) {
			dispatcher = es.createEventDispatcher();
		} else {
			dispatcher = es.createEventDispatcher(id);
		}
		onStarted = dispatcher.createType("Started");
		onStopped = dispatcher.createType("Stopped");
		onAdd = dispatcher.createType("Add");
		onUpd = dispatcher.createType("Upd");
	}
	
	/**
	 * Конструктор с идентификатором по-умолчанию.
	 * <p>
	 * @param es фасад событийной системы
	 */
	public IndicatorEventDispatcher(EventSystem es) {
		this(es, null);
	}
	
	/**
	 * Конструктор по-умолчанию
	 * <p>
	 * Создает диспетчер с идентификатором по-умолчанию и очередью синхронной
	 * подачи событий {@link SimpleEventQueue}.
	 */
	public IndicatorEventDispatcher() {
		this(new EventSystemImpl(new SimpleEventQueue()));
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @return диспетчер событий
	 */
	EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить тип события: запуск индикатора.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStarted() {
		return onStarted;
	}
	
	/**
	 * Получить тип события: останов индикатора.
	 * <p>
	 * @return тип события
	 */
	public EventType OnStopped() {
		return onStopped;
	}
	
	/**
	 * Получить тип события: новое значение ряда.
	 * <p>
	 * @return тип события
	 */
	public EventType OnAdded() {
		return onAdd;
	}
	
	/**
	 * Получить тип события: текущее значение обновлено.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUpdated() {
		return onUpd;
	}
	
	/**
	 * Генерировать событие о запуске индикатора.
	 */
	public void fireStarted() {
		dispatcher.dispatch(new EventImpl(onStarted));
	}
	
	/**
	 * Генерировать событие об останове индикатора.
	 */
	public void fireStopped() {
		dispatcher.dispatch(new EventImpl(onStopped));
	}
	
	/**
	 * Генерировать событие о новом значении ряда.
	 * <p>
	 * @param newValue новое значение индикатора
	 * @param index индекс значение в последовательности
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void fireAdded(Object newValue, int index) {
		dispatcher.dispatch(new ValueEvent(onAdd, newValue, index));
	}
	
	/**
	 * Генерировать событие об обновлении текущего значения ряда.
	 * <p>
	 * @param newValue новое значение индикатора
	 * @param index индекс значения в последовательности
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void fireUpdated(Object newValue, int index) {
		dispatcher.dispatch(new ValueEvent(onUpd, newValue, index));
	}
	
	/**
	 * Генерировать событие об обновлении текущего значения ряда.
	 * <p>
	 * @param oldValue предыдущее значение индикатора
	 * @param newValue новое значение индикатора
	 * @param index индекс значения в последовательности
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void fireUpdated(Object oldValue, Object newValue, int index) {
		dispatcher.dispatch(new ValueEvent(onUpd, oldValue, newValue, index));
	}
	
	/**
	 * Начать ретрансляцию событий ряда.
	 * <p>
	 * @param source источник событий
	 * @throws IllegalStateException ретрансляция уже запущена (возможно, для
	 * другого источника)
	 */
	public synchronized void startRelayFor(Series<?> source) {
		if ( relayed == null ) {
			relayed = source;
			relayed.OnAdded().addListener(this);
			relayed.OnUpdated().addListener(this);
		} else {
			throw new IllegalStateException("Relay already started");
		}
	}
	
	/**
	 * Остановить ретрансляцию событий ряда.
	 */
	public synchronized void stopRelay() {
		if ( relayed != null ) {
			relayed.OnAdded().removeListener(this);
			relayed.OnUpdated().removeListener(this);
			relayed = null;
		}
	}

	@Override
	public synchronized void onEvent(Event event) {
		@SuppressWarnings("rawtypes")
		ValueEvent e = (ValueEvent) event;
		if ( event.isType(relayed.OnAdded()) ) {
			fireAdded(e.getNewValue(), e.getValueIndex());
		} else if ( event.isType(relayed.OnUpdated()) ) {
			fireUpdated(e.getOldValue(), e.getNewValue(), e.getValueIndex());
		}
	}

}
