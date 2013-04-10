package ru.prolib.aquila.quik.subsys;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.quik.api.ConnEvent;
import ru.prolib.aquila.t2q.T2QConnStatus;

/**
 * Ретранслятор статуса подключения к QUIK API в события терминала.
 */
public class ConnectionStatusHandler implements EventListener {
	protected final EditableTerminal terminal;
	
	public ConnectionStatusHandler(EditableTerminal terminal) {
		super();
		this.terminal = terminal;
	}

	@Override
	public void onEvent(Event event) {
		ConnEvent e = (ConnEvent) event;
		if ( e.getStatus() == T2QConnStatus.DLL_CONN ) {
			terminal.fireTerminalConnectedEvent();
		} else if ( e.getStatus() == T2QConnStatus.DLL_DISC ) {
			terminal.fireTerminalDisconnectedEvent();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if (other != null && other.getClass()==ConnectionStatusHandler.class) {
			ConnectionStatusHandler o = (ConnectionStatusHandler) other;
			return new EqualsBuilder()
				.append(terminal, o.terminal)
				.isEquals();
		} else {
			return false;
		}
	}

}
