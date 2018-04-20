package ru.prolib.aquila.utils.experimental.sst.sdp2;

import java.util.Collection;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.ZTFrame;

public interface SDP2DataProvider<T extends SDP2Key> {
	
	/**
	 * Get slice by key.
	 * <p>
	 * This method will create new slice if it does not exists.
	 * <p>
	 * @param key - key of slice
	 * @return data slice
	 * @throws IllegalArgumentException - if slice is not exists
	 */
	SDP2DataSlice<T> getSlice(T key);
	
	/**
	 * Create a new slice.
	 * <p>
	 * @param key - key of slice
	 * @return data slice
	 * @throws IllegalArgumentException - if slice already exists
	 */
	SDP2DataSlice<T> createSlice(T key);
	
	/**
	 * Remove existing slice.
	 * <p>
	 * This method has no effect if slice does not exists.  If it exists then purge will remove
	 * the slice from the internal registry and call {@link SDP2DataSlice#close()} method.
	 * <p>
	 * @param key - key of slice to purge
	 */
	void purgeSlice(T key);
	
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
	Collection<ZTFrame> getTimeFrames(Symbol symbol);
	
	/**
	 * Get timeframes of all available slices not tied to symbol.
	 * <p>
	 * @return collection of timeframes
	 */
	Collection<ZTFrame> getTimeFramesWoSymbol();

}
