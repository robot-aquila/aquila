package ru.prolib.aquila.core.BusinessEntities;

public class SecurityField {
	
	public static int getVersion() {
		return 2;
	}
	
	public static final int DISPLAY_NAME = 1;
	public static final int LOT_SIZE = 3;
	
	/**
	 * Minimal price change.<br>
	 * Type: {@link FDecimal}
	 */
	public static final int TICK_SIZE = 4;
	
	/**
	 * One tick price in account currency (usually) or security currency.
	 * Type: {@link FMoney}
	 */
	public static final int TICK_VALUE = 5;
	
	public static final int INITIAL_MARGIN = 6;
	public static final int SETTLEMENT_PRICE = 7;
	public static final int LOWER_PRICE_LIMIT = 8;
	public static final int UPPER_PRICE_LIMIT = 9;
	public static final int OPEN_PRICE = 10;
	public static final int HIGH_PRICE = 11;
	public static final int LOW_PRICE = 12;
	public static final int CLOSE_PRICE = 13;
}
