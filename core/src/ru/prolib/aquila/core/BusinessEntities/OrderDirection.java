package ru.prolib.aquila.core.BusinessEntities;

/**
 * Направление заявки.
 * <p>
 * Класс определяет константы, использующиеся для спецификации направления
 * заявки или сделки: на покупку или продажу.
 * <p>
 * 2012-05-30<br>
 * $Id: OrderDirection.java 223 2012-07-04 12:26:58Z whirlwind $
 */
public class OrderDirection {
	/**
	 * Заявка на покупку.
	 */
	public static final OrderDirection BUY = new OrderDirection("Buy");
	
	/**
	 * Заявка на продажу.
	 */
	public static final OrderDirection SELL = new OrderDirection("Sell");
	
	private final String dir;
	
	/**
	 * Создать объект.
	 * <p>
	 * @param dir строковый код направления
	 */
	private OrderDirection(String dir) {
		super();
		this.dir = dir;
	}
	
	@Override
	public String toString() {
		return dir;
	}

}
