package ru.prolib.aquila.core.BusinessEntities;

/**
 * Тип цены.
 * <p>
 * Используется для спецификации отступа и спреда тейк-профита.
 * <p>
 * 2012-10-24<br>
 * $Id: PriceUnit.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public enum PriceUnit {
	/**
	 * Единицы цены.
	 */
	MONEY ("PU"),
	/**
	 * Проценты цены.
	 */
	PERCENT ("%"),
	/**
	 * Пункты.
	 */
	POINT ("Pts.");
	
	private final String code;

	/**
	 * Создать тип.
	 * <p>
	 * @param code код типа
	 */
	private PriceUnit(String code) {
		//super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}

}
