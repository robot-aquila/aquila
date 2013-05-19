package ru.prolib.aquila.core;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Очередь событий.
 * <p>
 * 2012-04-16<br>
 * $Id: EventQueueImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventQueueImpl implements EventQueue {
	private static final Logger logger;
	static private final QueueEntry EXIT = new QueueEntry();
	private final BlockingQueue<QueueEntry> queue;
	private final String name;
	private Thread thread = null;
	
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
		queue = new LinkedBlockingQueue<QueueEntry>();
	}
	
	/**
	 * Создать очередь событий.
	 */
	public EventQueueImpl() {
		this("EVNT");
	}

	@Override
	public synchronized boolean started() {
		return thread != null && thread.isAlive();
	}
	
	@Override
	public String getId() {
		return name;
	}

	/**
	 * Запустить поток диспетчерезации событий.
	 * <p>
	 * Процедура запуска завершается после того, как стартует поток обработки
	 * событий. 
	 * <p>
	 * @throws StarterInterruptedException старт очереди прерван
	 * @throws IllegalStateException объект уже в работе
	 */
	@Override
	public void start() throws StarterException {
		CountDownLatch started = new CountDownLatch(1);
		synchronized ( this ) {
			if ( started() ) {
				throw new IllegalStateException("Queue already started");
			}
			if ( queue.size() > 0 ) {
				logger.warn("start(): queue.size() > 0 for {}", name);
			}
			queue.clear();
			thread = new Thread(new QueueWorker(queue, started), name);
			thread.start();
		}
		try {
			started.await();
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new StarterInterruptedException(e);
		}
	}

	/**
	 * Остановить обработку событий.
	 * <p>
	 * Данный метод может быть вызван из любого потока. Завершение метода
	 * нельзя рассматривать как гарантию завершении потока обработки очереди
	 * событий. Для ожидания гарантированного завершения потока очереди следует
	 * использовать метод {@link #join()} или {@link #join(long)} с последующим
	 * вызовом {@link #started()}. 
	 * <p> 
	 */
	@Override
	public void stop() {
		synchronized ( this ) {
			if ( ! started() ) {
				return;
			}
			queue.offer(EXIT);
		}
	}

	@Override
	public synchronized boolean isDispatchThread() {
		return started()
			&& thread.getId() == Thread.currentThread().getId();
	}
	
	@Override
	public boolean join(long timeout) throws InterruptedException {
		if ( timeout <= 0 ) {
			throw new IllegalArgumentException("Timeout cannot be less than 1");
		}
		Thread queueThread = null;
		synchronized ( this ) {
			if ( ! started() ) return true;
			if ( isDispatchThread() ) {
				logger.warn("Cannot join(X) dispatch thread from himself");
				return false;
			} else {
				queueThread = thread;
			}
		}
		queueThread.join(timeout);
		return ! queueThread.isAlive();
	}

	@Override
	public void join() throws InterruptedException {
		Thread queueThread = null;
		synchronized ( this ) {
			if ( ! started() ) return;
			if ( isDispatchThread() ) {
				logger.warn("Cannot join() dispatch thread from himself");
				return;
			}
			queueThread = thread;
		}
		queueThread.join();
	}
	
	/**
	 * Поставить событие в очередь на обработку.
	 * <p>
	 * @throws IllegalStateException поток обработки не запущен
	 */
	@Override
	public synchronized
		void enqueue(Event event, List<EventListener> listeners)
	{
		if ( ! started() ) {
			throw new IllegalStateException("Queue not started");
		}
		if ( event == null ) {
			throw new NullPointerException("The event cannot be null");
		}
		if ( listeners == null ) {
			throw new NullPointerException("The listeners cannot be null");
		}
		if ( ! queue.offer(new QueueEntry(event, listeners)) ) {
			logger.error("Last event has been be rejected by queue (1)");
		}
	}
	
	@Override
	public synchronized void enqueue(Event event, EventDispatcher dispatcher) {
		if ( ! started() ) {
			throw new IllegalStateException("Queue not started");
		}
		if ( event == null ) {
			throw new NullPointerException("The event cannot be null");
		}
		if ( dispatcher == null ) {
			throw new NullPointerException("The dispatcher cannot be null");
		}
		if ( ! queue.offer(new QueueEntry(event, dispatcher)) ) {
			logger.error("Last event has been be rejected by queue (2)");
		}
	}
	
	/**
	 * Реализация диспетчеризации событий из очереди.
	 */
	static private class QueueWorker implements Runnable {
		private final BlockingQueue<QueueEntry> queue;
		private final CountDownLatch started;
		
		/**
		 * Конструктор
		 * <p>
		 * @param queue очередь событий
		 * @param started сигнал успешного запуска
		 */
		public QueueWorker(BlockingQueue<QueueEntry> queue,
						   CountDownLatch started)
		{
			super();
			this.queue = queue;
			this.started = started;
		}

		@Override
		public void run() {
			started.countDown();
			try {
				QueueEntry e;
				List<EventListener> list;
				while ( (e = queue.take()) != null ) {
					if ( e == EXIT ) {
						break;
					}
					// TODO: 
					// http://kobresia.acunote.com/projects/15260/tasks/1033
					if ( e.dispatcher != null ) {
						list = e.dispatcher.getListeners(e.event.getType()); 
					} else if ( e.listeners != null ) {
						list = e.listeners;
					} else {
						list = new Vector<EventListener>();
					}
					for ( EventListener listener : list ) {
						try {
							listener.onEvent(e.event);
						} catch ( Exception ex ) {
							logger.error("Unhandled exception: ", ex);
						}
					}
				}
			} catch ( InterruptedException e ) {
				Thread.currentThread().interrupt();
			}
		}
		
	}
	
	/**
	 * Запись очереди событий.
	 */
	static private class QueueEntry {
		private final Event event;
		private final List<EventListener> listeners;
		private final EventDispatcher dispatcher;
		
		/**
		 * Создать запись на основе списка наблюдателей.
		 * <p>
		 * Такая структура подразумевает рассылку события указанному списку
		 * наблюдателей независимо от состояния диспетчера на момент рассылки.
		 * Иначе говоря, данный подход определяет неизменный список получателей.  
		 * <p>
		 * @param e событие
		 * @param listeners получатели
		 */
		public QueueEntry(Event e, List<EventListener> listeners) {
			super();
			this.event = e;
			this.listeners = listeners;
			this.dispatcher = null;
		}
		
		/**
		 * Создать запись на основе диспетчера.
		 * <p>
		 * Такая структура подразумевает рассылку события наблюдателям, список
		 * которых определяется непосредственно на момент рассылки. Иначе
		 * говоря, список получателей может меняться в зависимости от состояния
		 * диспетчера на момент отправки.
		 * <p>
		 * @param e событие
		 * @param dispatcher диспетчер
		 */
		public QueueEntry(Event e, EventDispatcher dispatcher) {
			super();
			this.event = e;
			this.listeners = null;
			this.dispatcher = dispatcher;
		}
		
		/**
		 * Создать пустую запись.
		 */
		public QueueEntry() {
			super();
			this.event = null;
			this.listeners = null;
			this.dispatcher = null;
		}
		
	}
	
	/**
	 * Сравнивает только идентификаторы очередей.
	 * <p>
	 * Только для тестов.
	 */
	@Override
	public final boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != EventQueueImpl.class ) {
			return false;
		}
		EventQueueImpl o = (EventQueueImpl) other;
		return new EqualsBuilder()
			.append(o.name, name)
			.isEquals();
	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

}
