package ru.prolib.aquila.core.data.tseries;

/**
 * Interface of data handler to control shared time series.
 */
public interface STSeriesHandler {

	/**
	 * Initialize shared time series.
	 */
	void initialize();
	
	/**
	 * Get shared time series instance.
	 * <p>
	 * @return instance of shared time series
	 */
	STSeries getSeries();
	
	/**
	 * Begin data handling.
	 */
	void startDataHandling();
	
	/**
	 * Stop data handling.
	 */
	void stopDataHandling();
	
	/**
	 * Release resources and make cleanup. 
	 */
	void close();

}