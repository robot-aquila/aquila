package ru.prolib.aquila.data;

import java.io.Closeable;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Symbol state update source interface.
 */
public interface SymbolUpdateSource extends Closeable {

	/**
	 * Subscribe for state updates of specified symbol.
	 * <p>
	 * @param symbol - the symbol to subscribe
	 * @param consumer - consumer instance
	 */
	public void subscribeSymbol(Symbol symbol, DeltaUpdateConsumer consumer);
	
	/**
	 * Unsubscribe of state updates of specified symbol.
	 * <p>
	 * @param symbol - the symbol to unsubscribe
	 * @param consumer - consumer instance
	 */
	public void unsubscribeSymbol(Symbol symbol, DeltaUpdateConsumer consumer);

}
