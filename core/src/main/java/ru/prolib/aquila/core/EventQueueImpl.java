package ru.prolib.aquila.core;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.eque.EventDispatchingRequest;
import ru.prolib.aquila.core.eque.EventQueueService;

/**
 * Event queue implementation.
 * <p>
 * 2012-04-16<br>
 * $Id: EventQueueImpl.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class EventQueueImpl implements EventQueue {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EventQueueImpl.class);
	}
	
	private final BlockingQueue<EventDispatchingRequest> queue;
	private final String queueName;
	private final EventQueueService service;
	
	public EventQueueImpl(BlockingQueue<EventDispatchingRequest> queue,
			EventQueueService service,
			String id)
	{
		this.queue = queue;
		this.service = service;
		this.queueName = id;
	}

	@Override
	public EventQueueStats getStats() {
		return service.getStats();
	}
	
	@Override
	public FlushIndicator newFlushIndicator() {
		return service.createIndicator();
	}

	@Override
	public String getId() {
		return queueName;
	}

	@Override
	public void enqueue(EventType type, EventFactory factory) {
		// WARNING: Never do something which may cause deadlock while this call
		// Do not limit queue length, do not enter critical sections here, etc...
		try {
			// That makes no sense to test count of listeners
			// It may be listeners of alternate types
			service.eventEnqueued();
			queue.put(new EventDispatchingRequest(type, factory));
			service.eventSent();
		} catch ( InterruptedException e ) {
			Thread.currentThread().interrupt();
			logger.error("Interrupted: ", e);
		}
	}
	
	@Override
	public void shutdown() {
		try {
			queue.put(EventDispatchingRequest.EXIT);
		} catch ( InterruptedException e ) {
			logger.error("Interrupted: ", e);
		}
	}

}
