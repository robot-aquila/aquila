package ru.prolib.aquila.core;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Стартер очереди событий.
 * <p>
 * Данный стартер предназначен для использования в тех случаях, когда требуется
 * дождаться гарантированного завершения потока обработки очереди событий при
 * запросе на остановку. Если гарантий не требуется, то можно напрямую
 * использовать методы интерфейса {@link Starter}, реализуемые классом очереди.
 * <p>
 * 2013-02-10<br>
 * $Id$
 */
@Deprecated
public class EventQueueStarter implements Starter {
	private final EventQueue queue;
	private final long timeout;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param queue очередь событий
	 * @param timeout таймаут ожидания завершения операций
	 */
	public EventQueueStarter(EventQueue queue, long timeout) {
		super();
		this.queue = queue;
		this.timeout = timeout;
	}
	
	/**
	 * Получить очередь событий.
	 * <p>
	 * @return очередь событий
	 */
	public EventQueue getEventQueue() {
		return queue;
	}
	
	/**
	 * Получить значение таймаута.
	 * <p>
	 * @return таймаут
	 */
	public long getTimeout() {
		return timeout;
	}

	@Override
	public void start() throws StarterException {
		queue.start();
	}

	@Override
	public void stop() throws StarterException {
		queue.stop();
		boolean stopped = false;
		try {
			stopped = queue.join(timeout);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new StarterException(e);
		}
		if ( ! stopped ) {
			throw new StarterException("queue.join(x) timeout");
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == EventQueueStarter.class ?
			fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		EventQueueStarter o = (EventQueueStarter) other;
		return new EqualsBuilder()
			.append(queue, o.queue)
			.append(timeout, o.timeout)
			.isEquals();
	}

}
