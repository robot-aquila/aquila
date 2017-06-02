package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsImpl;

public class PortfolioParamsImpl extends OSCParamsImpl implements PortfolioParams {
	protected Terminal terminal;
	protected Account account;

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

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfolioParamsImpl.class ) {
			return false;
		}
		PortfolioParamsImpl o = (PortfolioParamsImpl) other;
		return new EqualsBuilder()
				.append(this.account, o.account)
				.append(this.controller, o.controller)
				.append(this.dispatcher, o.dispatcher)
				.append(this.id, o.id)
				.appendSuper(this.terminal == o.terminal)
				.isEquals();
	}

}
