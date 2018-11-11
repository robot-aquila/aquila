package ru.prolib.aquila.core.eque;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;

public class DeliveryEventTask implements Callable<Object> {
	private static final Logger logger;
	public static final DeliveryEventTask EXIT;
	
	static {
		logger = LoggerFactory.getLogger(DeliveryEventTask.class);
		EXIT = new DeliveryEventTask(null, null, null);			
	}
	
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