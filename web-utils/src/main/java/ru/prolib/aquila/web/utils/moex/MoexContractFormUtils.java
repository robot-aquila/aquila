package ru.prolib.aquila.web.utils.moex;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ru.prolib.aquila.web.utils.WUWebPageException;

public class MoexContractFormUtils {
	private static final Map<String, MoexContractType> stringToContractType;
	private static final Map<String, MoexQuotationType> stringToQuotationType;
	private static final Map<String, MoexSettlementType> stringToSettlementType;
	private static final Map<String, Integer> stringToContractField;
	private static final DateTimeFormatter dateFormat;
	private static final DateTimeFormatter clearingTimeFormat;
	
	static {
		stringToContractType = new HashMap<>();
		map(MoexContractType.FUTURES, "Futures");
		
		stringToQuotationType = new HashMap<>();
		map(MoexQuotationType.CAD_PER_1_USD, "в канадских долларах за 1 долл. США_CAD per 1 USD");
		map(MoexQuotationType.CHF_PER_1_USD, "в швейц. франках за 1 долл. США_CHF per 1 USD");
		map(MoexQuotationType.EUR_PER_1, "в евро за 1 акцию_EUR per 1");
		map(MoexQuotationType.JPY_PER_1_USD, "в японск. йенах за 1 долл. США_JPY per 1 USD");
		map(MoexQuotationType.PERCENT, "%");
		map(MoexQuotationType.POINTS, "points");
		map(MoexQuotationType.POINTS, "п.");
		map(MoexQuotationType.POINTS, "points (% from face value)");
		map(MoexQuotationType.POINTS, "points per 1 metric ton");
		map(MoexQuotationType.RUR, "RUR");
		map(MoexQuotationType.RUR_PER_1_CNY, "в российских рублях за 1 юань_RUB per 1 CNY");
		map(MoexQuotationType.RUR_PER_1_KILO, "RUR per 1 kilo");
		map(MoexQuotationType.RUR_PER_1_METRIC_TONN_EXL_VAT, "RUR per 1 metric ton exl. VAT");
		map(MoexQuotationType.TRY_PER_1_USD, "в турецких лирах за 1 долл. США_TRY per 1 USD");
		map(MoexQuotationType.UAH_PER_1_USD, "в укр. гривнах за 1 долл. США_UAH per 1 USD");
		map(MoexQuotationType.UNDEFINED, null); // if not exists
		map(MoexQuotationType.USD, "USD");
		map(MoexQuotationType.USD, "US dollar");
		map(MoexQuotationType.USD_PER_1_BARREL, "USD per 1 barrel");
		map(MoexQuotationType.USD_PER_1_EUR, "US dollar per 1 euro");
		map(MoexQuotationType.USD_PER_1_TROY_OUNCE, "USD per 1 troy ounce");
		
		stringToSettlementType = new HashMap<>();
		map(MoexSettlementType.CASH_SETTLED, "Cash-Settled");
		map(MoexSettlementType.DELIVERABLE, "Deliverable");
		
		stringToContractField = new HashMap<>();
		mapContract(MoexContractField.SYMBOL, "Contract Symbol");
		mapContract(MoexContractField.SYMBOL_CODE, "Contract Trading Symbol");
		mapContract(MoexContractField.CONTRACT_DESCR, "Contract Description");
		mapContract(MoexContractField.TYPE, "Type");
		mapContract(MoexContractField.SETTLEMENT, "Settlement");
		mapContract(MoexContractField.LOT_SIZE, "Сontract size");
		mapContract(MoexContractField.QUOTATION, "Quotation");
		mapContract(MoexContractField.FIRST_TRADING_DAY, "First Trading Day");
		mapContract(MoexContractField.LAST_TRADING_DAY, "Last Trading Day");
		mapContract(MoexContractField.DELIVERY, "Delivery");
		mapContract(MoexContractField.TICK_SIZE, "Price tick");
		mapContract(MoexContractField.TICK_VALUE, "Value of price tick, RUB");
		mapContract(MoexContractField.LOWER_PRICE_LIMIT, "Lower limit");
		mapContract(MoexContractField.UPPER_PRICE_LIMIT, "Upper limit");
		mapContract(MoexContractField.SETTLEMENT_PRICE, "Settlement price of last clearing session");
		mapContract(MoexContractField.FEE, "Contract buy/sell fee, RUB");
		mapContract(MoexContractField.INTRADAY_FEE, "Intraday (scalper) fee, RUB");
		mapContract(MoexContractField.NEGOTIATION_FEE, "Negotiated trade fee, RUB");
		mapContract(MoexContractField.EXERCISE_FEE, "Contract exercise fee, RUB");
		mapContract(MoexContractField.INITIAL_MARGIN, "Initial Margin (IM, rub)");
		mapContract(MoexContractField.INITIAL_MARGIN_DATE, "IM value on");
		mapContract(MoexContractField.FX_INTRADAY_CLEARING, "FX for intraday clearing");
		mapContract(MoexContractField.FX_EVENING_CLEARING, "FX for evening clearing");
		mapContract(MoexContractField.SETTLEMENT_PROC_DESCR, "Settlement procedure");
		
		dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		clearingTimeFormat = DateTimeFormatter.ofPattern("HH:mm");
	}
	
