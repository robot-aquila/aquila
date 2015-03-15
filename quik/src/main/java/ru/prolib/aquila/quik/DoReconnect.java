package ru.prolib.aquila.quik;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.t2q.T2QException;

/**
 * Процедура установления подключения.
 */
public class DoReconnect implements Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DoReconnect.class);
	}
	
	private final QUIKTerminal terminal;
	private final QUIKConfig config;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал 
	 * @param config конфигурация соединения
	 */
	public DoReconnect(QUIKTerminal terminal, QUIKConfig config) {
		super();
		this.terminal = terminal;
		this.config = config;
	}
	
	/**
	 * Получить экземпляр терминала.
	 * <p>
	 * @return терминал
	 */
	public QUIKTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить параметры подключения.
	 * <p>
	 * @return конфигурация
	 */
	public QUIKConfig getConfig() {
		return config;
	}

	@Override
	public void run() {
		synchronized ( terminal ) {
			QUIKClient client = terminal.getClient();
			if ( terminal.started() && ! terminal.connected() ) {
				try {
					client.connect(config.getQUIKPath());
					terminal.cancel(this);
				} catch ( T2QException e ) {
					logger.error(e.getMessage());
				}
			} else {
				terminal.cancel(this);
			}
		}
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
