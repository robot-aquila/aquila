package ru.prolib.aquila.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

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
	private static int queueLastIndex;
	
	static {
		logger = LoggerFactory.getLogger(EventQueueImpl.class);
	}
	
	private static String getNextQueueName() {
		queueLastIndex ++;
		return "EQUE-" + queueLastIndex;
	}

	private final BlockingQueue<EventDispatchingRequest> queue;
	private final String queueName;
	private final Thread dispatcherThread;
	private final AtomicLong totalEvents;
	private final EventQueueStats stats;

	/**
	 * Constructor.
	 * <p>
	 * @param queueName - the name of queue (used to identify threads)
	 * @param numOfWorkerThreads - number of threads to event delivery
	 */
	public EventQueueImpl(String queueName, int numOfWorkerThreads) {
		this.stats = new EventQueueStats();
		this.queueName = queueName;
		this.queue = new LinkedBlockingQueue<EventDispatchingRequest>();
		this.dispatcherThread = new DispatcherThread(queueName, numOfWorkerThreads, queue, stats);
		this.dispatcherThread.start();
		this.totalEvents = new AtomicLong(0);
	}
	
	public EventQueueImpl(String queueName) {
		this(queueName, 4);
	}
	
	/**
	 * Default constructor.
	 */
	public EventQueueImpl() {
		this(getNextQueueName());
	}
	
	public EventQueueStats getStats() {
		return stats;
	}
	
	@Override
	public long getTotalEvents() {
		return totalEvents.get();
	}

	@Override
	public String getId() {
		return queueName;
	}

	@Override
	public void enqueue(EventType type, EventFactory factory) {
		try {
			queue.put(new EventDispatchingRequest(type, factory));
			totalEvents.incrementAndGet();
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			logger.error("Interrupted: ", e);
		}
	}
	
	static class EventDispatchingRequest {
		private final EventType type;
		private final EventFactory factory;
		
		public EventDispatchingRequest(EventType type, EventFactory factory) {
			this.type = type;
			this.factory = factory;
		}
	}
	
	static class WorkerThreadFactory implements ThreadFactory {
		private int threadLastIndex = 0;
		private final String namePrefix;
		
		public WorkerThreadFactory(String namePrefix) {
			this.namePrefix = namePrefix;
		}

		@Override
		public synchronized Thread newThread(Runnable r) {
			threadLastIndex ++;
			Thread t = new Thread(r, namePrefix + ".WORKER-" + threadLastIndex);
			t.setDaemon(true);
			return t;
		}
		
	}
	
	static class DeliveryEventTask implements Callable<Object> {
		private final Event event;
		private final EventListener listener;
		private final EventQueueStats stats;
		
		public DeliveryEventTask(Event event,
				EventListener listener,
				EventQueueStats stats)
		{
			this.event = event;
			this.listener = listener;
			this.stats = stats;
		}

		@Override
		public Object call() {
			try {
				//logger.debug("Dispatch event {} to listener {}", event, listener);
				if ( stats == null ) {
					listener.onEvent(event);
				} else {
					long b1 = System.nanoTime();
					listener.onEvent(event);
					stats.addDeliveryTime(System.nanoTime() - b1);
				}
			} catch ( Throwable t ) {
				logger.error("Unhandled exception: ", t);
			}
			return null;
		}
		
	}
	
	static class DispatcherThread extends Thread {
		private final BlockingQueue<EventDispatchingRequest> queue;
		private final ExecutorService deliveryService;
		private final EventQueueStats stats;
		
		public DispatcherThread(String queueName, int numOfWorkerThreads,
				BlockingQueue<EventDispatchingRequest> queue,
				EventQueueStats stats)
		{
			super(queueName + ".DISPATCHER");
			setDaemon(true);
			this.queue = queue;
			this.deliveryService = Executors.newFixedThreadPool(numOfWorkerThreads,
					new WorkerThreadFactory(queueName));
			this.stats = stats;
		}
		
		@Override
		public void run() {
			try {
				EventDispatchingRequest request;
				if ( stats == null ) {
					while ( (request = queue.take()) != null ) {
						List<DeliveryEventTask> tasks = buildTaskList(request);
						for ( Future<Object> x : deliveryService.invokeAll(tasks) ) {
							x.get();
						}
					}
				} else {
					while ( (request = queue.take()) != null ) {
						long b1 = System.nanoTime();
						List<DeliveryEventTask> tasks = buildTaskList(request);
						long b2 = System.nanoTime();
						stats.addBuildingTaskListTime(b2 - b1);
						
						// Original method
						/*
						for ( Future<Object> x : deliveryService.invokeAll(tasks) ) {
							x.get();
						}
						*/
						
						// New, based on completable futures
						int count = tasks.size();
						CompletableFuture<?> fss[] = new CompletableFuture[count];
						for ( int i = 0; i < count; i ++ ) {
							DeliveryEventTask task = tasks.get(i);
							fss[i] = CompletableFuture.supplyAsync(()-> {
								return task.call();
							}, deliveryService);
						}
						CompletableFuture.allOf(fss).join();

						// Deliver here
						/*
						for ( DeliveryEventTask t : tasks ) {
							t.call();
						}
						*/
						
						stats.addDispatchingTime(System.nanoTime() - b2);
					}
				}
			} catch ( InterruptedException e ) {
				logger.error("Interrupted: ", e);
				Thread.currentThread().interrupt();
			} catch ( Throwable e ) {
				logger.error("Unexpected exception: ", e);
			}
		}
		
		private List<DeliveryEventTask> buildTaskList(EventDispatchingRequest request) {
			List<DeliveryEventTask> list = new LinkedList<>();
			Set<EventType> allTypes = new HashSet<>();
			for ( EventType type : getAllUniqueTypes(allTypes, request.type) ) {
				Event event = request.factory.produceEvent(type);
				for ( EventListener listener : type.getListeners() ) {
					list.add(new DeliveryEventTask(event, listener, stats));
				}
			}
			return list;
		}
		
		
		private Set<EventType> getAllUniqueTypes(Set<EventType> allTypes, EventType startType) {
			allTypes.add(startType);
			for ( EventType alternate : startType.getAlternateTypes() ) {
				if ( ! allTypes.contains(alternate) ) {
					getAllUniqueTypes(allTypes, alternate);
				}
			}
			return allTypes;
		}
		
	}

}
