package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDEServer;
import ru.prolib.aquila.dde.DDEService;

/**
 * Стартер DDE-сервиса.
 * <p>
 * 2012-08-18<br>
 * $Id: DDEServiceStarter.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class DDEServiceStarter implements Starter {
	private static final Logger logger;
	private final DDEServer server;
	private final DDEService service;
	
	static {
		logger = LoggerFactory.getLogger(DDEServiceStarter.class);
	}
	
	/**
	 * Создать стартер сервиса.
	 * <p>
	 * @param server DDE-сервер
	 * @param service обработчик сервиса
	 */
	public DDEServiceStarter(DDEServer server, DDEService service) {
		super();
		if ( server == null ) {
			throw new NullPointerException("Server cannot be null");
		}
		this.server = server;
		if ( service == null ) {
			throw new NullPointerException("Service cannot be null");
		}
		this.service = service;
	}
	
	/**
	 * Получить экземпляр сервера.
	 * <p>
	 * @return сервер
	 */
	public DDEServer getServer() {
		return server;
	}
	
	/**
	 * Получить экземпляр сервиса.
	 * <p>
	 * @return сервис
	 */
	public DDEService getService() {
		return service;
	}

	@Override
	public void start() throws StarterException {
		logger.debug("starting DDE service {}", service.getName());
		try {
			server.registerService(service);
		} catch ( DDEException e ) {
			throw new StarterException(e);
		}
		logger.debug("DDE service {} started", service.getName());
	}

	@Override
	public void stop() throws StarterException {
		logger.debug("stopping DDE service {}", service.getName());
		try {
			server.unregisterService(service.getName());
		} catch ( DDEException e ) {
			throw new StarterException(e);
		}
		logger.debug("DDE service {} stopped", service.getName());
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, /*0*/62813)
			.append(server)
			.append(service)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDEServiceStarter ) {
			DDEServiceStarter o = (DDEServiceStarter) other;
			return new EqualsBuilder()
				.append(server, o.server)
				.append(service, o.service)
				.isEquals();
		} else {
			return false;
		}
	}

}
