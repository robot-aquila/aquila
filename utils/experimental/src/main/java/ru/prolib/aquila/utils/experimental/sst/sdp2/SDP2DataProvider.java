package ru.prolib.aquila.utils.experimental.sst.sdp2;

import java.util.Collection;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.TimeFrame;

public interface SDP2DataProvider<T extends SDP2Key> {
	
	/**
	 * Get slice by key.
	 * <p>
	 * This method will create new slice if it does not exists.
	 * <p>
	 * @param key - key of slice
	 * @return data slice
	 */
	SDP2DataSlice<T> getSlice(T key);
	
	/**
	 * Get slices tied to the symbol.
	 * <p>
	 * @param symbol - symbol
	 * @return collection of slices
	 */
	Collection<SDP2DataSlice<T>> getSlices(Symbol symbol);
	
	/**
	 * Get all slices not tied to symbol.
	 * <p>
	 * @return collection of slices
	 */
	Collection<SDP2DataSlice<T>> getSlicesWoSymbol();
	
	/**
	 * Get all available slices.
	 * <p>
	 * @return collection of slices
	 */
	Collection<SDP2DataSlice<T>> getSlices();
	
	/**
	 * Get all available symbols.
	 * <p>
	 * @return collection of symbols
	 */
	Collection<Symbol> getSymbols();
	
	/**
	 * Get timerfames of all available slices for symbol.
	 * <p>
	 * @param symbol - symbol
	 * @return collection of timeframes
	 */
	Collection<TimeFrame> getTimeFrames(Symbol symbol);
	
	/**
	 * Get timeframes of all available slices not tied to symbol.
	 * <p>
	 * @return collection of timeframes
	 */
	Collection<TimeFrame> getTimeFramesWoSymbol();

}
