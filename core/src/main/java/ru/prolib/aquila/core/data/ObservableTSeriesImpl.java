package ru.prolib.aquila.core.data;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.tseries.TSeriesUpdateEventFactory;

public class ObservableTSeriesImpl<T> implements ObservableTSeries<T>, EditableTSeries<T> {
	protected final EventQueue queue;
	protected final EditableTSeries<T> series;
	private final EventType onUpdate;
	
	public ObservableTSeriesImpl(EventQueue queue, EditableTSeries<T> series) {
		this.queue = queue;
		this.series = series;
		onUpdate = new EventTypeImpl(series.getId() + ".UPDATE");
	}
	
	public EventQueue getEventQueue() {
		return queue;
	}
	
	public EditableTSeries<T> getUnderlyingSeries() {
		return series;
	}

	@Override
	public T get(Instant time) {
		return series.get(time);
	}

	@Override
	public ZTFrame getTimeFrame() {
		return series.getTimeFrame();
	}

	@Override
	public String getId() {
		return series.getId();
	}

	@Override
	public T get() throws ValueException {
		return series.get();
	}

	@Override
	public T get(int index) throws ValueException {
		return series.get(index);
	}

	@Override
	public int getLength() {
		return series.getLength();
	}

	@Override
	public LID getLID() {
		return series.getLID();
	}

	@Override
	public void lock() {
		series.lock();
	}

	@Override
	public void unlock() {
		series.unlock();
	}

	@Override
	public EventType onUpdate() {
		return onUpdate;
	}

	@Override
	public TSeriesUpdate set(Instant time, T value) {
		TSeriesUpdate update = series.set(time, value);
		if ( update.hasChanged() ) {
			queue.enqueue(onUpdate, new TSeriesUpdateEventFactory(update));
		}
		return update;
	}

	@Override
	public void clear() {
		series.clear();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ObservableTSeriesImpl.class ) {
			return false;
		}
		ObservableTSeriesImpl<?> o = (ObservableTSeriesImpl<?>) other;
		return new EqualsBuilder()
				.append(queue, o.queue)
				.append(series, o.series)
				.isEquals();
	}

	@Override
	public int toIndex(Instant time) {
		return series.toIndex(time);
	}

}
