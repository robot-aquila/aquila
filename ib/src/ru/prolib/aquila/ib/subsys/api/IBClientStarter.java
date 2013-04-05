package ru.prolib.aquila.ib.subsys.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.ib.IBException;

/**
 * Стартер подключения к IB API.
 * <p>
 * 2012-11-25<br>
 * $Id: IBClientStarter.java 515 2013-02-11 05:52:28Z whirlwind $
 */
public class IBClientStarter implements Starter {
	private static final Logger logger;
	private final IBClient client;
	private final IBConfig config;
	private String lastErrorMessage = null;
	
	static {
		logger = LoggerFactory.getLogger(IBClientStarter.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param client объект подключения
	 * @param config параметры подлкючения
	 */
	public IBClientStarter(IBClient client, IBConfig config) {
		super();
		this.client = client;
		this.config = config;
	}
	
	/**
	 * Получить объект подключения.
	 * <p>
	 * @return подключение
	 */
	public IBClient getClient() {
		return client;
	}
	
	/**
	 * Получить параметры подключения.
	 * <p>
	 * @return параметры подключения
	 */
	public IBConfig getConfig() {
		return  config;
	}

	@Override
	public void start() {
		try {
			client.eConnect(config.getHost(),
					config.getPort(), config.getClientId());
			if ( config.getClientId() == 0 ) {
				client.reqAutoOpenOrders(true);
			}
		} catch ( IBException e ) {
			synchronized ( this ) {
				if ( ! e.getMessage().equals(lastErrorMessage) ) {
					lastErrorMessage = e.getMessage();
					logger.error("Connect failed: {}", lastErrorMessage);
				}
			}
		}
	}

	@Override
	public void stop() {
		client.eDisconnect();
	}

}
