package ru.prolib.aquila.core;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Базовый тип события.
 * <p>
 * 2012-04-09<br>
 * $Id: EventTypeImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventTypeImpl implements EventType {
	public static final String AUTO_ID_PREFIX = "EvtType";
	private static int autoId = 1;
	private final EventDispatcher dispatcher;
	private final String id;
	
	/**
	 * Создать тип события.
	 * <p>
	 * Создается объект с идентификатором по умолчанию. Идентификатор
	 * формируется по шаблону {@link #AUTO_ID_PREFIX} + autoId;
	 * <p>
	 * @param dispatcher диспетчер событий
	 */
	public EventTypeImpl(EventDispatcher dispatcher) {
		this(dispatcher, AUTO_ID_PREFIX + (autoId ++));
	}
	
	/**
	 * Создать тип события.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param id идентификатор типа события
	 */
	public EventTypeImpl(EventDispatcher dispatcher, String id) {
		super();
		if ( dispatcher == null ) {
			throw new NullPointerException("Dispatcher cannot be null"); 
		}
		this.dispatcher = dispatcher;
		this.id = id;
	}
	
	/**
	 * Получить текущий идентификатор для автоназначения.
	 * <p>
	 * @return текущее значение идентификатора
	 */
	public static synchronized int getAutoId() {
		return autoId;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return asString();
	}
	
	@Override
	public String asString() {
		return dispatcher.asString() + "." + id;
	}
	
	@Override
	public void addListener(EventListener listener) {
		dispatcher.addListener(this, listener);
	}
	
	@Override
	public void removeListener(EventListener listener) {
		dispatcher.removeListener(this, listener);
	}

	/**
	 * Получить диспетчер событий
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Сравнить структуру типов.
	 * <p>
	 * Два разных экземпляра типа всегда представляют разные типы событий.
	 * По этому, для проверки на конкретный тип необходимо сравнивать значение
	 * с конкретным экземпляром. Метод сравнения сравнивает только структуру
	 * типа события. Структуры двух типов событий считаются одинаковыми, когда
	 * совпадают идентификаторы и списки наблюдателей. Проверка соответствия
	 * диспетчеров не выполняется.
	 * <p>
	 * Данный метод предназначен исключительно для использования в тестах.
	 * Он значительно сокращает код теста и позволяет избежать моков для
	 * воспроизведения типа. Сравнение данным методом никогда не должно
	 * использоваться в рабочих процессах.
	 * <p> 
	 */
	@Override
	public final synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != EventTypeImpl.class ) {
			return false;
		}
		EventTypeImpl o = (EventTypeImpl) other;
		return new EqualsBuilder()
			.append(id, o.id)
			.append(dispatcher.getListeners(this), o.dispatcher.getListeners(o))
			.isEquals();
	}
	
	/**
	 * Закрыт для перегрузки, так как влияет на диспетчеризацию.
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean isListener(EventListener listener) {
		return dispatcher.isTypeListener(this, listener);
	}
	
	@Override
	public EventListener once(EventListener listener) {
		ListenOnce once = new ListenOnce(this, listener);
		once.start();
		return once;
	}

}
