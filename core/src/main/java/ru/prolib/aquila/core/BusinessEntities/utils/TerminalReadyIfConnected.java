package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;

/**
 * Стандартная стратегия определения готовности терминала. 
 * <p>
 * В большинстве случаев доступность терминала определяется наличием соединения
 * с удаленной системой. В соответствии с данной стратегией, терминал считается
 * готовым к работе сразу после установления соединения и отмечается недоступным
 * для выполнения запросов сразу после разрыва соединения. Для использования
 * данной стратегии достаточно добавить экземпляр данного класса в набор задач
 * стартера терминала.
 */
public class TerminalReadyIfConnected implements Starter, EventListener {
	private final EditableTerminal<?> terminal; 
	
	public TerminalReadyIfConnected(EditableTerminal<?> terminal) {
		super();
		this.terminal = terminal;
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(terminal.OnConnected()) ) {
			terminal.fireTerminalReady();
		} else if ( event.isType(terminal.OnDisconnected()) ) {
			terminal.fireTerminalUnready();
		}
	}

	@Override
	public void start() {
		terminal.OnConnected().addSyncListener(this);
		terminal.OnDisconnected().addSyncListener(this);
	}

	@Override
	public void stop() {
		terminal.OnConnected().removeListener(this);
		terminal.OnDisconnected().removeListener(this);
	}

}
