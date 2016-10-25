package ru.prolib.aquila.data;

import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;

public class TimeLimitedL1UpdateIterator implements CloseableIterator<L1Update> {
	private final CloseableIterator<L1Update> source;
	private final Instant startTime;
	private boolean closed = false;
	private L1Update lastUpdate;
	
	public TimeLimitedL1UpdateIterator(CloseableIterator<L1Update> source,
			Instant startTime)
	{
		this.source = source;
		this.startTime = startTime;
	}

	@Override
	public void close() throws IOException {
		if ( ! closed ) {
			closed = true;
			IOUtils.closeQuietly(source);
		}
	}

	@Override
	public boolean next() throws IOException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		lastUpdate = null;
		while ( source.next() ) {
			L1Update update = source.item();
			if ( ! update.getTime().isBefore(startTime) ) {
				lastUpdate = update;
				return true;
			}
		}
		return false;
	}

	@Override
	public L1Update item() throws IOException, NoSuchElementException {
		if ( closed ) {
			throw new IOException("Iterator closed");
		}
		if ( lastUpdate == null ) {
			throw new NoSuchElementException();
		}
		return lastUpdate;
	}

}
