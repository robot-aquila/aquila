package ru.prolib.aquila.core.BusinessEntities;

public class PortfolioField {
	
	public static int getVersion() {
		return 2;
	}

	/**
	 * Account balance in the deposit currency.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int BALANCE = 1;
	
	/**
	 * Account equity in the deposit currency.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int EQUITY = 2;
	
	/**
	 * Current profit/loss of an account in the deposit currency.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int PROFIT_AND_LOSS = 3;
	
	/**
	 * Account margin used in the deposit currency.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int USED_MARGIN = 4;
	
	/**
	 * Free margin of an account in the deposit currency.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int FREE_MARGIN = 5;
	
	/**
	 * Margin call level.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int MARGIN_CALL_AT = 6;
	
	/**
	 * Margin stop out level.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int MARGIN_STOP_OUT_AT = 7;
	
	/**
	 * The current assets of an account.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int ASSETS = 8;
	
	/**
	 * The current liabilities on an account.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int LIABILITIES = 9;
	
	/**
	 * ISO 4217 code of account currency.<br>
	 * Type: {@link java.lang.String}
	 */
	public static final int CURRENCY = 10;
	
	/**
	 * Account leverage.<br>
	 * Type: {@link CDecimal}
	 */
	public static final int LEVERAGE = 11;

}
