package ru.prolib.aquila.core.BusinessEntities;

/**
 * Тип заявки.
 * <p>
 * 2012-05-30<br>
 * $Id: OrderType.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class OrderType {
	/**
	 * Лимитная.
	 */
	public static final OrderType LIMIT = new OrderType("Limit");
	/**
	 * Рыночная.
	 */
	public static final OrderType MARKET = new OrderType("Market");
	
	private final String code;
	
	/**
	 * Создать тип заявки.
	 * <p>
	 * @param code код типа
	 */
	private OrderType(String code) {
		super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}

}
