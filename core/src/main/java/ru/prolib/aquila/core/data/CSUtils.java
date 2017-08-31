package ru.prolib.aquila.core.data;

import java.time.Instant;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
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
		series.lock();
		try {
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
		} finally {
			series.unlock();
		}
	}
	
	/**
	 * Aggregate a trade to candle series.
	 * <p>
	 * @param series - target series
	 * @param lastTrade - tick trade info
	 * @throws ValueException an error occurred
	 */
	public void aggregate(EditableTSeries<Candle> series, Tick lastTrade)
			throws ValueException
	{
		series.lock();
		try {
			Instant time = lastTrade.getTime();
			Candle candle = series.get(time);
			if ( candle == null ) {
				candle = new Candle(series.getTimeFrame().getInterval(time),
						lastTrade.getPrice(), lastTrade.getSize());
			} else {
				candle = candle.addTick(lastTrade);
			}
			series.set(time, candle);
		} finally {
			series.unlock();
		}
	}
	
	public CSFiller createFiller(Security security, TimeFrame tf,
			ObservableSeriesImpl<Candle> candles)
	{
		return new CSLastTradeFiller(security, tf, candles, this);
	}
	
	public CSFiller createFiller(Terminal terminal, Symbol symbol, TimeFrame tf,
			ObservableSeriesImpl<Candle> candles)
	{
		try {
			return createFiller(terminal.getSecurity(symbol), tf, candles);
		} catch ( SecurityException e ) {
			throw new IllegalStateException(e);
		}
	}
	
	public CSFiller createFiller(Terminal terminal, Symbol symbol, TimeFrame tf) {
		return createFiller(terminal, symbol, tf, createCandleSeries(terminal));
	}
	
	public ObservableSeriesImpl<Candle> createCandleSeries(EventQueue queue) {
		return new ObservableSeriesImpl<Candle>(queue, new SeriesImpl<>());
	}
	
	public ObservableSeriesImpl<Candle> createCandleSeries(EventQueue queue, String id) {
		return new ObservableSeriesImpl<Candle>(queue, new SeriesImpl<>(id));
	}
	
	public ObservableSeriesImpl<Candle> createCandleSeries(Terminal terminal) {
		return createCandleSeries(((EditableTerminal) terminal).getEventQueue());
	}
	
	public ObservableSeriesImpl<Candle> createCandleSeries(Terminal terminal, String id) {
		return createCandleSeries(((EditableTerminal) terminal).getEventQueue(), id);
	}

}
