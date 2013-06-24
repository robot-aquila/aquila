package ru.prolib.aquila.ib.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Параметры подключения к API.
 * <p>
 * 2012-11-25<br>
 * $Id: IBConfig.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBConfig {
	private final String host;
	private final int port;
	private final int clientId;
	
	public IBConfig() {
		this(null, 4001, 0);
	}
	
	public IBConfig(int port) {
		this(null, port, 0);
	}
	
	public IBConfig(String host, int port) {
		this(host, port, 0);
	}
	
	public IBConfig(String host, int port, int clientId) {
		super();
		this.host = host;
		this.port = port;
		this.clientId = clientId;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getClientId() {
		return clientId;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != IBConfig.class ) {
			return false;
		}
		IBConfig o = (IBConfig) other;
		return new EqualsBuilder()
			.append(o.clientId, clientId)
			.append(o.port, port)
			.append(o.host, host)
			.isEquals();
	}

}
