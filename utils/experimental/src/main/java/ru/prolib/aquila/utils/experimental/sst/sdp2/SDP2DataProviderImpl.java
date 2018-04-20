package ru.prolib.aquila.utils.experimental.sst.sdp2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.ZTFrame;

/**
 * Experimental feature!
 * <p>
 * Data provider organizes an access to series data using of data slices.
 * Data slice is a set of series associated with timeframe and symbol (optional).
 * This implementation is about managing the slices but not managing series.
 * Controlling series creation and filling is out of scope of this class.
 * <p>
 * @param <T> - storage key type
 */
public class SDP2DataProviderImpl<T extends SDP2Key> implements SDP2DataProvider<T> {
	private final SDP2DataSliceFactory<T> factory;
	private final Map<T, SDP2DataSlice<T>> slices;
	
	/**
	 * Service constructor. For testing purposes only.
	 * <p>
	 * @param factory - data slice factory
	 * @param slices - slices storage
	 */
	SDP2DataProviderImpl(SDP2DataSliceFactory<T> factory, Map<T, SDP2DataSlice<T>> slices) {
		this.factory = factory;
		this.slices = slices;
	}
	
	public SDP2DataProviderImpl(SDP2DataSliceFactory<T> factory) {
		this(factory, new HashMap<>());
	}
	
	/**
	 * Common constructor.
	 * <p>
	 * Will create provider with standard slice factory implementation.
	 * <p>
	 * @param queue - event queue
	 */
	public SDP2DataProviderImpl(EventQueue queue) {
		this(new SDP2DataSliceFactoryImpl<T>(queue));
	}

	public SDP2DataSliceFactory<T> getFactory() {
		return factory;
	}
	

	@Override
	public synchronized SDP2DataSlice<T> createSlice(T key) {
		SDP2DataSlice<T> slice = slices.get(key);
		if ( slice != null ) {
			throw new IllegalArgumentException("Slice already exists: " + key);
		}
		slice = factory.produce(key);
		slices.put(key, slice);
		return slice;
	}

	@Override
	public synchronized void purgeSlice(T key) {
		SDP2DataSlice<T> slice = slices.remove(key);
		if ( slice != null ) {
			slice.close();
		}
	}

	@Override
	public synchronized SDP2DataSlice<T> getSlice(T key) {
		SDP2DataSlice<T> slice = slices.get(key);
		if ( slice == null ) {
			throw new IllegalArgumentException("Slice not exists: " + key);

		}
		return slice;
	}

	@Override
	public synchronized Collection<SDP2DataSlice<T>> getSlices(Symbol symbol) {
		List<SDP2DataSlice<T>> result = new ArrayList<>();
		for ( Map.Entry<T, SDP2DataSlice<T>> x : slices.entrySet() ) {
			if ( symbol.equals(x.getKey().getSymbol()) ) {
				result.add(x.getValue());
			}
		}
		return result;
	}

	@Override
	public synchronized Collection<SDP2DataSlice<T>> getSlicesWoSymbol() {
		List<SDP2DataSlice<T>> result = new ArrayList<>();
		for ( Map.Entry<T, SDP2DataSlice<T>> x : slices.entrySet() ) {
			if ( x.getKey().getSymbol() == null ) {
				result.add(x.getValue());
			}
		}
		return result;
	}

	@Override
	public synchronized Collection<SDP2DataSlice<T>> getSlices() {
		return new ArrayList<>(slices.values());
	}

	@Override
	public synchronized Collection<Symbol> getSymbols() {
		Set<Symbol> result = new HashSet<>();
		for ( T x : slices.keySet() ) {
			if ( x.getSymbol() != null ) {
				result.add(x.getSymbol());
			}
		}
		return result;
	}

	@Override
	public synchronized Collection<ZTFrame> getTimeFrames(Symbol symbol) {
		Set<ZTFrame> result = new HashSet<>();
		for ( T x : slices.keySet() ) {
			if ( symbol.equals(x.getSymbol()) ) {
				result.add(x.getTimeFrame());
			}
		}
		return result;
	}

	@Override
	public synchronized Collection<ZTFrame> getTimeFramesWoSymbol() {
		Set<ZTFrame> result = new HashSet<>();
		for ( T x : slices.keySet() ) {
			if ( x.getSymbol() == null ) {
				result.add(x.getTimeFrame());
			}
		}
		return result;
	}

}
