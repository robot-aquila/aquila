package ru.prolib.aquila.core.BusinessEntities;

public class SecurityField {
	
	public static int getVersion() {
		return 5;
	}
	
	/**
	 * Security display name.<br>
	 * Type: {@link java.lang.String}
	 */
	public static final int DISPLAY_NAME = 1;
	
	/**
	 * Lot size.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int LOT_SIZE = 3;
	
	/**
	 * Minimal price change.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int TICK_SIZE = 4;
	
	/**
	 * One tick price in account currency (usually) or security currency.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int TICK_VALUE = 5;
	
	/**
	 * Initial margin per one contract.
	 * Type: {@link CDecimal}
	 */
	public static final int INITIAL_MARGIN = 6;
	
	/**
	 * Initial price.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int SETTLEMENT_PRICE = 7;
	
	/**
	 * Lower price limit.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int LOWER_PRICE_LIMIT = 8;
	
	/**
	 * Upper price limit.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int UPPER_PRICE_LIMIT = 9;
	
	/**
	 * Session open price.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int OPEN_PRICE = 10;
	
	/**
	 * Session highest price.<br>
	 * Type: {@link CDecimal}
	 */	
	public static final int HIGH_PRICE = 11;
	
	/**
	 * Session lowest price.<br>
	 * Type: {@link CDecimal}
	 */	
	public static final int LOW_PRICE = 12;
	
	/**
	 * Session close price.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int CLOSE_PRICE = 13;
	
	/**
	 * Security expiration time.<br>
	 * Type: nullable {@link java.time.Instant}
	 */
	public static final int EXPIRATION_TIME = 14;

}
