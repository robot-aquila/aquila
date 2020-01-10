package ru.prolib.aquila.core.eque;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;

public class DispatcherThreadV2 extends Thread {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DispatcherThreadV2.class);
	}
	
	public static DispatcherThreadV2
		createNoTimeStats(BlockingQueue<EventDispatchingRequest> queue,
				String queueName,
				EventQueueService service)
	{
		DispatcherThreadV2 t = new DispatcherThreadV2(queue, service);
		t.setName(queueName + ".DISPATCHER");
		t.setDaemon(true);
		return t;
	}
	
	private final BlockingQueue<EventDispatchingRequest> queue;
	private final EventQueueService service;
	
	public DispatcherThreadV2(BlockingQueue<EventDispatchingRequest> queue,
			EventQueueService service)
	{
		this.queue = queue;
		this.service = service;
	}
	
	@Override
	public void run() {
		EventDispatchingRequest request;
		try {
			while ( (request = queue.take()) != null ) {
				if ( request == EventDispatchingRequest.EXIT ) {
					break;
				}
				for ( EventType type : ESUtils.getAllUniqueTypes(request.type) ) {
					Event event = request.factory.produceEvent(type);
					for ( EventListener listener : type.getListeners() ) {
						try {
							listener.onEvent(event);
						} catch ( Throwable t ) {
							Object args[] = { event, listener, t };
							logger.error("Error dispatching event {} to {}: {}", args);
						}
					}
				}
				service.eventDispatched();
			}
		} catch ( InterruptedException e ) {
			logger.error("Interrupted: ", e);
			Thread.currentThread().interrupt();
		} finally {
			service.shutdown();
		}
	}

}
