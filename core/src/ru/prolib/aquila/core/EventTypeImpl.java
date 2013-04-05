package ru.prolib.aquila.core;

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
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventType#addListener(ru.prolib.aquila.core.EventListener)
	 */
	@Override
	public void addListener(EventListener listener) {
		dispatcher.addListener(this, listener);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventType#removeListener(ru.prolib.aquila.core.EventListener)
	 */
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
	 * Закрыт для перегрузки, так как влияет на диспетчеризацию.
	 */
	@Override
	public final boolean equals(Object other) {
		return super.equals(other);
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

}
