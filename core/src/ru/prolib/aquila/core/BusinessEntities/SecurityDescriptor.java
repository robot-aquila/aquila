package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.thoughtworks.xstream.annotations.*;

/**
 * Дескриптор инструмента.
 * <p>
 * Используется для однозначной идентификации инструмента биржевой торговли.
 * <p>
 * 2012-06-01<br>
 * $Id: SecurityDescriptor.java 341 2012-12-18 17:16:30Z whirlwind $
 */
@XStreamAlias("SecurityDescriptor")
public class SecurityDescriptor {
	private final String code;
	private final String classCode;
	@XStreamAlias("currencyCode")
	private final String curCode;
	private final SecurityType type;
	
	/**
	 * Создать дескриптор инструмента
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param curCode код валюты
	 * @param type тип инструмента
	 */
	public SecurityDescriptor(String code, String classCode, String curCode,
			SecurityType type)
	{
		super();
		this.code = code;
		this.classCode = classCode;
		this.curCode = curCode;
		this.type = type;
	}
	
	/**
	 * Получить код инструмента
	 * <p>
	 * @return код инструмента
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Получить код класса
	 * <p>
	 * @return код класса
	 */
	public String getClassCode() {
		return classCode;
	}
	
	/**
	 * Получить код валюты.
	 * <p>
	 * @return код валюты
	 */
	public String getCurrency() {
		return curCode;
	}
	
	/**
	 * Получить тип инструмента.
	 * <p>
	 * @return тип инструмента
	 */
	public SecurityType getType() {
		return type;
	}
	
	/**
	 * Проверить валидность дескриптора.
	 * <p>
	 * @return true если валидный дескриптор, иначе false
	 */
	public boolean isValid() {
		return code != null && code.length() > 0
			&& classCode != null && classCode.length() > 0
			&& curCode != null && curCode.length() > 0
			&& type != null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( other.getClass() != SecurityDescriptor.class ) {
			return false;
		}
		SecurityDescriptor o = (SecurityDescriptor)other;
		return new EqualsBuilder()
			.append(code, o.code)
			.append(classCode, o.classCode)
			.append(curCode, o.curCode)
			.append(type, o.type)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(code)
			.append(classCode)
			.append(curCode)
			.append(type)
			.hashCode();
	}
	
	@Override
	public String toString() {
		return code + "@" + classCode + "(" + type + "/" + curCode + ")";
	}

}
