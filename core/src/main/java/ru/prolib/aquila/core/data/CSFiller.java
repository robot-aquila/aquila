package ru.prolib.aquila.core.data;

/**
 * Candle series filler.
 */
public interface CSFiller {

	/**
	 * Get series of candles under handling.
	 * <p>
	 * @return series of candles
	 */
	public ObservableSeries<Candle> getSeries();
	
	/**
	 * Get selected time frame.
	 * <p>
	 * @return selected time frame
	 */
	public TimeFrame getTF();
	
	/**
	 * Start gathering candles.
	 */
	public void start();
	
	/**
	 * Stop gathering candles.
	 */
	public void stop();
	
	/**
	 * Check filler is started.
	 * <p>
	 * @return true if started, false otherwise
	 */
	public boolean isStarted();
	
}