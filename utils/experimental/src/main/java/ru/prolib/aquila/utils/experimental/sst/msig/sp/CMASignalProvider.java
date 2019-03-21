package ru.prolib.aquila.utils.experimental.sst.msig.sp;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.ObservableSeriesOLD;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.SeriesEvent;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignal;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignalProvider;

/**
 * Crossing moving averages.
 */
public class CMASignalProvider implements EventListener, MarketSignalProvider {
	private final ObservableSeriesOLD<Candle> candles;
	private final Series<CDecimal> maShort, maLong;
	private final MarketSignal signal;
	private final TAMath math;
	
	public CMASignalProvider(ObservableSeriesOLD<Candle> candles,
			Series<CDecimal> maShort, Series<CDecimal> maLong, 
			MarketSignal signal, TAMath math)
	{
		this.candles = candles;
		this.maLong = maLong;
		this.maShort = maShort;
		this.signal = signal;
		this.math = math;
	}
	
	public Series<CDecimal> getMALong() {
		return maLong;
	}
	
	public Series<CDecimal> getMAShort() {
		return maShort;
	}
	
	@Override
	public MarketSignal getSignal() {
		return signal;
	}
	
	@Override
	public void start() {
		candles.onAdd().addListener(this);
	}
	
	@Override
	public void stop() {
		candles.onAdd().removeListener(this);
	}

	@Override
	public void onEvent(Event evt) {
		@SuppressWarnings("unchecked")
		SeriesEvent<Candle> event = (SeriesEvent<Candle>) evt;
		int index = event.getIndex() - 1;
		try {
			if ( math.crossOver(maShort, maLong, index) ) {
				Candle c = candles.get(index);
				signal.fireBullish(c.getStartTime(), c.getClose());
			} else
			if ( math.crossUnder(maShort, maLong, index) ) {
				Candle c = candles.get(index);
				signal.fireBearish(c.getStartTime(), c.getClose());
			}
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception", e);
		}
	}
	
}