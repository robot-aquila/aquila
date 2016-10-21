package ru.prolib.aquila.probe.datasim.symbol;

import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Interface of a reader factory interface.
 * <p>
 * This interface represents a factory to produce readers of symbol state
 * updates. Any data storage may be used in combination with the symbol update
 * source classes by implementing this interface.
 */
public interface SymbolUpdateReaderFactory {
	
	/**
	 * Create an update reader.
	 * <p>
	 * @param symbol - the symbol to read updates of
	 * @param startTime - start time for the data. The first update of produced
	 * iterator must be dated with the time which is equals or greater than the
	 * start time.
	 * @return iterator of the symbol state updates
	 * @throws IOException - an error occurred
	 */
	public CloseableIterator<DeltaUpdate>
		createReader(Symbol symbol, Instant startTime) throws IOException;

}
