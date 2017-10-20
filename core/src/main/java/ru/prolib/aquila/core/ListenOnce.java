package ru.prolib.aquila.core;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Единоразовый обозреватель события.
 */
public class ListenOnce implements EventListener {
	private final EventType type;
	private final EventListener listener;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param listener обозреватель
	 */
	public ListenOnce(EventType type, EventListener listener) {
		super();
		this.type = type;
		this.listener = listener;
	}
	
	/**
	 * Начать отслеживание события.
	 */
	public void start() {
		type.addListener(this);
	}

	@Override
	public void onEvent(Event event) {
		type.removeListener(this);
		listener.onEvent(event);
	}
	
	/**
	 * Получить тип события.
	 * <p>
	 * @return тип события
	 */
	public EventType getEventType() {
		return type;
	}
	
	/**
	 * Получить обозреватель события.
	 * <p>
	 * @return обозреватель
	 */
	public EventListener getListener() {
		return listener;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ListenOnce.class ) {
			return false;
		}
		ListenOnce o = (ListenOnce) other;
		return new EqualsBuilder()
			.append(o.listener, listener)
			.appendSuper(o.type == type)
			.isEquals();
	}

}
