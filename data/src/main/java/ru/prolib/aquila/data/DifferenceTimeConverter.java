package ru.prolib.aquila.data;

import java.time.Duration;
import java.time.Instant;

/**
 * A difference-based time converter.
 * <p>
 * This converter makes new instants by adding a time difference between the
 * current time and the source time which were passed at the first time. The
 * difference is kept until the {@link #reset()} call.
 */
public class DifferenceTimeConverter implements TimeConverter {
	private Duration diff;

	@Override
	public Instant convert(Instant currentTime, Instant sourceTime) {
		if ( diff == null ) {
			diff = Duration.between(sourceTime, currentTime);
			return currentTime;
		} else {
			return sourceTime.plus(diff);
		}
	}

	@Override
	public void reset() {
		diff = null;
	}

}
