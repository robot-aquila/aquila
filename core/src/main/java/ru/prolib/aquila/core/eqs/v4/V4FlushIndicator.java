package ru.prolib.aquila.core.eqs.v4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.prolib.aquila.core.FlushIndicator;

public class V4FlushIndicator implements FlushIndicator {
	protected final V4Queue queue;
	protected final AtomicBoolean finished;
	
	public V4FlushIndicator(V4Queue queue) {
		this.queue = queue;
		this.finished = new AtomicBoolean(false);
	}

	@Override
	public void start() {
		
	}

	@Override
	public void waitForFlushing(long duration, TimeUnit unit) throws InterruptedException, TimeoutException {
		if ( finished.compareAndSet(false, true) ) {
			queue.waitForFlushing(duration, unit);
		}
	}

}
