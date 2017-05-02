package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class ObservableSeriesImpl<T> implements ObservableSeries<T>, EditableSeries<T> {
	protected final EventQueue queue;
	protected final EditableSeries<T> series;
	private final EventType onSet, onAdd;
	
	public ObservableSeriesImpl(EventQueue queue, EditableSeries<T> series) {
		this.queue = queue;
		this.series = series;
		this.onSet = new EventTypeImpl(series.getId() + ".SET");
		this.onAdd = new EventTypeImpl(series.getId() + ".ADD");
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
	public void set(T value) throws ValueException {
		series.set(value);
		queue.enqueue(onSet, new SeriesEventFactory(getLength() - 1, value));
	}

	@Override
	public void add(T value) throws ValueException {
		series.add(value);
		queue.enqueue(onAdd, new SeriesEventFactory(getLength() - 1, value));
	}

	@Override
	public void clear() {
		series.clear();
	}

	@Override
	public EventType onSet() {
		return onSet;
	}

	@Override
	public EventType onAdd() {
		return onAdd;
	}
	
	private static class SeriesEventFactory implements EventFactory {
		private final int index;
		private final Object value;
		
		public SeriesEventFactory(int index, Object value) {
			this.index = index;
			this.value = value;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Event produceEvent(EventType type) {
			return new SeriesEvent(type, index, value);
		}
		
	}

}
