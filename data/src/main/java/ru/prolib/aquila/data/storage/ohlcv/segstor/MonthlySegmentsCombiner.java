package ru.prolib.aquila.data.storage.ohlcv.segstor;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthlySegmentStorage;

public class MonthlySegmentsCombiner implements CloseableIterator<Candle> {
	private final SymbolMonthlySegmentStorage<Candle> smss;
	private final List<SymbolMonthly> segments;
	private CloseableIterator<Candle> segmentReader;
	private boolean closed = false;

	public MonthlySegmentsCombiner(SymbolMonthlySegmentStorage<Candle> smss,
			List<SymbolMonthly> segments)
	{
		this.smss = smss;
		this.segments = segments;
	}
	
	/**
	 * For testing purposes only.
	 * <p>
	 * @return - list of segments to read
	 */
	List<SymbolMonthly> getSegments() {
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
	
	public SymbolMonthlySegmentStorage<Candle> getSMSS() {
		return smss;
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
				segmentReader = smss.createReader(segments.remove(0));
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
		if ( other == null || other.getClass() != MonthlySegmentsCombiner.class ) {
			return false;
		}
		MonthlySegmentsCombiner o = (MonthlySegmentsCombiner) other;
		return new EqualsBuilder()
				.append(o.smss, smss)
				.append(o.segments, segments)
				.append(o.closed, closed)
				.append(o.segmentReader, segmentReader)
				.isEquals();
	}

}
