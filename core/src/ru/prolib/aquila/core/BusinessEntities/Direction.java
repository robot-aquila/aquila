package ru.prolib.aquila.core.BusinessEntities;

/**
 * Направление операции.
 * <p>
 * Класс определяет константы, использующиеся для спецификации направления
 * заявки или сделки: на покупку или продажу.
 */
public class Direction {
	/**
	 * Заявка на покупку.
	 */
	public static final Direction BUY = new Direction("Buy");
	
	/**
	 * Заявка на продажу.
	 */
	public static final Direction SELL = new Direction("Sell");
	
	private final String dir;
	
	/**
	 * Создать объект.
	 * <p>
	 * @param dir строковый код направления
	 */
	private Direction(String dir) {
		super();
		this.dir = dir;
	}
	
	@Override
	public String toString() {
		return dir;
	}

}
