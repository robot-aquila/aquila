package ru.prolib.aquila.web.utils.moex;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.web.utils.WUWebPageException;

public class MoexContractFormUtils {
	private static final Map<String, Integer> stringToContractField;
	
	static {
		stringToContractField = new HashMap<>();
		mapContract(MoexContractField.SYMBOL, "Contract Symbol");
		mapContract(MoexContractField.SYMBOL_CODE, "Contract Trading Symbol");
		mapContract(MoexContractField.CONTRACT_DESCR, "Contract Description");
		mapContract(MoexContractField.TYPE, "Type");
		mapContract(MoexContractField.SETTLEMENT, "Settlement");
		mapContract(MoexContractField.LOT_SIZE, "Сontract size (lot)");
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
		mapContract(MoexContractField.INITIAL_MARGIN, "First level of Initial margin concentration limit*");
		mapContract(MoexContractField.INITIAL_MARGIN_DATE, "IM value on");
		mapContract(MoexContractField.FX_INTRADAY_CLEARING, "FX for intraday clearing");
		mapContract(MoexContractField.FX_EVENING_CLEARING, "FX for evening clearing");
		mapContract(MoexContractField.SETTLEMENT_PROC_DESCR, "Settlement procedure");
	}
	
	static void mapContract(Integer fieldID, String id) {
		stringToContractField.put(id, fieldID);
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
	
	private WUWebPageException errForm(String msg) {
		return new WUWebPageException(msg);
	}

}
