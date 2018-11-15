package ru.prolib.aquila.core.data.tseries;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesByLastTrade;

/**
 * Common data handler of chart data based on price in OHLC form and traded
 * volumes data.
 */
public class SecurityChartDataHandler implements STSeriesHandler {
	
	public interface HandlerSetup {
		Symbol getSymbol();
		ZTFrame getTimeFrame();
		Terminal getTerminal();
		EventQueue getEventQueue();
		String getSharedSeriesID();
		String getOhlcSeriesID();
		String getOhlcMutatorSeriesID();
		void loadInitialData(EditableTSeries<Candle> ohlc);
		void createDerivedSeries(STSeries source,
				TSeriesCacheController<Candle> cache,
				TSeries<Candle> ohlc);
	}
	
	public interface Factory {
		STSeries createSeries();
		TSeriesCacheControllerETS<Candle> createCacheCtrl(EditableTSeries<Candle> ohlc);
		CandleSeriesByLastTrade createOhlcProducer(EditableTSeries<Candle> ohlc);
	}
	
	public static class FactoryImpl implements Factory {
		private final HandlerSetup setup;
		
		public FactoryImpl(HandlerSetup setup) {
			this.setup = setup;
		}
		
		@Override
		public STSeries createSeries() {
			return new STSeries(
					setup.getSharedSeriesID(),
					setup.getTimeFrame(),
					setup.getEventQueue()
				);
		}

		@Override
		public TSeriesCacheControllerETS<Candle> createCacheCtrl(EditableTSeries<Candle> ohlc) {
			return new TSeriesCacheControllerETS<Candle>(ohlc);
		}

		@Override
		public CandleSeriesByLastTrade createOhlcProducer(EditableTSeries<Candle> ohlc) {
			return new CandleSeriesByLastTrade(
					ohlc,
					setup.getTerminal(),
					setup.getSymbol()
				);
		}

	}
	
	private final HandlerSetup setup;
	private final Factory factory;
	private boolean initialized, started, closed;
	private STSeries source;
	private CandleSeriesByLastTrade ohlcProducer;
	
	SecurityChartDataHandler(HandlerSetup setup, Factory factory) {
		this.setup = setup;
		this.factory = factory;
	}
	
	public SecurityChartDataHandler(HandlerSetup setup) {
		this(setup, new FactoryImpl(setup));
	}
	
	public HandlerSetup getSetup() {
		return setup;
	}
	
	public Factory getFactory() {
		return factory;
	}
	
	public CandleSeriesByLastTrade getOhlcProducer() {
		return ohlcProducer;
	}
	
	@Override
	public synchronized STSeries getSeries() {
		return source;
	}
	
	@Override
	public synchronized void initialize() {
		if ( initialized ) {
			throw new IllegalStateException("Already initialized");
		}

		source = factory.createSeries();
		EditableTSeries<Candle> ohlc = source.createSeries(setup.getOhlcSeriesID(), false);
		TSeriesCacheControllerETS<Candle> cache = factory.createCacheCtrl(ohlc);
		// OHLC mutator+cache controller will clear caches on close
		source.registerRawSeries(cache, setup.getOhlcMutatorSeriesID());
		
		setup.loadInitialData(ohlc);
		setup.createDerivedSeries(source, cache, ohlc);

		ohlcProducer = factory.createOhlcProducer(cache);
		
		initialized = true;
	}
	
	@Override
	public synchronized void startDataHandling() {
		if ( started ) {
			return;
		}
		if ( ! initialized ) {
			throw new IllegalStateException("Handler is not initialized");
		}
		if ( closed ) {
			throw new IllegalStateException("Handler is closed");
		}
		ohlcProducer.start();
		
		started = true;
	}
	
	@Override
	public synchronized void stopDataHandling() {
		if ( started ) {
			ohlcProducer.stop();
			
			started = false;
		}
	}
	
	@Override
	public synchronized void close() {
		if ( started ) {
			throw new IllegalStateException("Handler is started");
		}
		
		closed = true;
	}

}
