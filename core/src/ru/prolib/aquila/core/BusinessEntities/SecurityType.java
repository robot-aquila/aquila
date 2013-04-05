package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Тип инструмента.
 * <p>
 * 2012-12-18<br>
 * $Id: SecurityType.java 341 2012-12-18 17:16:30Z whirlwind $
 */
public class SecurityType {
	public static final SecurityType UNK  = new SecurityType("UNK", "Unknown");
	public static final SecurityType STK  = new SecurityType("STK", "Stock");
	public static final SecurityType OPT  = new SecurityType("OPT", "Option");
	public static final SecurityType FUT  = new SecurityType("FUT", "Futures");
	public static final SecurityType BOND = new SecurityType("BOND", "Bond");
	public static final SecurityType CASH = new SecurityType("CASH", "Cash");
	
	private final String code;
	private final String name;
	
	/**
	 * Создать тип инструмента.
	 * <p>
	 * @param code код типа
	 * @param name наименование типа
	 */
	private SecurityType(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return code;
	}
	
	/**
	 * Получить наименование типа.
	 * <p>
	 * @return наименование
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Получить код типа.
	 * <p>
	 * @return код типа
	 */
	public String getCode() {
		return code;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121219, 142011)
			.append(code)
			.append(name)
			.toHashCode();
	}

}
