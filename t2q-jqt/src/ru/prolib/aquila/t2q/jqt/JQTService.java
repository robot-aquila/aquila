package ru.prolib.aquila.t2q.jqt;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.JQTrans.JQTransServer;
import ru.prolib.aquila.t2q.T2QException;
import ru.prolib.aquila.t2q.T2QService;

/**
 * Сервис обработки транзакций.
 * <p>
 * 2013-01-31<br>
 * $Id: JQTService.java 493 2013-02-06 05:37:55Z whirlwind $
 */
public class JQTService implements T2QService {
	private static final Logger logger;
	private final JQTHandler handler;
	private final JQTransServer server;
	
	static {
		logger = LoggerFactory.getLogger(JQTService.class);
	}

	/**
	 * Конструктор.
	 * <p>
	 * @param handler обработчик
	 * @param server сервер
	 */
	public JQTService(JQTHandler handler, JQTransServer server) {
		super();
		this.handler = handler;
		this.server = server;
	}
	
	/**
	 * Получить обработчик.
	 * <p>
	 * @return обработчик
	 */
	public JQTHandler getHandler() {
		return handler;
	}
	
	/**
	 * Получить сервер.
	 * <p>
	 * @return сервер
	 */
	public JQTransServer getServer() {
		return server;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == JQTService.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		JQTService o = (JQTService) other;
		return new EqualsBuilder()
			.append(handler, o.handler)
			.append(server, o.server)
			.isEquals();
	}

	@Override
	public void connect(String param) throws T2QException {
		try {
			server.connect(param);
		} catch ( Exception e ) {
			throw new T2QException(e);
		}
	}

	@Override
	public void disconnect() {
		try {
			server.disconnect();
		} catch ( Exception e ) {
			logger.warn("Error disconnect (ignored): ", e);
		}
	}

	@Override
	public void send(String spec) throws T2QException {
		try {
			logger.debug("send: {}", spec);
			server.send(spec);
		} catch ( Exception e ) {
			throw new T2QException(e);
		}
	}

}
