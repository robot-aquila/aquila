package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Дескриптор субъекта доступа.
 * <p>
 * Данный класс используется для идентификации субъекта доступа на этапе
 * подключения. Характеризуется строковыми идентификаторами сервиса и темы.
 * <p>
 * 2012-07-27<br>
 * $Id: DDEAccessSubject.java 304 2012-11-06 09:17:07Z whirlwind $
 */
public class DDEAccessSubject {
	private final String service;
	private final String topic;

	/**
	 * Создать дескриптор.
	 * <p>
	 * @param service имя DDE-сервиса
	 * @param topic имя темы
	 */
	public DDEAccessSubject(String service, String topic) {
		super();
		if ( service == null ) {
			throw new NullPointerException("Service cannot be null");
		}
		if ( topic == null ) {
			throw new NullPointerException("Topic cannot be null");
		}
		this.service = service;
		this.topic = topic;
	}
	
	/**
	 * Получить имя DDE-сервиса.
	 * <p>
	 * @return имя сервиса
	 */
	public String getService() {
		return service;
	}
	
	/**
	 * Получить строку темы.
	 * <p>
	 * @return тема
	 */
	public String getTopic() {
		return topic;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDEAccessSubject ) {
			DDEAccessSubject o = (DDEAccessSubject)other;
			return new EqualsBuilder()
				.append(getService(), o.getService())
				.append(getTopic(), o.getTopic())
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, /*0*/85909)
			.append(service)
			.append(topic)
			.toHashCode();
	}

}
