package ru.prolib.aquila.utils.experimental.sst.robot;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioException;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.CSFiller;
import ru.prolib.aquila.core.data.CSUtils;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.ObservableSeriesImpl;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.sst.robot.sp.SPCrossingMovingAverages;

public class RobotData {
	private final EditableTerminal terminal;
	private final RobotConfig config;
	private final ObservableSeriesImpl<Candle> candles;
	private final SPCrossingMovingAverages sigProv;
	private CSFiller csFiller;
	
	public RobotData(EditableTerminal terminal, RobotConfig config, MarketSignal signal) {
		this.terminal = terminal;
		this.config = config;
		this.candles = new CSUtils().createCandleSeries(terminal);
		this.sigProv = new SignalProviderFactory(terminal.getEventQueue()).crossingMAs(candles, 7, 14, signal);
	}
	
	public Terminal getTerminal() {
		return terminal;
	}
	
	public EventQueue getEventQueue() {
		return ((EditableTerminal) terminal).getEventQueue();
	}
	
	public RobotConfig getConfig() {
		return config;
	}
	
	public Security getSecurity() {
		try {
			return terminal.getSecurity(getSymbol());
		} catch ( SecurityException e ) {
			throw new IllegalStateException(e);
		}
	}
	
	public Portfolio getPortfolio() {
		try {
			return terminal.getPortfolio(getAccount());
		} catch ( PortfolioException e ) {
			throw new IllegalStateException(e);
		}
	}
	
	public Position getPosition() {
		return getPortfolio().getPosition(getSymbol());
	}
	
	public Symbol getSymbol() {
		return config.getSymbol();
	}
	
	public Account getAccount() {
		return config.getAccount();
	}
	
	public RobotData setCSFiller(CSFiller filler) {
		this.csFiller = filler;
		return this;
	}
	
	public CSFiller getCSFiller() {
		return csFiller;
	}
	
	public Series<Double> getMALong() {
		return sigProv.getMALong();
	}
	
	public Series<Double> getMAShort() {
		return sigProv.getMAShort();
	}
	
	public ObservableSeriesImpl<Candle> getCandleSeries() {
		return candles;
	}
	
	public SPCrossingMovingAverages getSignalProvider() {
		return sigProv;
	}
	
	public MarketSignal getSignal() {
		return sigProv.getSignal();
	}

}
