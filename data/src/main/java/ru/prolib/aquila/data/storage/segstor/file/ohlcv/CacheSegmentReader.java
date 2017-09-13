package ru.prolib.aquila.data.storage.segstor.file.ohlcv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.NoSuchElementException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TimeFrame;

public class CacheSegmentReader implements CloseableIterator<Candle> {
	private final BufferedReader reader;
	private final TimeFrame timeFrame;
	private final CacheUtils utils;
	private Candle lastValue;
	private long lastNumber = -1;
	private boolean closed = false;
	
	public CacheSegmentReader(BufferedReader reader, TimeFrame timeFrame, CacheUtils utils) {
		this.reader = reader;
		this.timeFrame = timeFrame;
		this.utils = utils;
	}

	@Override
	public synchronized void close() throws IOException {
		if ( ! closed ) {
			reader.close();
			closed = true;
		}
	}

	@Override
	public synchronized boolean next() throws IOException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		lastNumber ++;
		String line = reader.readLine();
		if ( line == null ) {
			lastValue = null;
			return false;
		}
		try {
			lastValue = utils.parseOHLCVv1(line, timeFrame);
		} catch ( IOException e ) {
			throw new IOException(e.getMessage() + " at " + lastNumber, e);
		}
		return true;
	}

	@Override
	public synchronized Candle item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		return lastValue;
	}

}
