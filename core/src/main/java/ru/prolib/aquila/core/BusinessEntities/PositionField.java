package ru.prolib.aquila.core.BusinessEntities;

public class PositionField {
	
	public static int getVersion() {
		return 3;
	}
	
	/**
	 * Position current volume.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int CURRENT_VOLUME = 1;
	
	/**
	 * Position current price.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int CURRENT_PRICE = 2;
	
	/**
	 * Position open price.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int OPEN_PRICE = 3;
	
	/**
	 * Position used margin.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int USED_MARGIN = 4;
	
	/**
	 * Position profit and loss.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int PROFIT_AND_LOSS = 5;
	
}
