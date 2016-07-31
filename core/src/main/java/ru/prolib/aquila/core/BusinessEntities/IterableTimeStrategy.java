package ru.prolib.aquila.core.BusinessEntities;

import java.io.IOException;
import java.time.Instant;

public class IterableTimeStrategy implements TimeStrategy {
	private final CloseableIterator<Instant> iterator;
	
	public IterableTimeStrategy(CloseableIterator<Instant> iterator) {
		this.iterator = iterator;
	}

	@Override
	public Instant getTime() {
		try {
			iterator.next();
			return iterator.item();
		} catch ( IOException e ) {
			throw new RuntimeException("Failed to get a next timestamp: ", e);
		}
	}

	@Override
	public void close() throws IOException {
		iterator.close();
	}
	
}