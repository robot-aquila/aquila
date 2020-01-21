package ru.prolib.aquila.data.replay;

import java.util.concurrent.atomic.AtomicBoolean;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFSymbol;

public class CandleReplayToSeriesStarter implements Starter {
	private final CandleReplayService service;
	private final Symbol symbol;
	private final EditableTSeries<Candle> target;
	private final AtomicBoolean started;
	private SubscrHandler handler;
	
	public CandleReplayToSeriesStarter(CandleReplayService service, Symbol symbol, EditableTSeries<Candle> target) {
		this.service = service;
		this.symbol = symbol;
		this.target = target;
		this.started = new AtomicBoolean(false);
	}
	
	@Override
	public synchronized void start() throws StarterException {
		if ( started.compareAndSet(false, true) ) {
			handler = service.subscribe(new TFSymbol(symbol, target.getTimeFrame()), new CandleReplayToSeries(target));
			handler.getConfirmation().join();
		}
	}

	@Override
	public synchronized void stop() throws StarterException {
		if ( started.compareAndSet(true, false) ) {
			if ( handler != null ) {
				handler.close();
				handler = null;
			}
		}
	}

}
