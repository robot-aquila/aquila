package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleCloseSeries;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ta.QEMA;

public class SignalProvider {
	
	public static class CrossingMovingAverages implements EventListener {
		private final ObservableSeries<Candle> candles;
		private final Series<Double> maShort, maLong;
		private final Signal signal;
		private final TAMath math;
		
		public CrossingMovingAverages(ObservableSeries<Candle> candles,
				Series<Double> maShort, Series<Double> maLong, 
				Signal signal, TAMath math)
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
		
		public Signal getSignal() {
			return signal;
		}
		
		public void start() {
			candles.onAdd().addListener(this);
		}
		
		public void stop() {
			candles.onAdd().removeListener(this);
		}

		@Override
		public void onEvent(Event event) {
			int index = candles.getLength() - 2;
			try {
				if ( math.crossOver(maShort, maLong, index) ) {
					Candle c = candles.get(-1);
					signal.fireBullish(c.getStartTime(), c.getClose());
				} else
				if ( math.crossUnder(maShort, maLong, index) ) {
					Candle c = candles.get(-1);
					signal.fireBearish(c.getStartTime(), c.getClose());
				}
			} catch ( ValueException e ) {
				throw new IllegalStateException("Unexpected exception", e);
			}
		}
		
	}
	
	private final EventQueue queue;
	
	public SignalProvider(EventQueue queue) {
		this.queue = queue;
	}

	public CrossingMovingAverages crossingMAs(ObservableSeries<Candle> candles, int shortPeriod,
			int longPeriod, Signal signal)
	{
		Series<Double> close = new CandleCloseSeries(candles);
		return new CrossingMovingAverages(candles, new QEMA(close, shortPeriod),
				new QEMA(close, longPeriod), signal, new TAMath());
		
	}

	public CrossingMovingAverages crossingMAs(ObservableSeries<Candle> candles, int shortPeriod,
			int longPeriod)
	{
		return crossingMAs(candles, shortPeriod, longPeriod, new Signal(queue));
	}

}
