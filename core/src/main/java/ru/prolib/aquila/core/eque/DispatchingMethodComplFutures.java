package ru.prolib.aquila.core.eque;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DispatchingMethodComplFutures implements DispatchingMethod {
	private final ExecutorService executor;
	
	public DispatchingMethodComplFutures(ExecutorService executor) {
		this.executor = executor;
	}

	@Override
	public void dispatch(List<DeliveryEventTask> tasks) {
		int count = tasks.size();
		CompletableFuture<?> fss[] = new CompletableFuture[count];
		for ( int i = 0; i < count; i ++ ) {
			DeliveryEventTask task = tasks.get(i);
			fss[i] = CompletableFuture.supplyAsync(()-> {
				return task.call();
			}, executor);
		}
		CompletableFuture.allOf(fss).join();
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