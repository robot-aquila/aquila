package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleCloseSeries;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.ta.QEMA;
import ru.prolib.aquila.utils.experimental.sst.robot.sp.SPCrossingMovingAverages;

public class SignalProviderFactory {
	private final EventQueue queue;
	
	public SignalProviderFactory(EventQueue queue) {
		this.queue = queue;
	}

	public SPCrossingMovingAverages crossingMAs(ObservableSeries<Candle> candles, int shortPeriod,
			int longPeriod, MarketSignal signal)
	{
		Series<Double> close = new CandleCloseSeries(candles);
		return new SPCrossingMovingAverages(candles, new QEMA(close, shortPeriod),
				new QEMA(close, longPeriod), signal, new TAMath());
		
	}

	public SPCrossingMovingAverages crossingMAs(ObservableSeries<Candle> candles, int shortPeriod,
			int longPeriod)
	{
		return crossingMAs(candles, shortPeriod, longPeriod, new MarketSignal(queue));
	}

}
