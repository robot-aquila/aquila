package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsImpl;

public class PositionParamsImpl extends OSCParamsImpl implements PositionParams {
	protected Terminal terminal;
	protected Account account;
	protected Symbol symbol;
	protected Security security;
	protected Portfolio portfolio;
	
	@Override
	public Terminal getTerminal() {
		if ( terminal == null ) {
			throw new IllegalStateException("Undefined terminal");
		}
		return terminal;
	}

	@Override
	public Account getAccount() {
		if ( account == null ) {
			throw new IllegalStateException("Undefined account");
		}
		return account;
	}

	@Override
	public Symbol getSymbol() {
		if ( symbol == null ) {
			throw new IllegalStateException("Undefined symbol");
		}
		return symbol;
	}
	
	@Override
	public Security getSecurity() {
		if ( security == null ) {
			throw new IllegalStateException("Undefined security");
		}
		return security;
	}
	
	@Override
	public Portfolio getPortfolio() {
		if ( portfolio == null ) {
			throw new IllegalStateException("Undefined portfolio");
		}
		return portfolio;
	}
	
	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public void setSecurity(Security security) {
		this.security = security;
	}
	
	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || other.getClass() != PositionParamsImpl.class ) {
			return false;
		}
		PositionParamsImpl o = (PositionParamsImpl) other;
		return new EqualsBuilder()
				.append(this.account, o.account)
				.append(this.controller, o.controller)
				.append(this.dispatcher, o.dispatcher)
				.append(this.id, o.id)
				.append(this.symbol, o.symbol)
				.append(this.lock, o.lock)
				.appendSuper(this.terminal == o.terminal)
				.append(this.security, o.security)
				.append(this.portfolio, o.portfolio)
				.isEquals();
	}

}
