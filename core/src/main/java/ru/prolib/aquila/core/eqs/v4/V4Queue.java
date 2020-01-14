package ru.prolib.aquila.core.eqs.v4;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.prolib.aquila.core.FlushIndicator;
import ru.prolib.aquila.core.concurrency.SelectiveBarrier;
import ru.prolib.aquila.core.eque.EventDispatchingRequest;

public class V4Queue implements BlockingQueue<EventDispatchingRequest>, FlushIndicator {
	public static final long DEFAULT_TIMEOUT_MILLIS = 1000L;
	
	protected final BlockingQueue<EventDispatchingRequest> queue;
	protected final SelectiveBarrier barrier;
	protected final long timeout;
	
	public V4Queue(BlockingQueue<EventDispatchingRequest> basic_queue,
			SelectiveBarrier barrier,
			long put_timeout_millis)
	{
		this.queue = basic_queue;
		this.barrier = barrier;
		this.timeout = put_timeout_millis;
	}

	@Override
	public void put(EventDispatchingRequest request) throws InterruptedException {
		try {
			barrier.await(timeout, TimeUnit.MILLISECONDS);
		} catch ( TimeoutException e ) {
			throw new IllegalStateException(e);
		}
		queue.put(request);
	}
	
	@Override
	public void start() {
		
	}
	
	@Override
	public void waitForFlushing(long duration, TimeUnit unit) throws InterruptedException, TimeoutException {
		barrier.setAllowAll(false);
		queue.put(EventDispatchingRequest.FLUSH);
		barrier.await(duration, unit);
	}
	
	@Override
	public EventDispatchingRequest take() throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public EventDispatchingRequest poll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EventDispatchingRequest element() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EventDispatchingRequest peek() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EventDispatchingRequest remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends EventDispatchingRequest> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<EventDispatchingRequest> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(EventDispatchingRequest e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super EventDispatchingRequest> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super EventDispatchingRequest> c, int maxElements) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offer(EventDispatchingRequest e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean offer(EventDispatchingRequest e, long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public EventDispatchingRequest poll(long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int remainingCapacity() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	
}
