package ru.prolib.aquila.core.data;

import java.io.IOException;
import java.util.NoSuchElementException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;

public class CloseableIteratorOfSeries<T> implements CloseableIterator<T> {
	private final Series<T> series;
	private int currentIndex = -1;
	private T lastValue;
	private boolean closed;
	
	public CloseableIteratorOfSeries(Series<T> series) {
		this.series = series;
	}

	@Override
	public synchronized void close() throws IOException {
		closed = true;
	}

	@Override
	public synchronized boolean next() throws IOException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		currentIndex ++;
		series.lock();
		try {
			if ( currentIndex >= series.getLength() ) {
				lastValue = null;
				return false;
			} else {
				lastValue = series.get(currentIndex);
				return true;
			}
		} catch ( ValueException e ) {
			throw new IOException("Unexpected exception: ", e);
		} finally {
			series.unlock();
		}
	}

	@Override
	public synchronized T item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		return lastValue;
	}

}
