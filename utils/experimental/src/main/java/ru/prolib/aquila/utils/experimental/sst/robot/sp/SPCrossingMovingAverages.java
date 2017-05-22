package ru.prolib.aquila.utils.experimental.sst.robot.sp;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.SeriesEvent;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.sst.robot.MarketSignal;
import ru.prolib.aquila.utils.experimental.sst.robot.SignalProvider;

public class SPCrossingMovingAverages implements EventListener, SignalProvider {
	private final ObservableSeries<Candle> candles;
	private final Series<Double> maShort, maLong;
	private final MarketSignal signal;
	private final TAMath math;
	
	public SPCrossingMovingAverages(ObservableSeries<Candle> candles,
			Series<Double> maShort, Series<Double> maLong, 
			MarketSignal signal, TAMath math)
	{
		this.candles = candles;
		this.maLong = maLong;
		this.maShort = maShort;
		this.signal = signal;
		this.math = math;
	}
	
	public Series<Double> getMALong() {
		return maLong;
	}
	
	public Series<Double> getMAShort() {
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