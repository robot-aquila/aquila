package ru.prolib.aquila.core.eque;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatchingMethodOriginal implements DispatchingMethod {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DispatchingMethodOriginal.class);
	}
	
	private final ExecutorService executor;
	
	public DispatchingMethodOriginal(ExecutorService executor) {
		this.executor = executor;
	}

	@Override
	public void dispatch(List<DeliveryEventTask> tasks) {
		try {
			for ( Future<Object> x : executor.invokeAll(tasks) ) {
				x.get();
			}
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

	@Override
	public void shutdown() {
		executor.shutdown();
		try {
			if ( ! executor.awaitTermination(10, TimeUnit.SECONDS) ) {
				executor.shutdownNow();
			}
		} catch ( InterruptedException e ) {
			executor.shutdownNow();
		}
	}
	
}