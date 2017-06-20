package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import java.util.concurrent.locks.Lock;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.OrderImpl;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioException;
import ru.prolib.aquila.core.BusinessEntities.Position;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsImpl;

public class OrderParamsBuilder extends OSCParamsBuilder {
	protected Terminal terminal;
	protected Account account;
	protected Symbol symbol;
	protected Long orderID;
	protected Security security;
	protected Portfolio portfolio;
	protected Position position;
	
	public OrderParamsBuilder(EventQueue queue) {
		super(queue);
	}
	
	public OrderParamsBuilder() {
		super();
	}
	
	@Override
	public OrderParamsBuilder withID(String id) {
		super.withID(id);
		return this;
	}
	
	@Override
	public OrderParamsBuilder withEventDispatcher(EventDispatcher dispatcher) {
		super.withEventDispatcher(dispatcher);
		return this;
	}
	
	@Override
	public OrderParamsBuilder withController(OSCController controller) {
		super.withController(controller);
		return this;
	}
	
	@Override
	public OrderParamsBuilder withLock(Lock lock) {
		super.withLock(lock);
		return this;
	}
	
	public OrderParamsBuilder withTerminal(Terminal terminal) {
		this.terminal = terminal;
		return this;
	}
	
	public OrderParamsBuilder withAccount(Account account) {
		this.account = account;
		return this;
	}
	
	public OrderParamsBuilder withSymbol(Symbol symbol) {
		this.symbol = symbol;
		return this;
	}
	
	public OrderParamsBuilder withOrderID(Long orderID) {
		this.orderID = orderID;
		return this;
	}
	
	public OrderParamsBuilder withSecurity(Security security) {
		this.security = security;
		return this;
	}
	
	public OrderParamsBuilder withPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
		return this;
	}
	
	public OrderParamsBuilder withPosition(Position position) {
		this.position = position;
		return this;
	}
	
	@Override
	public OrderParams buildParams() {
		return (OrderParams) super.buildParams();
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
	
	protected Long getOrderID() {
		if ( orderID == null ) {
			throw new IllegalStateException("Undefined order ID");
		}
		return orderID;
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
	
	protected Position getPosition() {
		if ( position == null ) {
			return getDefaultPosition();
		} else {
			return position;
		}
	}
	
	@Override
	protected String getDefaultID() {
		return String.format("%s.%s[%s].ORDER#%d",
			getTerminal().getTerminalID(), getAccount(), getSymbol(), getOrderID());
	}
	
	@Override
	protected OSCController getDefaultController() {
		return new OrderImpl.OrderController();
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
	
	protected Position getDefaultPosition() {
		return getPortfolio().getPosition(getSymbol());
	}
	
	@Override
	protected OSCParamsImpl createParams() {
		OrderParamsImpl params = new OrderParamsImpl();
		params.setTerminal(getTerminal());
		params.setAccount(getAccount());
		params.setSymbol(getSymbol());
		params.setOrderID(getOrderID());
		params.setSecurity(getSecurity());
		params.setPortfolio(getPortfolio());
		params.setPosition(getPosition());
		return params;
	}

}
