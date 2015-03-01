package ru.prolib.aquila.core;

import java.util.*;
import java.util.concurrent.*;
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
	static private final EventSI EXIT = new EventImpl(null);
	private final BlockingQueue<EventSI> queue;
	private final String name;
	private volatile Thread thread = null;
	private final LinkedList<EventSI> cache1, cache2;
	
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
		queue = new LinkedBlockingQueue<EventSI>();
		cache1 = new LinkedList<EventSI>();
		cache2 = new LinkedList<EventSI>();
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
			thread = new Thread(new QueueWorker(queue, started, name), name);
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
				logger.warn("Cannot join(X) dispatch thread from itself");
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
				logger.warn("Cannot join() dispatch thread from itself");
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
	public synchronized void enqueue(EventSI event) {
		if ( ! started() ) {
			throw new IllegalStateException("Queue not started: " + name);
		}
		if ( event == null ) {
			throw new NullPointerException("The event cannot be null");
		}
		// Кэшированние событий используется для соблюдения требования
		// диспетчеризации событий в порядке их поступления. Если этого не
		// сделать, то при генерации событий из обработчика другого события
		// нарушение неизбежно.
		cache1.add(event);
		if ( cache1.size() == 1 ) {
			List<EventListener> listeners;
			// Только в этом случае мы можем начинать диспетчеризацию.
			// Более одного элемента в кэше означает, что выше по стеку
			// очередь уже обрабатывается.
			do {
				event = cache1.getFirst(); // Сразу удалять нельзя!
				listeners = event.getTypeSI().getSyncListeners();
				for ( EventListener listener : listeners ) {
					try {
						listener.onEvent(event);
					} catch ( Throwable e ) {
						logger.error("Unhandled exception: ", e);
					}
				}
				cache2.add(cache1.pollFirst());
			} while ( cache1.size() > 0 );
			while ( cache2.size() > 0 ) {
				try {
					queue.put(cache2.pollFirst());
				} catch ( InterruptedException e ) {
					Thread.currentThread().interrupt();
					logger.error("Thread interrupted: ", e);
				}				
			}
		}

	}
	
	/**
	 * Реализация диспетчеризации событий из очереди.
	 */
	static private class QueueWorker implements Runnable {
		private final BlockingQueue<EventSI> queue;
		private CountDownLatch started;
		private final String name;
		
		/**
		 * Конструктор
		 * <p>
		 * @param queue очередь событий
		 * @param started сигнал успешного запуска
		 * @param name имя потока
		 */
		public QueueWorker(BlockingQueue<EventSI> queue,
						   CountDownLatch started, String name)
		{
			super();
			this.queue = queue;
			this.started = started;
			this.name = name;
		}

		@Override
		public void run() {
			started.countDown();
			started = null;
			try {
				EventSI event;
				List<EventListener> listeners;
				while ( (event = queue.take()) != null ) {
					if ( event == EXIT ) {
						break;
					}
					listeners = event.getTypeSI().getAsyncListeners();
					for ( EventListener listener : listeners ) {
						try {
							listener.onEvent(event);
						} catch ( Exception ex ) {
							logger.error("Unhandled exception: ", ex);
						}
					}
				}
			} catch ( InterruptedException e ) {
				Object args[] = { name, e };
				logger.error("Queue thread interrupted: {}", args);
				Thread.currentThread().interrupt();
			} catch ( Throwable e ) {
				Object args[] = { name, e };
				logger.error("Queue thread exception: {}", args);
			}
		}
		
	}

}
