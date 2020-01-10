package ru.prolib.aquila.probe.scheduler.utils;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.FlushIndicator;
import ru.prolib.aquila.probe.ThreadSynchronizer;

public class EventQueueSynchronizerV2 implements ThreadSynchronizer {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EventQueueSynchronizerV2.class);
	}
	
	private final EventQueue queue;
	private FlushIndicator indicator;
	
	public EventQueueSynchronizerV2(EventQueue queue) {
		this.queue = queue;
	}
	
	@Override
	public void beforeExecution(Instant currentTime) {
		if ( indicator != null ) {
			throw new IllegalStateException();
		}
		indicator = queue.newFlushIndicator();
		indicator.start();
	}

	@Override
	public void afterExecution(Instant currentTime) {
		
	}

	@Override
	public void waitForThread(Instant currentTime) throws InterruptedException {
		try {
			indicator.waitForFlushing(1, TimeUnit.SECONDS);
		} catch ( TimeoutException e ) {
			logger.error("Timeout flushing queue: ", e);
		} finally {
			indicator = null;
		}
	}

}
