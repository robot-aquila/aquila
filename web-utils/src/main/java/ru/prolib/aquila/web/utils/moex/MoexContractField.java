package ru.prolib.aquila.web.utils.moex;

/**
 * This enumeration describes all possible fields of the contract details page
 * http://moex.com/en/contract.aspx?code=S where S is a contract symbol.
 */
public class MoexContractField {
	/**
	 * Contract Symbol<br>
	 * Value: 1<br>
	 * Type: String<br>
	 * Example: RTS-6.16<br>
	 */
	public static final int SYMBOL = 1;
	
	/**
	 * Contract Trading Symbol<br>
	 * Value: 2<br>
	 * Type: String<br>
	 * Example: RIM6<br>
	 */
	public static final int SYMBOL_CODE = 2;
	
	/**
	 * Contract Description<br>
	 * Value: 3<br>
	 * Type: String<br>
	 * Example: RTS Index Futures<br>
	 */
	public static final int CONTRACT_DESCR = 3;
	
	/**
	 * Type<br>
	 * Value: 4<br>
	 * Type: {@link MoexContractType}<br>
	 */
	public static final int TYPE = 4;
	
	/**
	 * Settlement<br>
	 * Value: 5<br>
	 * Type: {@link MoexSettlementType}<br>
	 */
	public static final int SETTLEMENT = 5;
	
	/**
	 * Contract size<br>
	 * Value: 6<br>
	 * Type: int<br>
	 */
	public static final int LOT_SIZE = 6;
	
	/**
	 * Quotation<br>
	 * Value: 7<br>
	 * Type: {@link MoexQuotationType}<br>
	 */
	public static final int QUOTATION = 7;
	
	/**
	 * First Trading Day<br>
	 * Value: 8<br>
	 * Type: {@link java.time.LocalDate}<br>
	 */
	public static final int FIRST_TRADING_DAY = 8;
	
	/**
	 * Last Trading Day<br>
	 * Value: 9<br>
	 * Type: {@link java.time.LocalDate}<br>
	 */
	public static final int LAST_TRADING_DAY = 9;
	
	/**
	 * Delivery<br>
	 * Value: 10<br>
	 * Type: {@link java.time.LocalDate}<br>
	 */
	public static final int DELIVERY = 10;
	
	/**
	 * Price tick.<br>
	 * Value: 11<br>
	 * Type: Double<br>
	 */
	public static final int TICK_SIZE = 11;
	
	/**
	 * Value of price tick, RUB<br>
	 * Value: 12<br>
	 * Type: Double<br>
	 */
	public static final int TICK_VALUE = 12;
	
	/**
	 * Lower limit<br>
	 * Value: 13<br>
	 * Type: Double<br>
	 */
	public static final int LOWER_PRICE_LIMIT = 13;
	
	/**
	 * Upper limit<br>
	 * Value: 14<br>
	 * Type: Double<br>
	 */
	public static final int UPPER_PRICE_LIMIT = 14;
	
	/**
	 * Settlement price of last clearing session<br>
	 * Value: 15<br>
	 * Type: Double<br>
	 */
	public static final int SETTLEMENT_PRICE = 15;
	
	/**
	 * Contract buy/sell fee, RUB<br>
	 * Value: 16<br>
	 * Type: Double<br>
	 */
	public static final int FEE = 16;
	
	/**
	 * Intraday (scalper) fee, RUB<br>
	 * Value: 17<br>
	 * Type: Double<br>
	 */
	public static final int INTRADAY_FEE = 17;
	
	/**
	 * Negotiated trade fee, RUB<br>
	 * Value: 18<br>
	 * Type: Double<br>
	 */
	public static final int NEGOTIATION_FEE = 18;

	/**
	 * Contract exercise fee.<br>
	 * Value: 19<br>
	 * Type: Double<br>
	 */
	public static final int EXERCISE_FEE = 19;
	
	/**
	 * Initial Margin (IM, rub)<br>
	 * Value: 20<br>
	 * Type: Double<br>
	 */
	public static final int INITIAL_MARGIN = 20;
	
	/**
	 * IM value on<br>
	 * Value: 21<br>
	 * Type: {@link java.time.LocalDate}<br>
	 */
	public static final int INITIAL_MARGIN_DATE = 21;
	
	/**
	 * FX for intraday clearing<br>
	 * Value: 22<br>
	 * Type: {@link java.time.LocalTime}<br>
	 */
	public static final int FX_INTRADAY_CLEARING = 22;
	
	/**
	 * FX for evening clearing<br>
	 * Value: 23<br>
	 * Type: {@link java.time.LocalTime}<br>
	 */
	public static final int FX_EVENING_CLEARING = 23;
	
	/**
	 * Settlement procedure<br>
	 * Value: 24<br>
	 * Type: String<br>
	 * Example: Cash settlement. An average value of RTS Index calculated during
	 * the period from 15:00 to 16:00 Moscow time of the last trading day
	 * multiplied by 100 is taken as a settlement price. The tick value equals
	 * 20% of the USD/RUB exchange rate determined in accordance with the
	 * Methodology at 6:30 pm MSK on the last trading day.
	 */
	public static final int SETTLEMENT_PROC_DESCR = 24;
	
}
