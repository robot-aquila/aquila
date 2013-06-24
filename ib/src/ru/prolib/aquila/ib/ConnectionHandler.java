package ru.prolib.aquila.ib;

import java.util.Timer;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ib.api.*;

/**
 * Обработчик соединения с IB API.
 * <p>
 * Выполняет своевременное подключение, отключение и восстановление соединения.
 */
public class ConnectionHandler implements Starter, EventListener {
	private final IBEditableTerminal terminal;
	private final Timer timer;
	private final IBConfig config;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param terminal экземпляр терминала
	 * @param config параметры подключения
	 * @param timer таймер
	 */
	ConnectionHandler(IBEditableTerminal terminal,IBConfig config,Timer timer) {
		super();
		this.terminal = terminal;
		this.timer = timer;
		this.config = config;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal экземпляр терминала
	 * @param config параметры подключения
	 */
	public ConnectionHandler(IBEditableTerminal terminal, IBConfig config) {
		this(terminal, config, new Timer(true));
	}
	
	public IBEditableTerminal getTerminal() {
		return terminal;
	}
	
	public IBConfig getConfig() {
		return config;
	}

	@Override
	public void start() {
		terminal.OnStarted().addListener(this);
		terminal.OnStopped().addListener(this);
		terminal.OnDisconnected().addListener(this);
	}

	@Override
	public void stop() {
		terminal.OnStarted().removeListener(this);
		terminal.OnStopped().removeListener(this);
		terminal.OnDisconnected().removeListener(this);
		terminal.getClient().disconnect();
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(terminal.OnStarted()) ) {
			timer.schedule(new DoReconnect(terminal, config), 0L, 5000L);
		} else if ( event.isType(terminal.OnDisconnected()) ) {
			if ( terminal.started() ) {
				timer.schedule(new DoReconnect(terminal, config), 0L, 5000L);
			}
		
		} else if ( event.isType(terminal.OnStopped()) ) {
			terminal.getClient().disconnect();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ConnectionHandler.class ) {
			return false;
		}
		ConnectionHandler o = (ConnectionHandler) other;
		return new EqualsBuilder()
			.append(o.config, config)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

}
