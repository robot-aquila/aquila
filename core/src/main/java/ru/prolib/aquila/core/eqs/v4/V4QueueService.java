package ru.prolib.aquila.core.eqs.v4;

import java.util.concurrent.atomic.AtomicLong;

import ru.prolib.aquila.core.EventQueueStats;
import ru.prolib.aquila.core.FlushIndicator;
import ru.prolib.aquila.core.eque.EventQueueService;

public class V4QueueService implements EventQueueService {
	private final V4Queue queue;
	private final AtomicLong enqueued, sent, dispatched, preparingTime, dispatchingTime, deliveryTime;
	
	public V4QueueService(V4Queue queue,
			AtomicLong enqueued,
			AtomicLong sent,
			AtomicLong dispatched,
			AtomicLong preparing_time,
			AtomicLong dispatching_time,
			AtomicLong delivery_time)
	{
		this.queue = queue;
		this.enqueued = enqueued;
		this.sent = sent;
		this.dispatched = dispatched;
		this.preparingTime = preparing_time;
		this.dispatchingTime = dispatching_time;
		this.deliveryTime = delivery_time;
	}
	
	public V4QueueService(V4Queue queue) {
		this(queue, new AtomicLong(), new AtomicLong(), new AtomicLong(), new AtomicLong(), new AtomicLong(),
				new AtomicLong());
	}

	@Override
	public FlushIndicator createIndicator() {
		return new V4FlushIndicator(queue);
	}

	@Override
	public EventQueueStats getStats() {
		return new V4QueueStats(
				enqueued.get(),
				sent.get(),
				dispatched.get(),
				preparingTime.get(),
				dispatchingTime.get(),
				deliveryTime.get()
			);
	}

	@Override
	public void eventEnqueued() {
		enqueued.addAndGet(1L);
	}

	@Override
	public void eventSent() {
		sent.addAndGet(1L);
	}

	@Override
	public void eventDispatched() {
		dispatched.addAndGet(1L);
	}

	@Override
	public void addPreparingTime(long nanos) {
		preparingTime.addAndGet(nanos);
	}

	@Override
	public void addDispatchingTime(long nanos) {
		dispatchingTime.addAndGet(nanos);
	}

	@Override
	public void addDeliveryTime(long nanos) {
		deliveryTime.addAndGet(nanos);
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void eventDispatched(long preparing_time, long dispatching_time) {
		dispatched.addAndGet(1L);
		preparingTime.addAndGet(preparing_time);
		dispatchingTime.addAndGet(dispatching_time);
	}

}
