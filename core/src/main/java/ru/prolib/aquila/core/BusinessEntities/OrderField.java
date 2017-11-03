package ru.prolib.aquila.core.BusinessEntities;

public class OrderField {
	
	public static int getVersion() {
		return 2;
	}
	
	/**
	 * Order type.<br>
	 * Type: {@link OrderType}.
	 */
	public static final int TYPE = 1;
	
	/**
	 * Order action.<br>
	 * Type: {@link OrderAction}.
	 */
	public static final int ACTION = 2;
	
	/**
	 * Order placement time.<br>
	 * Type: {@link java.time.Instant}.
	 */
	public static final int TIME = 3;
	
	/**
	 * Order status.<br>
	 * Type: {@link OrderStatus}.
	 */
	public static final int STATUS = 4;
	
	/**
	 * Order price.<br>
	 * Type: nullable {@link CDecimal}.
	 */
	public static final int PRICE = 5;
	
	/**
	 * Order initial volume.<br>
	 * Type: {@link CDecimal}.
	 */
	public static final int INITIAL_VOLUME = 6;
	
	/**
	 * Order current volume.<br>
	 * Type: {@link CDecimal}.
	 */
	public static final int CURRENT_VOLUME = 7;
	
	/**
	 * Order execution time.<br>
	 * Type: nullable {@link java.time.Instant}.
	 */
	public static final int TIME_DONE = 8;
	
	/**
	 * Executed value of order.<br>
	 * Type: {@link CDecimal}.
	 */
	public static final int EXECUTED_VALUE = 9;
	
	/**
	 * External ID of order.<br>
	 * Type: {@link java.lang.String}.
	 */
	public static final int EXTERNAL_ID = 10;
	
	/**
	 * Order comment.<br>
	 * Type: {@link java.lang.String}.
	 */
	public static final int COMMENT = 11;
	
	/**
	 * Order system comment.<br>
	 * Type: {@link java.lang.String}.
	 */
	public static final int SYSTEM_MESSAGE = 12;
	
	/**
	 * User defined long.<br>
	 * Type: {@link java.lang.Long}.
	 */
	public static final int USER_DEFINED_LONG = 13;
	
	/**
	 * User defined string.<br>
	 * Type: {@link java.lang.String}.
	 */
	public static final int USER_DEFINED_STRING = 14;
	
}
