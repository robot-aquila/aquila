package ru.prolib.aquila.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private volatile Thread thread = null;
	private final LinkedList<Event> cache1;
	private final Lock queueLock = new ReentrantLock();
	private boolean queueProcessing = false;
	
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
		cache1 = new LinkedList<Event>();
		startWorker();
	}
	
	/**
	 * Создать очередь событий.
	 */
	public EventQueueImpl() {
		this("EVNT");
	}
	
	private void startWorker() {
		thread = new Thread(new QueueWorker(queue), name);
		thread.setDaemon(true);
		thread.start();
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
		
		// Кэшированние событий используется для соблюдения требования
		// диспетчеризации событий в порядке их поступления. Если этого не
		// сделать, то при генерации событий из обработчика другого события
		// нарушение неизбежно.
		
		queueLock.lock();
		try {
			cache1.add(event);
			if ( queueProcessing ) {
				return;
			}
			queueProcessing = true;
		} finally {
			queueLock.unlock();
		}

		LinkedList<Event> cache2 = new LinkedList<>();
		List<EventListener> listeners;
		for ( ;; ) {
			queueLock.lock();
			try {
				cache2.add(event = cache1.pollFirst());
			} finally {
				queueLock.unlock();
			}
			
			listeners = event.getType().getSyncListeners();
			for ( EventListener listener : listeners ) {
				try {
					listener.onEvent(event);
				} catch ( Throwable e ) {
					logger.error("Unhandled exception: ", e);
				}
			}
			queueLock.lock();
			try {
				if ( cache1.size() == 0 ) {
					break;
				}
			} finally {
				queueLock.unlock();
			}
		}
		
		queueLock.lock();
		try {
			for ( Event x : cache2 ) {
				try {
					queue.put(x);
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
					logger.error("Thread interrupted: ", e);
				}
			}
			queueProcessing = false;
		} finally {
			queueLock.unlock();
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
				List<EventListener> listeners;
				while ( (event = queue.take()) != null ) {
					listeners = event.getType().getAsyncListeners();
					for ( EventListener listener : listeners ) {
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
