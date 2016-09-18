package ru.prolib.aquila.data;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;

/**
 * This class allows to organize a cyclic reading of some data using the reader
 * factory and number of repeats. The number of repeats may be zero (that means
 * repeat infinitely) or positive integer (that means repeat several times). 
 * <p>
 * @param <T> - type of reading data
 */
public class CyclicReader<T> implements CloseableIterator<T> {
	public static final int REPEAT_INFINITELY = 0;
	protected final ReaderFactory<? extends T> factory;
	protected final int repeat;
	protected CloseableIterator<? extends T> reader;
	protected int round;
	protected boolean closed;
	
	public CyclicReader(ReaderFactory<? extends T> factory, int repeat) {
		if ( repeat < 0 ) {
			throw new IllegalArgumentException("Number of repeats cannot be less than zero: " + repeat);
		}
		this.factory = factory;
		this.repeat = repeat;
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			closeReader();
			closed = true;
		}
	}

	@Override
	public boolean next() throws IOException {
		if ( closed ) {
			return false;
		}
		if ( reader == null ) {
			reader = factory.createReader();
			if ( ! reader.next() ) {
				// Empty file. Stop reading.
				close();
				return false;
			}
			return true;
		}
		if ( reader.next() ) {
			return true;
		}
		closeReader();
		round ++;
		if ( repeat == REPEAT_INFINITELY || round < repeat ) {
			return next();
		}
		return false;
	}

	@Override
	public T item() throws IOException, NoSuchElementException {
		if ( reader != null ) {
			return reader.item();
		}
		throw new NoSuchElementException();
	}
	
	private void closeReader() {
		IOUtils.closeQuietly(reader);
		reader = null;
	}

}
