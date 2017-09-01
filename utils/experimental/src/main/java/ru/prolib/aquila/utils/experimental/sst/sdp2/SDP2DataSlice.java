package ru.prolib.aquila.utils.experimental.sst.sdp2;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TimeFrame;

public interface SDP2DataSlice<K extends SDP2Key> {
	
	/**
	 * Get key of the slice.
	 * <p>
	 * @return key
	 */
	K getKey();

	/**
	 * Get symbol of the slice.
	 * <p>
	 * @return symbol or null if slice is not tied to the symbol
	 */
	Symbol getSymbol();
	
	/**
	 * Get timeframe of the slice.
	 * <p>
	 * @return timeframe
	 */
	TimeFrame getTimeFrame();
	
	/**
	 * Get series of start time of existing slice intervals.
	 * <p>
	 * @return series
	 */
	ObservableTSeries<Instant> getIntervalStartSeries();
	
	/**
	 * Get observable series by ID.
	 * <p>
	 * @param seriesID - series ID
	 * @return series instance
	 * @throws IllegalStateException if series not exists or not observable
	 */
	<T> ObservableTSeries<T> getObservableSeries(String seriesID);
	
	/**
	 * Get series by ID.
	 * <p>
	 * @param seriesID - series ID
	 * @return series instance
	 * @throws IllegalStateException if series not exists
	 */
	<T> TSeries<T> getSeries(String seriesID);
	
	/**
	 * Check that series is exists.
	 * <p>
	 * @param seriesID - series ID
	 * @return true if series exists, false - otherwise
	 */
	boolean isExists(String seriesID);
	
	/**
	 * Check that series is observable.
	 * <p>
	 * @param seriesID - series ID
	 * @return true if seires observable, false - otherwise
	 * @throws IllegalStateException if series not exists
	 */
	boolean isObservable(String seriesID);
	
	/**
	 * Create new series.
	 * <p>
	 * @param seriesID - series ID.
	 * @param observable - create series observable if true. 
	 * @return new created series
	 * @throws IllegalStateException if series already exists
	 */
	<T> EditableTSeries<T> createSeries(String seriesID, boolean observable);
	
	/**
	 * Register series.
	 * <p>
	 * @param series - the series instance. This series may be not related to common storage.
	 * @throws IllegalStateException if series already exists
	 */
	<T> void registerRawSeries(TSeries<T> series);

}
