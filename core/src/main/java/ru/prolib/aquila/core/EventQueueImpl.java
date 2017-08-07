package ru.prolib.aquila.core;

import java.util.*;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Event queue implementation.
 * <p>
 * 2012-04-16<br>
 * $Id: EventQueueImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventQueueImpl implements EventQueue {
	private static final Logger logger;
	private final BlockingQueue<Event> queue;
	private final String name;
	private final Thread thread;
	private final int maxSize = 5120;
	
	static {
		logger = LoggerFactory.getLogger(EventQueueImpl.class);
	}
	
	/**
	 * Создать очередь событий.
	 * <p>
	 * @param threadName наименование потока диспетчера очереди событий
	 */
	public EventQueueImpl(String threadName) {
		super();
		this.name = threadName;
		queue = new LinkedBlockingQueue<Event>();
		thread = new Thread(new QueueWorker(queue), name);
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 * Создать очередь событий.
	 */
	public EventQueueImpl() {
		this("EVNT");
	}

	@Override
	public String getId() {
		return name;
	}
	
	/**
	 * Поставить событие в очередь на обработку.
	 * <p>
	 * @throws IllegalStateException поток обработки не запущен
	 */
	private void enqueue(Event event) {
		if ( event == null ) {
			throw new NullPointerException("The event cannot be null");
		}
		
		try {
			if ( queue.size() >= maxSize ) {
				logger.warn("Queue is slow: {}", name);
				// Wait if this thread is not a worker thread.
				if ( Thread.currentThread() != thread ) {
					int expSize = (int)(maxSize * 0.4); // wait for 60% free size
					logger.debug("Isn't worker thread. Wait until queue has free space. Limit: {} pcs.", expSize);
					do {
						Thread.sleep(50L);
					} while ( queue.size() >= expSize );
				}
			}
			queue.put(event);
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			logger.error("Thread interrupted: ", e);
		}
	}
	
	/**
	 * Реализация диспетчеризации событий из очереди.
	 */
	static private class QueueWorker implements Runnable {
		private final BlockingQueue<Event> queue;
		
		/**
		 * Конструктор
		 * <p>
		 * @param queue очередь событий
		 */
		public QueueWorker(BlockingQueue<Event> queue) {
			super();
			this.queue = queue;
		}

		@Override
		public void run() {
			try {
				Event event;
				while ( (event = queue.take()) != null ) {
					for ( EventListener listener : event.getType().getListeners() ) {
						try {
							listener.onEvent(event);
						} catch ( Exception ex ) {
							logger.error("Unhandled exception: ", ex);
						}
					}
				}
			} catch ( InterruptedException e ) {
				logger.error("Queue thread interrupted: ", e);
				Thread.currentThread().interrupt();
			} catch ( Throwable e ) {
				logger.error("Queue thread exception: ", e);
				throw e;
			}
		}
		
	}

	@Override
	public void enqueue(EventType type, EventFactory factory) {
		if ( type.hasAlternates() ) {
			Set<EventType> alternates = new HashSet<EventType>();
			alternates.add(type);
			fillUniqueAlternates(alternates, type);
			for ( EventType alternate : alternates ) {
				enqueue(factory.produceEvent(alternate));
			}
		} else {
			enqueue(factory.produceEvent(type));	
		}
	}
	
	private void fillUniqueAlternates(Set<EventType> alternates, EventType type) {
		for ( EventType alternate : type.getAlternateTypes() ) {
			if ( ! alternates.contains(alternate) ) {
				alternates.add(alternate);
				fillUniqueAlternates(alternates, alternate);
			}
		}
	}

}