	static void mapContract(Integer fieldID, String id) {
		stringToContractField.put(id, fieldID);
	}
	
	static void map(MoexContractType value, String id) {
		stringToContractType.put(id, value);
	}
	
	static void map(MoexQuotationType value, String id) {
		stringToQuotationType.put(id, value);
	}
	
	static void map(MoexSettlementType value, String id) {
		stringToSettlementType.put(id, value);
	}
	
	/**
	 * Convert string to settlement type.
	 * <p>
	 * @param id - the string ID
	 * @return settlement type
	 * @throws WUWebPageException - unidentified settlement type ID
	 */
	public MoexSettlementType toSettlementType(String id) throws WUWebPageException {
		MoexSettlementType value = stringToSettlementType.get(id);
		if ( value != null ) {
			return value;
		}
		throw errForm("Unidentified settlement type: [" + id + "]");
	}
	
	/**
	 * Convert string to quotation type.
	 * <p>
	 * @param id - the string ID (may be null)
	 * @return quotation type
	 * @throws WUWebPageException - unidentified quotation type ID
	 */
	public MoexQuotationType toQuotationType(String id) throws WUWebPageException {
		MoexQuotationType value = stringToQuotationType.get(id);
		if ( value != null ) {
			return value;
		}
		throw errForm("Unidentified quotation type: [" + id + "]");
	}
	
	/**
	 * Convert string to contract type.
	 * <p>
	 * @param id - the string ID
	 * @return contract type
	 * @throws WUWebPageException - unidentified contract type
	 */
	public MoexContractType toContractType(String id) throws WUWebPageException {
		MoexContractType value = stringToContractType.get(id);
		if ( value != null ) {
			return value;
		}
		throw errForm("Unidentified contract type: [" + id + "]");
	}
	
	/**
	 * Convert string to contract field ID.
	 * <p>
	 * @param id - string to convert
	 * @return contract field ID (see {@link MoexContractField}).
	 * @throws WUWebPageException - unidentified string ID
	 */
	public int toContractField(String id) throws WUWebPageException {
		Integer value = stringToContractField.get(id);
		if ( value != null ) {
			return value;
		}
		throw errForm("Unidentified contract field: [" + id + "]");
	}
	
	public int toInteger(String stringValue) throws WUWebPageException {
		try {
			return Integer.valueOf(StringUtils.replace(stringValue, ",", ""));
		} catch ( NumberFormatException e ) {
			throw errForm("Cannot convert to integer: [" + stringValue + "]", e);
		}
	}
	
	public LocalDate toLocalDate(String stringValue) throws WUWebPageException {
		try {
			return LocalDate.parse(stringValue, dateFormat);
		} catch ( DateTimeParseException e ) {
			throw errForm("Cannot convert to date: [" + stringValue + "]", e);
		}
	}
	
	public double toDouble(String stringValue) throws WUWebPageException {
		try {
			return Double.valueOf(StringUtils.replace(stringValue, ",", ""));
		} catch ( NumberFormatException e ) {
			throw errForm("Cannot convert to double: [" + stringValue + "]", e);
		}
	}
	
	public LocalTime toClearingTime(String stringValue) throws WUWebPageException {
		try {
			return LocalTime.parse(stringValue.substring(0, 5), clearingTimeFormat);
		} catch ( DateTimeParseException e ) {
			throw errForm("Cannot convert to time: [" + stringValue + "]", e);
		}
	}
	
	private WUWebPageException errForm(String msg, Throwable t) {
		return new WUWebPageException(msg, t);
	}
	
	private WUWebPageException errForm(String msg) {
		return new WUWebPageException(msg);
	}

}
