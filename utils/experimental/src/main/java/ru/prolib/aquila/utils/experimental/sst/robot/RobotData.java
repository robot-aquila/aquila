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
import ru.prolib.aquila.utils.experimental.sst.msig.MarketSignal;

public class RobotData {
	private final EditableTerminal terminal;
	private final RobotConfig config;
	private final MarketSignal signal;
	
	public RobotData(EditableTerminal terminal, RobotConfig config, MarketSignal signal) {
		this.terminal = terminal;
		this.config = config;
		this.signal = signal;
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
	
	public MarketSignal getSignal() {
		return signal;
	}

}
