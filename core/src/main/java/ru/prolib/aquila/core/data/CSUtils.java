package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.Tick;

/**
 * Candle series utilities.
 */
public class CSUtils {
	
	/**
	 * Aggregate a trade to candle series.
	 * <p>
	 * @param series - target series
	 * @param tf - timeframe
	 * @param lastTrade - a trade tick instance
	 * @return true if the data was aggregated, false otherwise
	 * @throws ValueException - an error occurred
	 */
	public boolean aggregate(EditableSeries<Candle> series, TimeFrame tf, Tick lastTrade)
			throws ValueException
	{
		synchronized ( series ) {
			if ( series.getLength() > 0 ) {
				Candle lastCandle = series.get();
				if ( lastCandle.getInterval().contains(lastTrade.getTime()) ) {
					series.set(lastCandle.addTick(lastTrade));
					return true;
				} else if ( lastTrade.getTime().isBefore(lastCandle.getEndTime()) ) {
					// Time in the past - incorrect time. Throw exception or ignore.
					return false;
				}
			}
			// zero series length or new future candle 
			series.add(new Candle(tf.getInterval(lastTrade.getTime()),
					lastTrade.getPrice(), lastTrade.getSize()));
			return true;
		}
	}

}
