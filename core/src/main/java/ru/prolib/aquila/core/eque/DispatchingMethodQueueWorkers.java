package ru.prolib.aquila.core.eque;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatchingMethodQueueWorkers implements DispatchingMethod {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DispatchingMethodQueueWorkers.class);
	}
	
	private final DeliveryEventWorkerPool pool;
	
	public DispatchingMethodQueueWorkers(String namePrefix, int numWorkers) {
		this.pool = new DeliveryEventWorkerPool(namePrefix, numWorkers);
	}

	@Override
	public void dispatch(List<DeliveryEventTask> tasks) {
		CountDownLatch finished = new CountDownLatch(tasks.size());
		pool.setFinishSignal(finished);
		for ( DeliveryEventTask task : tasks ) {
			try {
				pool.enqueue(task);
			} catch ( InterruptedException e ) {
				logger.error("Unexpected interruption: ", e);
			}
		}
		try {
			if ( ! finished.await(30, TimeUnit.SECONDS) ) {
				logger.warn("Event was not delivered in 0 seconds");
			}
		} catch ( InterruptedException e ) {
			logger.error("Unexpected interruption: ", e);
		}
	}

	@Override
	public void shutdown() {
		try {
			pool.shutdown();
		} catch ( InterruptedException e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

}
