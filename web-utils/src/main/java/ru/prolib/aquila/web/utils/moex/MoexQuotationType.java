package ru.prolib.aquila.web.utils.moex;

@Deprecated
public enum MoexQuotationType {
	/**
	 * null (if not exists)
	 */
	UNDEFINED,
	
	/**
	 * "в евро за 1 акцию_EUR per 1"
	 */
	EUR_PER_1,
	
	/**
	 * "%"
	 */
	PERCENT,
	
	/**
	 * "USD" and "US dollar"
	 */
	USD,
	
	/**
	 * "USD per 1 troy ounce"
	 */
	USD_PER_1_TROY_OUNCE,
	
	/**
	 * "US dollar per 1 euro"
	 */
	USD_PER_1_EUR,
	
	/**
	 * "USD per 1 barrel"
	 */
	USD_PER_1_BARREL,
	
	/**
	 * "RUR"
	 */
	RUR,
	
	/**
	 * "RUR per 1 kilo"
	 */
	RUR_PER_1_KILO,
	
	/**
	 * "points", "п." and "points (% from face value)"
	 */
	POINTS,
	
	/**
	 * "в укр. гривнах за 1 долл. США_UAH per 1 USD"
	 */
	UAH_PER_1_USD,
	
	/**
	 * "в канадских долларах за 1 долл. США_CAD per 1 USD"
	 */
	CAD_PER_1_USD,
	
	/**
	 * "в швейц. франках за 1 долл. США_CHF per 1 USD"
	 */
	CHF_PER_1_USD,
	
	/**
	 * "в российских рублях за 1 юань_RUB per 1 CNY"
	 */
	RUR_PER_1_CNY,
	
	/**
	 * "в японск. йенах за 1 долл. США_JPY per 1 USD"
	 */
	JPY_PER_1_USD,
	
	/**
	 * "RUR per 1 metric ton exl. VAT"
	 */
	RUR_PER_1_METRIC_TONN_EXL_VAT,
	
	/**
	 * "в турецких лирах за 1 долл. США_TRY per 1 USD"
	 */
	TRY_PER_1_USD,
	
	/**
	 * RUB per 1 kilo
	 */	
	RUB_PER_1_KILO,
	
	/**
	 *  RUB per lot
	 */
	RUB_PER_LOT
}
