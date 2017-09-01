package ru.prolib.aquila.utils.experimental.sst.msig.sp;

import java.time.Instant;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesEvent;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignal;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignalProvider;

public class CMASignalProviderTS implements EventListener, MarketSignalProvider {
	private final ObservableTSeries<Candle> candles;
	private final TSeries<Double> maShort, maLong;
	private final MarketSignal signal;
	private final TAMath math;
	private Instant lastUpdatedInterval;
	
	public CMASignalProviderTS(ObservableTSeries<Candle> candles, TSeries<Double> maShort,
			TSeries<Double> maLong, MarketSignal signal, TAMath math)
	{
		this.candles = candles;
		this.maShort = maShort;
		this.maLong = maLong;
		this.signal = signal;
		this.math = math;
	}
	
	public CMASignalProviderTS(ObservableTSeries<Candle> candles, TSeries<Double> maShort,
			TSeries<Double> maLong, MarketSignal signal)
	{
		this(candles, maShort, maLong, signal, TAMath.getInstance());
	}
	
	public CMASignalProviderTS(ObservableTSeries<Candle> candles, TSeries<Double> maShort,
			TSeries<Double> maLong, EventQueue queue, String signalID)
	{
		this(candles, maShort, maLong, new MarketSignal(queue, signalID));
	}

	public ObservableTSeries<Candle> getCandles() {
		return candles;
	}
	
	public TSeries<Double> getMAShort() {
		return maShort;
	}
	
	public TSeries<Double> getMALong() {
		return maLong;
	}
	
	@Override
	public MarketSignal getSignal() {
		return signal;
	}

	@Override
	public void start() {
		candles.onUpdate().addListener(this);
	}

	@Override
	public void stop() {
		candles.onUpdate().removeListener(this);
	}

	@Override
	public void onEvent(Event event) {
		TSeriesEvent<?> e = (TSeriesEvent<?>) event;
		Instant thisUpdatedInterval = e.getInterval().getStart();
		synchronized ( this ) {
			if ( thisUpdatedInterval.equals(lastUpdatedInterval) ) {
				return;
			} else {
				lastUpdatedInterval = thisUpdatedInterval;
			}
		}
		
		Multilock lock = new Multilock(candles, maShort, maLong);
		lock.lock();
		try {
			int index = -1;
			if ( candles.getLength() >= 2 ) {
				if ( math.crossOver(maShort, maLong, index) ) {
					Candle c = candles.get(index);
					signal.fireBullish(c.getStartTime(), c.getClose());
				} else
				if ( math.crossUnder(maShort, maLong, index) ) {
					Candle c = candles.get(index);
					signal.fireBearish(c.getStartTime(), c.getClose());
				}
			}
		} catch ( ValueException err ) {
			throw new IllegalStateException("Unexpected exception", err);
		} finally {
			lock.unlock();
		}
	}

}
