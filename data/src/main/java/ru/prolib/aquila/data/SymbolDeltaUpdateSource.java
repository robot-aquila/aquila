package ru.prolib.aquila.data;

import java.io.Closeable;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * The symbol-associated delta-update source.
 */
public interface SymbolDeltaUpdateSource extends Closeable {

	/**
	 * Subscribe for delta-updates of specified symbol.
	 * <p>
	 * @param symbol - the symbol to subscribe
	 * @param consumer - consumer instance
	 */
	public void subscribeSymbol(Symbol symbol, DeltaUpdateConsumer consumer);
	
	/**
	 * Unsubscribe of delta-updates of specified symbol.
	 * <p>
	 * @param symbol - the symbol to unsubscribe
	 * @param consumer - consumer instance
	 */
	public void unsubscribeSymbol(Symbol symbol, DeltaUpdateConsumer consumer);

}
