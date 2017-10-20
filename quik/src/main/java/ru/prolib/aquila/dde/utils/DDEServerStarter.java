package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDEServer;

/**
 * Стартер DDE-сервера.
 * <p>
 * 2013-01-30<br>
 * $Id: DDEServerStarter.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class DDEServerStarter implements Starter {
	private static final Logger logger;
	private final DDEServer server;
	
	static {
		logger = LoggerFactory.getLogger(DDEServerStarter.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param server DDE-сервер
	 */
	public DDEServerStarter(DDEServer server) {
		super();
		this.server = server;
	}
	
	/**
	 * Получить DDE-сервер.
	 * <p>
	 * @return DDE-сервер
	 */
	public DDEServer getServer() {
		return server;
	}

	@Override
	public void start() throws StarterException {
		logger.debug("starting DDE server...");
		try {
			server.start();
		} catch ( DDEException e ) {
			throw new StarterException(e);
		}
		logger.debug("DDE server started");
	}

	@Override
	public void stop() throws StarterException {
		logger.debug("stopping DDE server...");
		try {
			server.stop();
			logger.debug("Wait for DDE window thread...");
			server.join();
		} catch ( DDEException e ) {
			throw new StarterException(e);
		}
		logger.debug("DDE server stopped");
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == DDEServerStarter.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		DDEServerStarter o = (DDEServerStarter) other;
		return new EqualsBuilder()
			.append(server, o.server)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130131, 204231)
			.append(server)
			.toHashCode();
	}

}
