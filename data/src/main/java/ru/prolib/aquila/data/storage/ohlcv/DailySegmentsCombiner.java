package ru.prolib.aquila.data.storage.ohlcv;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolDailySegmentStorage;

public class DailySegmentsCombiner implements CloseableIterator<Candle> {
	private final SymbolDailySegmentStorage<Candle> segstor;
	private final List<SymbolDaily> segments;
	private CloseableIterator<Candle> segmentReader;
	private boolean closed = false;
	
	public DailySegmentsCombiner(SymbolDailySegmentStorage<Candle> segstor, List<SymbolDaily> segments) {
		this.segstor = segstor;
		this.segments = segments;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return - list of segments to read
	 */
	List<SymbolDaily> getSegments() {
		return segments;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @param reader - reader
	 */
	void setSegmentReader(CloseableIterator<Candle> reader) {
		this.segmentReader = reader;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return current segment reader
	 */
	CloseableIterator<Candle> getSegmentReader() {
		return segmentReader;
	}
	
	public SymbolDailySegmentStorage<Candle> getSegmentStorage() {
		return segstor;
	}
	
	/**
	 * Check that iterator is closed.
	 * <p>
	 * @return true if closed, false - otherwise
	 */
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			if ( segmentReader != null ) {
				segmentReader.close();
				segmentReader = null;
			}
			closed = true;
		}		
	}

	@Override
	public boolean next() throws IOException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		for ( ;; ) {
			if ( segmentReader != null ) {
				if ( segmentReader.next() ) {
					return true;
				} else {
					segmentReader.close();
				}
			}
			if ( segments.size() == 0 ) {
				return false;
			}
			try {
				segmentReader = segstor.createReader(segments.remove(0));
			} catch ( DataStorageException e ) {
				throw new IOException("Storage exception: ", e);
			}
		}
	}

	@Override
	public Candle item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		if ( segmentReader == null ) {
			throw new NoSuchElementException();
		}
		return segmentReader.item();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DailySegmentsCombiner.class ) {
			return false;
		}
		DailySegmentsCombiner o = (DailySegmentsCombiner) other;
		return new EqualsBuilder()
				.append(o.segstor, segstor)
				.append(o.segments, segments)
				.append(o.closed, closed)
				.append(o.segmentReader, segmentReader)
				.isEquals();
	}

}
