package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import java.util.concurrent.locks.Lock;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioException;
import ru.prolib.aquila.core.BusinessEntities.PositionImpl;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsImpl;

public class PositionParamsBuilder extends OSCParamsBuilder {
	protected Terminal terminal;
	protected Account account;
	protected Symbol symbol;
	protected Security security;
	protected Portfolio portfolio;
	
	public PositionParamsBuilder(EventQueue queue) {
		super(queue);
	}
	
	public PositionParamsBuilder() {
		super();
	}
	
	@Override
	public PositionParamsBuilder withID(String id) {
		super.withID(id);
		return this;
	}
	
	@Override
	public PositionParamsBuilder withEventDispatcher(EventDispatcher dispatcher) {
		super.withEventDispatcher(dispatcher);
		return this;
	}
	
	@Override
	public PositionParamsBuilder withController(OSCController controller) {
		super.withController(controller);
		return this;
	}
	
	@Override
	public PositionParamsBuilder withLock(Lock lock) {
		super.withLock(lock);
		return this;
	}
	
	public PositionParamsBuilder withTerminal(Terminal terminal) {
		this.terminal = terminal;
		return this;
	}
	
	public PositionParamsBuilder withAccount(Account account) {
		this.account = account;
		return this;
	}
	
	public PositionParamsBuilder withSymbol(Symbol symbol) {
		this.symbol = symbol;
		return this;
	}
	
	public PositionParamsBuilder withSecurity(Security security) {
		this.security = security;
		return this;
	}
	
	public PositionParamsBuilder withPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
		return this;
	}

	@Override
	public PositionParams buildParams() {
		return (PositionParams) super.buildParams();
	}
	
	protected Terminal getTerminal() {
		if ( terminal == null ) {
			throw new IllegalStateException("Undefined terminal");
		}
		return terminal;
	}
	
	protected Account getAccount() {
		if ( account == null ) {
			throw new IllegalStateException("Undefined account");
		}
		return account;
	}

	protected Symbol getSymbol() {
		if ( symbol == null ) {
			throw new IllegalStateException("Undefined symbol");
		}
		return symbol;
	}
	
	protected Security getSecurity() {
		if ( security == null ) {
			return getDefaultSecurity();
		} else {
			return security;
		}
	}
	
	protected Portfolio getPortfolio() {
		if ( portfolio == null ) {
			return getDefaultPortfolio();
		} else {
			return portfolio;
		}
	}
	
	@Override
	protected String getDefaultID() {
		return String.format("%s.%s[%s].POSITION", getTerminal().getTerminalID(),
				getAccount(), getSymbol());
	}
	
	@Override
	protected OSCController getDefaultController() {
		return new PositionImpl.PositionController();
	}
	
	protected Security getDefaultSecurity() {
		try {
			return getTerminal().getSecurity(getSymbol());
		} catch ( SecurityException e ) {
			throw new IllegalStateException("Error accessing security", e);
		}
	}
	
	protected Portfolio getDefaultPortfolio() {
		try {
			return getTerminal().getPortfolio(getAccount());
		} catch (PortfolioException e) {
			throw new IllegalStateException("Error accessing portfolio", e);
		}
	}
	
	@Override
	protected OSCParamsImpl createParams() {
		PositionParamsImpl params = new PositionParamsImpl();
		params.setTerminal(getTerminal());
		params.setAccount(getAccount());
		params.setSymbol(getSymbol());
		params.setSecurity(getSecurity());
		params.setPortfolio(getPortfolio());
		return params;
	}

}
