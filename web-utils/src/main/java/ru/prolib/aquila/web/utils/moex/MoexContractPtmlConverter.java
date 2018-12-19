package ru.prolib.aquila.web.utils.moex;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.data.DataFormatException;
import ru.prolib.aquila.data.storage.file.PtmlDeltaUpdateConverter;

public class MoexContractPtmlConverter implements PtmlDeltaUpdateConverter {
	private static final DateTimeFormatter moexDateFormat;
	
	static {
		moexDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	}

	@Override
	public String toString(int token, Object value) throws DataFormatException {
		switch ( token ) {
		case MoexContractField.CONTRACT_DESCR:
		case MoexContractField.DELIVERY:
		case MoexContractField.FIRST_TRADING_DAY:
		case MoexContractField.FX_EVENING_CLEARING:
		case MoexContractField.FX_INTRADAY_CLEARING:
		case MoexContractField.INITIAL_MARGIN_DATE:
		case MoexContractField.LAST_TRADING_DAY:
		case MoexContractField.LOT_SIZE:
		case MoexContractField.LOWER_PRICE_LIMIT:
		case MoexContractField.QUOTATION:
		case MoexContractField.SETTLEMENT:
		case MoexContractField.SETTLEMENT_PRICE:
		case MoexContractField.SETTLEMENT_PROC_DESCR:
		case MoexContractField.SYMBOL:
		case MoexContractField.SYMBOL_CODE:
		case MoexContractField.TICK_SIZE:
		case MoexContractField.TYPE:
		case MoexContractField.UPPER_PRICE_LIMIT:
			return value.toString();
		case MoexContractField.TICK_VALUE:
		case MoexContractField.FEE:
		case MoexContractField.INTRADAY_FEE:
		case MoexContractField.NEGOTIATION_FEE:
		case MoexContractField.EXERCISE_FEE:
		case MoexContractField.INITIAL_MARGIN:
			return ((CDecimal) value).toBigDecimal().toString();
		default:
			throw new DataFormatException("Unknown token: " + token);
		}
	}

	@Override
	public Object toObject(int token, String value) throws DataFormatException {
		switch ( token ) {
		case MoexContractField.SYMBOL:
		case MoexContractField.SYMBOL_CODE:
		case MoexContractField.CONTRACT_DESCR:
		case MoexContractField.SETTLEMENT_PROC_DESCR:
		case MoexContractField.TYPE:
		case MoexContractField.SETTLEMENT:
		case MoexContractField.QUOTATION:
			return value;
		case MoexContractField.FIRST_TRADING_DAY:
		case MoexContractField.LAST_TRADING_DAY:
		case MoexContractField.DELIVERY:
		case MoexContractField.INITIAL_MARGIN_DATE:
			try {
				return LocalDate.parse(value);
			} catch ( DateTimeParseException e ) {
				// Possible generic case when parsing a date from MOEX website
			}
			try {
				return LocalDate.parse(value, moexDateFormat);
			} catch ( DateTimeParseException e ) {
				throw new DataFormatException("Cannot parse date: " + value, e);
			}
		case MoexContractField.TICK_SIZE:
		case MoexContractField.LOWER_PRICE_LIMIT:
		case MoexContractField.UPPER_PRICE_LIMIT:
		case MoexContractField.SETTLEMENT_PRICE:
		case MoexContractField.LOT_SIZE:
			try {
				return new CDecimalBD(toBigDecimal(value));
			} catch ( NumberFormatException e ) {
				throw new DataFormatException("Cannot parse decimal value: " + value, e);
			}
		case MoexContractField.TICK_VALUE:
		case MoexContractField.FEE:
		case MoexContractField.INTRADAY_FEE:
		case MoexContractField.NEGOTIATION_FEE:
		case MoexContractField.EXERCISE_FEE:
		case MoexContractField.INITIAL_MARGIN:
			try {
				return new CDecimalBD(toBigDecimal(value), CDecimalBD.RUB);
			} catch ( NumberFormatException e ) {
				throw new DataFormatException("Cannot parse money value: " + value, e);
			}
		case MoexContractField.FX_INTRADAY_CLEARING:
		case MoexContractField.FX_EVENING_CLEARING:
			try {
				return LocalTime.parse(value.substring(0, 5));
			} catch ( DateTimeParseException e ) {
				throw new DataFormatException("Cannot parse time: " + value, e);
			}
		default:
			return value;
			//throw new DataFormatException("Unknown token: " + token);
		}
	}
	
	private BigDecimal toBigDecimal(String value) {
		value = value.replace(",", "");
		value = new BigDecimal(value).toPlainString();
		if ( value.contains(".") ) {
			//value = StringUtils.trimTrailingCharacter(value, '0');
			//value = StringUtils.trimTrailingCharacter(value, '.');
			value = value.replaceAll("\\.?0+$", "");
		}
		return new BigDecimal(value);
	}

}
