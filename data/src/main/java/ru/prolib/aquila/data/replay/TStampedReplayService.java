package ru.prolib.aquila.data.replay;

import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.TStamped;

/**
 * Timestamped objects replay service interface.
 * <p>
 * This interface declares methods which are required to run
 * a {@link ru.prolib.aquila.data.replay.TStampedReplay timestamp}-based replay.
 */
public interface TStampedReplayService {
	
	/**
	 * Create a new reader.
	 * <p>
	 * This method should open a new reader to iterate through a set of
	 * {@link ru.prolib.aquila.core.BusinessEntities.TStamped timestamped}
	 * objects. Each object from the set will be scheduled for consumption.
	 * Exact time of consumption is determined by calling the
	 * {@link #consumptionTime(Instant, TStamped)} method. Keep in mind that
	 * this method may called many times. In case of using the consumption time
	 * based at the first element in sequence some variables should be reset to
	 * its initial state after opening a new reader.
	 * <p>
	 * @return reader of set of {@link ru.prolib.aquila.core.BusinessEntities.TStamped timestamped} objects
	 * @throws IOException - an error occurred
	 */
	public CloseableIterator<? extends TStamped> createReader() throws IOException;
	
	/**
	 * Get a time to consume specified object.
	 * <p>
	 * @param currentTime - the current time
	 * @param object - object to consume
	 * @return a new time related to the current time which points when the object should be consumed
	 */
	public Instant consumptionTime(Instant currentTime, TStamped object);
	
	/**
	 * Mutate object to a new consumption time.
	 * <p>
	 * In many cases the source object should be converted to a new form
	 * according to a new consumption time. For example a tick which was read
	 * from a file should be recreated with a new time then consumed. This
	 * method is responsible to mutation of an object to a new time. If no
	 * mutation needed just return the passed object back.
	 * <p>
	 * @param object - the source object
	 * @param consumptionTime - the new consumption time
	 * @return mutated object
	 */
	public TStamped mutate(TStamped object, Instant consumptionTime);
	
	/**
	 * Consume object.
	 * <p>
	 * This method is called at the specified consumption time and may be used
	 * to do something with the object or to route it to an appropriate
	 * subsystem. 
	 * <p>
	 * @param object - object to consume
	 * @throws IOException - an error occurred
	 */
	public void consume(TStamped object) throws IOException;

}
