package ru.prolib.aquila.core;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.eque.DispatcherThread;
import ru.prolib.aquila.core.eque.DispatcherThreadV2;
import ru.prolib.aquila.core.eque.DispatcherThreadV3;
import ru.prolib.aquila.core.eque.DispatchingType;
import ru.prolib.aquila.core.eque.EventDispatchingRequest;
import ru.prolib.aquila.core.eque.EventQueueStats;

/**
 * Event queue implementation.
 * <p>
 * 2012-04-16<br>
 * $Id: EventQueueImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventQueueImpl implements EventQueue {
	
	private static final DispatchingType DEFAULT_DISPATCHING_TYPE;
	private static final int DEFAULT_NUM_OF_WORKERS;
	
	private static final Logger logger;
	private static int queueLastIndex;
	
	static {
		logger = LoggerFactory.getLogger(EventQueueImpl.class);
		DEFAULT_DISPATCHING_TYPE = DispatchingType.NEW_RIGHT_HERE_V3;
		DEFAULT_NUM_OF_WORKERS = 4;
	}
	
	private static String getNextQueueName() {
		queueLastIndex ++;
		return "EQUE-" + queueLastIndex;
	}

	private final BlockingQueue<EventDispatchingRequest> queue;
	private final String queueName;
	private final Thread dispatcherThread;
	private final EventQueueStats stats;

	/**
	 * Constructor.
	 * <p>
	 * @param queueName - the name of queue (used to identify threads)
	 * @param numOfWorkerThreads - number of threads to event delivery
	 * @param dispatchingType - type of dispatching algorithm
	 */
	public EventQueueImpl(String queueName,
			int numOfWorkerThreads,
			DispatchingType dispatchingType)
	{
		this.stats = new EventQueueStats();
		this.queueName = queueName;
		this.queue = new LinkedBlockingQueue<EventDispatchingRequest>();
		switch ( dispatchingType ) {
		case OLD_ORIGINAL:
			dispatcherThread = DispatcherThread.createOriginal(queue, queueName, numOfWorkerThreads, stats);
			break;
		case OLD_COMPL_FUTURES:
			dispatcherThread = DispatcherThread.createComplFutures(queue, queueName, numOfWorkerThreads, stats);
			break;
		case OLD_RIGHT_HERE:
			dispatcherThread = DispatcherThread.createRightHere(queue, queueName, stats);
			break;
		case NEW_QUEUE_4WORKERS:
			dispatcherThread = DispatcherThread.createQueueWorkers(queue, queueName, 4, stats);
			break;
		case NEW_QUEUE_6WORKERS:
			dispatcherThread = DispatcherThread.createQueueWorkers(queue, queueName, 6, stats);
			break;
		case NEW_RIGHT_HERE_NO_TIME_STATS:
			dispatcherThread = DispatcherThreadV2.createNoTimeStats(queue, queueName, stats);
			break;
		case NEW_RIGHT_HERE_V3:
			dispatcherThread = DispatcherThreadV3.createV3(queue, queueName, stats);
			break;
		default:
			throw new IllegalArgumentException("Unsupported dispatching type: " + dispatchingType);
		}
		this.dispatcherThread.start();
	}
	
	public EventQueueImpl(String queueName) {
		this(queueName, 4, DEFAULT_DISPATCHING_TYPE);
	}
	
	/**
	 * Default constructor.
	 */
	public EventQueueImpl() {
		this(getNextQueueName());
	}
	
	public EventQueueImpl(DispatchingType dispatchingType) {
		this(getNextQueueName(), DEFAULT_NUM_OF_WORKERS, dispatchingType);
	}
	
	public EventQueueStats getStats() {
		return stats;
	}
	
	@Override
	public long getTotalEvents() {
		return stats.getTotalEventsSent();
	}

	@Override
	public String getId() {
		return queueName;
	}

	@Override
	public void enqueue(EventType type, EventFactory factory) {
		try {
			if ( type.countListeners() > 0 ) {
				queue.put(new EventDispatchingRequest(type, factory));
				stats.addEventSent();
			}
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			logger.error("Interrupted: ", e);
		}
	}
	
	public void shutdown() throws InterruptedException {
		//logger.debug("Shutdown request sent to {}", queueName);
		queue.put(EventDispatchingRequest.EXIT);
	}

}
