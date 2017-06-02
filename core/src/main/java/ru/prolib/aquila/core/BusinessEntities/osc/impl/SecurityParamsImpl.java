package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsImpl;

public class SecurityParamsImpl extends OSCParamsImpl implements SecurityParams {
	protected Terminal terminal;
	protected Symbol symbol;

	@Override
	public Terminal getTerminal() {
		if ( terminal == null ) {
			throw new IllegalStateException("Undefined terminal");
		}
		return terminal;
	}

	@Override
	public Symbol getSymbol() {
		if ( symbol == null ) {
			throw new IllegalStateException("Undefined symbol");
		}
		return symbol;
	}
	
	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || other.getClass() != SecurityParamsImpl.class ) {
			return false;
		}
		SecurityParamsImpl o = (SecurityParamsImpl) other;
		return new EqualsBuilder()
				.append(o.controller, controller)
				.append(o.dispatcher, dispatcher)
				.append(o.id, id)
				.append(o.symbol, symbol)
				.appendSuper(o.terminal == terminal)
				.isEquals();
	}
}
