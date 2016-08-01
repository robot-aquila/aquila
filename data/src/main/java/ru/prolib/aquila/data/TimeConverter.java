package ru.prolib.aquila.data;

import java.time.Instant;

/**
 * Utility interface to convert one time to another.
 */
public interface TimeConverter {
	
	/**
	 * Convert time.
	 * <p>
	 * @param currentTime - current time which may be used as a base for calculation
	 * @param sourceTime - time to convert
	 * @return converted time
	 */
	public Instant convert(Instant currentTime, Instant sourceTime);
	
	/**
	 * Reset converter to its initial state.
	 */
	public void reset();

}
