package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsImpl;

public class OrderParamsImpl extends OSCParamsImpl implements OrderParams {
	protected Terminal terminal;
	protected Account account;
	protected Symbol symbol;
	protected Long orderID;
	
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
	public long getOrderID() {
		if ( orderID == null ) {
			throw new IllegalStateException("Undefined order ID");
		}
		return orderID;
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
	
	public void setOrderID(Long orderID) {
		this.orderID = orderID;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || other.getClass() != OrderParamsImpl.class ) {
			return false;
		}
		OrderParamsImpl o = (OrderParamsImpl) other;
		return new EqualsBuilder()
				.append(this.account, o.account)
				.append(this.controller, o.controller)
				.append(this.dispatcher, o.dispatcher)
				.append(this.id, o.id)
				.append(this.orderID, o.orderID)
				.append(this.symbol, o.symbol)
				.append(this.lock, o.lock)
				.appendSuper(this.terminal == o.terminal)
				.isEquals();
	}

}
