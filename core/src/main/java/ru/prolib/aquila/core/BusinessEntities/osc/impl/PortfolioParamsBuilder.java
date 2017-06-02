package ru.prolib.aquila.core.BusinessEntities.osc.impl;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.PortfolioImpl;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsImpl;

public class PortfolioParamsBuilder extends OSCParamsBuilder {
	protected Terminal terminal;
	protected Account account;
	
	public PortfolioParamsBuilder(EventQueue queue) {
		super(queue);
	}
	
	public PortfolioParamsBuilder() {
		super();
	}

	@Override
	public PortfolioParamsBuilder withID(String id) {
		super.withID(id);
		return this;
	}
	
	@Override
	public PortfolioParamsBuilder withEventDispatcher(EventDispatcher dispatcher) {
		super.withEventDispatcher(dispatcher);
		return this;
	}
	
	@Override
	public PortfolioParamsBuilder withController(OSCController controller) {
		super.withController(controller);
		return this;
	}
	
	public PortfolioParamsBuilder withTerminal(Terminal terminal) {
		this.terminal = terminal;
		return this;
	}
	
	public PortfolioParamsBuilder withAccount(Account account) {
		this.account = account;
		return this;
	}
	
	@Override
	public PortfolioParams buildParams() {
		return (PortfolioParams) super.buildParams();
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

	@Override
	protected String getDefaultID() {
		return String.format("%s.%s.PORTFOLIO", getTerminal().getTerminalID(), getAccount());
	}
	
	@Override
	protected OSCController getDefaultController() {
		return new PortfolioImpl.PortfolioController();
	}
	
	@Override
	protected OSCParamsImpl createParams() {
		PortfolioParamsImpl params = new PortfolioParamsImpl();
		params.setTerminal(getTerminal());
		params.setAccount(getAccount());
		return params;
	}
	
}
