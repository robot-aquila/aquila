package ru.prolib.aquila.core.data.tseries;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ZTFrame;

/**
 * Stub of setup of {@link SecurityChartDataHandler}.<br>
 * Useful for tests or as basis for more complex setups.
 */
public class SCDHSetupStub implements SecurityChartDataHandler.HandlerSetup {
	public static final String SID_SHARED = "CHART";
	public static final String SID_OHLC = "OHLC";
	public static final String SID_OHLC_MUTATOR = "OHLC_MUTATOR";
	
	private final Symbol symbol;
	private final ZTFrame tframe;
	private final EditableTerminal terminal;
	
	public SCDHSetupStub(Symbol symbol, ZTFrame tframe, EditableTerminal terminal) {
		this.symbol = symbol;
		this.tframe = tframe;
		this.terminal = terminal;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public ZTFrame getTimeFrame() {
		return tframe;
	}

	@Override
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public EventQueue getEventQueue() {
		return terminal.getEventQueue();
	}

	@Override
	public String getSharedSeriesID() {
		return SID_SHARED;
	}

	@Override
	public String getOhlcSeriesID() {
		return SID_OHLC;
	}

	@Override
	public String getOhlcMutatorSeriesID() {
		return SID_OHLC_MUTATOR;
	}

	@Override
	public void loadInitialData(EditableTSeries<Candle> ohlc) {
		
	}

	@Override
	public void createDerivedSeries(STSeries source,
			TSeriesCacheController<Candle> cache,
			TSeries<Candle> ohlc)
	{
		
	}

	@Override
	public void onStart() {
		
	}

	@Override
	public void onStop() {
		
	}

}
