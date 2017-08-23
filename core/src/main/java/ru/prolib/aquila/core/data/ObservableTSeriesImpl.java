package ru.prolib.aquila.core.data;

import java.time.Instant;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.concurrency.LID;

public class ObservableTSeriesImpl<T> implements ObservableTSeries<T> {
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
	public TimeFrame getTimeFrame() {
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

}
