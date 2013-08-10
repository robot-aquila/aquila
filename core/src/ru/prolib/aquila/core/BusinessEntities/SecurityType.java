package ru.prolib.aquila.core.BusinessEntities;

/**
 * Тип инструмента.
 * <p>
 * 2012-12-18<br>
 * $Id: SecurityType.java 341 2012-12-18 17:16:30Z whirlwind $
 */
public enum SecurityType {
	UNK ("UNK", "Unknown"),
	STK ("STK", "Stock"),
	OPT ("OPT", "Option"),
	FUT ("FUT", "Futures"),
	BOND ("BOND", "Bond"),
	CASH ("CASH", "Cash");
	
	private final String code;
	private final String name;
	
	/**
	 * Создать тип инструмента.
	 * <p>
	 * @param code код типа
	 * @param name наименование типа
	 */
	private SecurityType(String code, String name) {
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

}
