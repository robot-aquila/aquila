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
import ru.prolib.aquila.utils.experimental.sst.msig.BreakSignal;
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignal;

public class RobotData {
	private final EditableTerminal terminal;
	private final RobotConfig config;
	private final BreakSignal breakSignal;
	private MarketSignal marketSignal;
	private RobotDataSliceTracker dataSliceTracker;
	private RobotStateListener stateListener;
	
	public RobotData(EditableTerminal terminal, RobotConfig config, BreakSignal breakSignal) {
		this.terminal = terminal;
		this.config = config;
		this.breakSignal = breakSignal;
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
	
	public BreakSignal getBreakSignal() {
		return breakSignal;
	}
	
	public synchronized MarketSignal getMarketSignal() {
		if ( marketSignal == null ) {
			throw new IllegalStateException("Market signal not defined");
		}
		return marketSignal;
	}
	
	public synchronized void setMarketSignal(MarketSignal signal) {
		if ( this.marketSignal != null ) {
			throw new IllegalStateException("Market signal already defined");
		}
		this.marketSignal = signal;
	}
	
	public synchronized RobotDataSliceTracker getDataSliceTracker() {
		if ( dataSliceTracker == null ) {
			throw new IllegalStateException("Data slice tracker is not defined");
		}
		return dataSliceTracker;
	}
	
	public synchronized void setDataSliceTracker(RobotDataSliceTracker tracker) {
		if ( this.dataSliceTracker != null ) {
			throw new IllegalStateException("Data slice already defined");
		}
		this.dataSliceTracker = tracker;
	}
	
	public synchronized void setStateListener(RobotStateListener stateListener) {
		if ( this.stateListener != null ) {
			throw new IllegalStateException("State listener already defined");
		}
		this.stateListener = stateListener;
	}

	public RobotStateListener getStateListener() {
		if ( stateListener == null ) {
			throw new IllegalStateException("State listener not defined");
		}
		return stateListener;
	}
}
