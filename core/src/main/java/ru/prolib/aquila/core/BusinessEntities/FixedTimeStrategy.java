package ru.prolib.aquila.core.BusinessEntities;

import java.io.IOException;
import java.time.Instant;

public class FixedTimeStrategy implements TimeStrategy {
	private final Instant fixedTime;
	
	public FixedTimeStrategy(Instant fixedTime) {
		this.fixedTime = fixedTime;
	}

	@Override
	public Instant getTime() {
		return fixedTime;
	}

	@Override
	public void close() throws IOException {
		
	}
	
}