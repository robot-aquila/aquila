package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

/**
 * Interface of an object which is tied to a time point.
 */
public interface Timestamped {
	
	public Instant getTime();

}
