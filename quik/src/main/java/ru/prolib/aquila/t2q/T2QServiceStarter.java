package ru.prolib.aquila.t2q;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;

/**
 * Стартер сервиса транзакций.
 * <p>
 * 2013-01-30<br>
 * $Id: T2QServiceStarter.java 493 2013-02-06 05:37:55Z whirlwind $
 */
public class T2QServiceStarter implements Starter {
	private final T2QService service;
	private final String connParam;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param service экземпляр сервиса
	 * @param connParam параметры подключения
	 */
	public T2QServiceStarter(T2QService service, String connParam) {
		super();
		if ( service == null ) {
			throw new NullPointerException("Service cannot be null");
		}
		if ( connParam == null ) {
			throw new NullPointerException("Connection string cannot be null");
		}
		this.service = service;
		this.connParam = connParam;
	}
	
	/**
	 * Получить экземпляр сервиса.
	 * <p>
	 * @return сервис
	 */
	public T2QService getService() {
		return service;
	}
	
	/**
	 * Получить строку параметров подключения.
	 * <p>
	 * @return параметры подключения
	 */
	public String getConnectionParam() {
		return connParam;
	}

	@Override
	public void start() throws StarterException {
		try {
			service.connect(connParam);
		} catch ( T2QException e ) {
			throw new StarterException(e);
		}
	}

	@Override
	public void stop() throws StarterException {
		service.disconnect();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == T2QServiceStarter.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		T2QServiceStarter o = (T2QServiceStarter) other;
		return new EqualsBuilder()
			.append(service, o.service)
			.append(connParam, o.connParam)
			.isEquals();
	}

}
