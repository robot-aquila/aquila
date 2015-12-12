package ru.prolib.aquila.core.BusinessEntities;

import java.io.Serializable;
import java.util.Currency;
import org.apache.commons.lang3.builder.*;

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
 */
public class Symbol implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String code;
	private final String classCode;
	private final Currency currency;
	private final SymbolType type;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param currency валюта инструмента
	 * @param type тип инструмента
	 */
	public Symbol(String code, String classCode, Currency currency,
			SymbolType type)
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
	public Symbol(String code, String classCode, String curCode,
			SymbolType type)
	{
		this(code, classCode, Currency.getInstance(curCode), type);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает дескриптор инструмента типа {@link SymbolType#STK}.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param curCode код валюты согласно ISO 4217
	 */
	public Symbol(String code, String classCode, String curCode) {
		this(code, classCode, curCode, SymbolType.STK);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает дескриптор инструмента типа {@link SymbolType#STK}.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param currency валюта инструмента
	 */
	public Symbol(String code, String classCode, Currency currency) {
		this(code, classCode, currency, SymbolType.STK);
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
	public SymbolType getType() {
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
		if ( !(other instanceof Symbol) ) {
			return false;
		}
		Symbol o = (Symbol)other;
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
