package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class TerminalEvent extends EventImpl {
	private final Terminal terminal;

	public TerminalEvent(EventType type, Terminal terminal) {
		super(type);
		this.terminal = terminal;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}

}
