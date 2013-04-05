package ru.prolib.aquila.core.rule;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;


/**
 * Задача единичного запуска связанной задачи по событию.
 * <p>
 * 2013-01-07<br>
 * $Id: RunOnceOnEvent.java 400 2013-01-08 05:22:51Z whirlwind $
 */
public class RunOnceOnEvent implements EventListener, Runnable {
	private final EventType type;
	private final Runnable runnable;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param type тип ожидаемого события
	 * @param runnable связаная задача
	 */
	public RunOnceOnEvent(EventType type, Runnable runnable) {
		super();
		this.type = type;
		this.runnable = runnable;
	}
	
	/**
	 * Получить ожидаемый тип события.
	 * <p>
	 * @return тип события
	 */
	public EventType getEventType() {
		return type;
	}
	
	/**
	 * Получить связаную задачу для запуска.
	 * <p>
	 * @return задача для запуска
	 */
	public Runnable getRunnable() {
		return runnable;
	}

	@Override
	public void run() {
		type.addListener(this);
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(type) ) {
			type.removeListener(this);
			runnable.run();
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130107, 72647)
			.append(type)
			.append(runnable)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == RunOnceOnEvent.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		RunOnceOnEvent o = (RunOnceOnEvent) other;
		return new EqualsBuilder()
			.append(type, o.type)
			.append(runnable, o.runnable)
			.isEquals();
	}

}
