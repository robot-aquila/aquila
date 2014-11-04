package ru.prolib.aquila.core.BusinessEntities;

import java.util.Currency;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.thoughtworks.xstream.annotations.*;

/**
 * Дескриптор инструмента.
 * <p>
 * Используется для однозначной идентификации инструмента биржевой торговли.
 * <p>
 * Класс позволяет наследование с целью расширения списка атрибутов инструмента
 * в служебных целях. Однако требуется гарантия принципа эквивалентности
 * по четырем значимым атрибутам: коду инструмента, коду класса, валюте и
 * типу инструмента. В связи с этим, методы {@link #equals(Object)} и
 * {@link #hashCode()} объявлены финальными, что делает невозможным 
 * переопределение и участие расширенных атрибутов при сравнении и получении
 * хэш-кода дескриптора. Реализация собственных процедур сравнения и
 * формирования хэш-кода приведет к тому, что пользователи модели, которые будут
 * использовать базовый дескриптор, фактически не смогут сослаться инструмент. 
 * <p>
 * 2012-06-01<br>
 * $Id: SecurityDescriptor.java 341 2012-12-18 17:16:30Z whirlwind $
 */
@XStreamAlias("SecurityDescriptor")
public class SecurityDescriptor {
	private final String code;
	private final String classCode;
	private final Currency currency;
	private final SecurityType type;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param currency валюта инструмента
	 * @param type тип инструмента
	 */
	public SecurityDescriptor(String code, String classCode, Currency currency,
			SecurityType type)
	{
		this.code = code;
		this.classCode = classCode;
		this.currency = currency;
		this.type = type;		
	}
	
	/**
	 * Создать дескриптор инструмента
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param curCode код валюты согласно ISO 4217
	 * @param type тип инструмента
	 */
	public SecurityDescriptor(String code, String classCode, String curCode,
			SecurityType type)
	{
		this(code, classCode, Currency.getInstance(curCode), type);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает дескриптор инструмента типа {@link SecurityType#STK}.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param curCode код валюты согласно ISO 4217
	 */
	public SecurityDescriptor(String code, String classCode, String curCode) {
		this(code, classCode, curCode, SecurityType.STK);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает дескриптор инструмента типа {@link SecurityType#STK}.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param currency валюта инструмента
	 */
	public SecurityDescriptor(String code, String classCode, Currency currency) {
		this(code, classCode, currency, SecurityType.STK);
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
	 * Получить валюту инструмента.
	 * <p>
	 * @return валюта
	 */
	public Currency getCurrency() {
		return currency;
	}
	
	/**
	 * Получить ISO 4217 код валюты.
	 * <p>
	 * @return код валюты инструмента
	 */
	public String getCurrencyCode() {
		return currency.getCurrencyCode();
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
			&& currency != null
			&& type != null;
	}
	
	@Override
	public final boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( !(other instanceof SecurityDescriptor) ) {
			return false;
		}
		SecurityDescriptor o = (SecurityDescriptor)other;
		return new EqualsBuilder()
			.append(code, o.code)
			.append(classCode, o.classCode)
			.append(currency, o.currency)
			.append(type, o.type)
			.isEquals();
	}
	
	@Override
	public final int hashCode() {
		return new HashCodeBuilder()
			.append(code)
			.append(classCode)
			.append(currency)
			.append(type)
			.hashCode();
	}
	
	@Override
	public String toString() {
		return code + "@" + classCode + "(" + type + "/" + currency + ")";
	}

}
