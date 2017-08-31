package ru.prolib.aquila.utils.experimental.sst.cs.msig;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.utils.experimental.sst.cs.CSDataProvider;
import ru.prolib.aquila.utils.experimental.sst.cs.CSDataSlice;
import ru.prolib.aquila.utils.experimental.sst.cs.CSIndicatorManager;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignal;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignalBuilder;
import ru.prolib.aquila.utils.experimental.sst.msig.sp.CMASignalProvider;

public class CMASignalBuilder implements MarketSignalBuilder {
	private final CSIndicatorManager ind;
	private final EventQueue queue;
	private final CSDataProvider dataProvider;
	private final Symbol symbol;
	private final TimeFrame tf;
	private final int shortPeriod, longPeriod;
	
	public CMASignalBuilder(EventQueue queue, CSDataProvider dataProvider,
			Symbol symbol, TimeFrame tf, int shortPeriod, int longPeriod,
			CSIndicatorManager ind)
	{
		this.ind = ind;
		this.queue = queue;
		this.dataProvider = dataProvider;
		this.symbol = symbol;
		this.tf = tf;
		this.shortPeriod = shortPeriod;
		this.longPeriod = longPeriod;
	}
	
	public CMASignalBuilder(EventQueue queue, CSDataProvider dataProvider,
			Symbol symbol, TimeFrame tf, int shortPeriod, int longPeriod)
	{
		this(queue, dataProvider, symbol, tf, shortPeriod, longPeriod,
				CSIndicatorManager.getInstance());
	}

	@Override
	public CMASignalProvider build(String signalID) {
		CSDataSlice slice = dataProvider.getSlice(symbol, tf);
		return new CMASignalProvider(slice.getCandleSeries(),
				ind.getQEMA(slice, shortPeriod),
				ind.getQEMA(slice, longPeriod),
				new MarketSignal(queue, signalID),
				TAMath.getInstance());
	}

}
