package ru.prolib.aquila.ib;

import java.util.TimerTask;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.api.IBConfig;

/**
 * Процедура установления подключения.
 */
public class DoReconnect extends TimerTask {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DoReconnect.class);
	}
	
	private final IBEditableTerminal terminal;
	private final IBConfig config;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал 
	 * @param config конфигурация соединения
	 */
	public DoReconnect(IBEditableTerminal terminal, IBConfig config) {
		super();
		this.terminal = terminal;
		this.config = config;
	}
	
	/**
	 * Получить экземпляр терминала.
	 * <p>
	 * @return терминал
	 */
	public IBEditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить параметры подключения.
	 * <p>
	 * @return конфигурация
	 */
	public IBConfig getConfig() {
		return config;
	}

	@Override
	public void run() {
		synchronized ( terminal ) {
			IBClient client = terminal.getClient();
			if ( terminal.started() ) {
				client.connect(config);
				if ( client.connected() ) {
					cancel();
				}
			} else {
				cancel();
			}
		}
	}
	
	@Override
	public boolean cancel() {
		logger.debug("cancelled");
		return super.cancel();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DoReconnect.class ) {
			return false;
		}
		DoReconnect o = (DoReconnect) other;
		return new EqualsBuilder()
			.append(o.config, config)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

}
