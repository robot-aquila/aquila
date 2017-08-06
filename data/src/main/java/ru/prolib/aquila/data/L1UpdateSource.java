package ru.prolib.aquila.data;

import java.io.Closeable;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface L1UpdateSource extends Closeable {
	
	public void subscribeL1(Symbol symbol, L1UpdateConsumer consumer);
	
	public void unsubscribeL1(Symbol symbol, L1UpdateConsumer consumer);
	
	/**
	 * Set start time of expected data.
	 * <p>
	 * This is optional feature which may be supported or not by specific sources.
	 * It depends on actual data provider. Some data providers may give an access
	 * to historical data but many will give only real-time or delayed data.
	 * Commonly (if this call wasn't performed) the source will use current time
	 * or earliest possible time to determine the start time of data. See
	 * documentation on concrete L1 source implementation for details.
	 * <p>
	 * In general this option focuses to increase precision of market simulators
	 * when replayed recored data which may represents any past or future period.
	 * This method allow to specify exactly when expected period of data starts.
	 * Generally this call makes sense before first subscriber. How it works
	 * for subsequent calls depends on concrete implementation but usually it
	 * will no effect when reading of recorded data already started.
	 * <p>
	 * @param symbol - symbol to set the time
	 * @param time - start time of expected data
	 */
	public void setStartTimeL1(Symbol symbol, Instant time);

}
