package ru.prolib.aquila.core.eqs.v4;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.concurrency.SelectiveBarrier;
import ru.prolib.aquila.core.eque.EventDispatchingRequest;
import ru.prolib.aquila.core.eque.EventQueueService;

public class V4Dispatcher implements Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(V4Dispatcher.class);
	}
	
	private final BlockingQueue<EventDispatchingRequest> queue;
	private final SelectiveBarrier barrier;
	private final EventQueueService service;
	
	public V4Dispatcher(BlockingQueue<EventDispatchingRequest> queue,
			SelectiveBarrier barrier,
			EventQueueService service)
	{
		this.queue = queue;
		this.barrier = barrier;
		this.service = service;
	}

	@Override
	public void run() {
		barrier.setAllowedThread(Thread.currentThread());
		try {
			boolean await_flush = false;
			for ( ;; ) {
				EventDispatchingRequest request = null;
				if ( await_flush ) {
					request = queue.poll();
					if ( request == null ) {
						barrier.setAllowAll(true);
						await_flush = false;
					}
				}
				if ( request == null ) {
					request = queue.take();
				}
				if ( request == EventDispatchingRequest.EXIT ) {
					break;
				} else if ( request == EventDispatchingRequest.FLUSH ) {
					await_flush = true;
				} else {
					long t1 = System.nanoTime();
					Set<EventType> types = request.type.getFullListOfRelatedTypes();
					long t2 = System.nanoTime();
					for ( EventType type : types ) {
						Event event = request.factory.produceEvent(type);
						for ( EventListener listener : type.getListeners() ) {
							try {
								listener.onEvent(event);
							} catch ( Exception t ) {
								logger.error("Error dispatching event {} to {}", event, listener);
								logger.error("Unhandled exception: ", t);
							}
						}
					}
					service.eventDispatched(t2 - t1, System.nanoTime() - t2);
				}
			}
		} catch ( InterruptedException e ) {
			logger.error("Interrupted: ", e);
			Thread.currentThread().interrupt();
		} finally {
			service.shutdown();
		}
	}

}
