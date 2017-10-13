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

	/**
	 * Constructor.
	 * <p>
	 * @param queueName - the name of queue (used to identify threads)
	 * @param numOfWorkerThreads - number of threads to event delivery
	 */
	public EventQueueImpl(String queueName, int numOfWorkerThreads) {
		this.queueName = queueName;
		this.queue = new LinkedBlockingQueue<EventDispatchingRequest>();
		this.dispatcherThread = new DispatcherThread(queueName, numOfWorkerThreads, queue);
		this.dispatcherThread.start();
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

	@Override
	public String getId() {
		return queueName;
	}		

	@Override
	public void enqueue(EventType type, EventFactory factory) {
		try {
			queue.put(new EventDispatchingRequest(type, factory));
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
		
		public DeliveryEventTask(Event event, EventListener listener) {
			this.event = event;
			this.listener = listener;
		}

		@Override
		public Object call() throws Exception {
			try {
				//logger.debug("Dispatch event {} to listener {}", event, listener);
				listener.onEvent(event);
			} catch ( Throwable t ) {
				logger.error("Unhandled exception: ", t);
			}
			return null;
		}
		
	}
	
	static class DispatcherThread extends Thread {
		private final BlockingQueue<EventDispatchingRequest> queue;
		private final ExecutorService deliveryService;
		
		public DispatcherThread(String queueName, int numOfWorkerThreads,
				BlockingQueue<EventDispatchingRequest> queue)
		{
			super(queueName + ".DISPATCHER");
			setDaemon(true);
			this.queue = queue;
			this.deliveryService = Executors.newFixedThreadPool(numOfWorkerThreads,
					new WorkerThreadFactory(queueName));
		}
		
		@Override
		public void run() {
			try {
				EventDispatchingRequest request;
				while ( (request = queue.take()) != null ) {
					for ( Future<Object> x : deliveryService.invokeAll(buildTaskList(request)) ) {
						x.get();
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
					list.add(new DeliveryEventTask(event, listener));
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
