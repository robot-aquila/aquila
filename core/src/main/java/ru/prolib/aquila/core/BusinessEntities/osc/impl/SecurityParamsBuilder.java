package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.SecurityImpl;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsImpl;

public class SecurityParamsBuilder extends OSCParamsBuilder {
	protected Terminal terminal;
	protected Symbol symbol;
	
	public SecurityParamsBuilder(EventQueue queue) {
		super(queue);
	}
	
	public SecurityParamsBuilder() {
		super();
	}
	
	@Override
	public SecurityParamsBuilder withID(String id) {
		super.withID(id);
		return this;
	}
	
	@Override
	public SecurityParamsBuilder withEventDispatcher(EventDispatcher dispatcher) {
		super.withEventDispatcher(dispatcher);
		return this;
	}
	
	@Override
	public SecurityParamsBuilder withController(OSCController controller) {
		super.withController(controller);
		return this;
	}
	
	public SecurityParamsBuilder withTerminal(Terminal terminal) {
		this.terminal = terminal;
		return this;
	}
	
	public SecurityParamsBuilder withSymbol(Symbol symbol) {
		this.symbol = symbol;
		return this;
	}

	@Override
	public SecurityParams buildParams() {
		return (SecurityParams) super.buildParams();
	}

	protected Terminal getTerminal() {
		if ( terminal == null ) {
			throw new IllegalStateException("Undefined terminal");
		}
		return terminal;
	}
	
	protected Symbol getSymbol() {
		if ( symbol == null ) {
			throw new IllegalStateException("Undefined symbol");
		}
		return symbol;
	}
	
	@Override
	protected String getDefaultID() {
		return String.format("%s.%s.SECURITY", getTerminal().getTerminalID(), getSymbol());
	}
	
	@Override
	protected OSCController getDefaultController() {
		return new SecurityImpl.SecurityController();
	}
	
	@Override
	protected OSCParamsImpl createParams() {
		SecurityParamsImpl params = new SecurityParamsImpl();
		params.setTerminal(getTerminal());
		params.setSymbol(getSymbol());
		return params;
	}

}
