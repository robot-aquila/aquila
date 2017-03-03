package ru.prolib.aquila.web.utils.moex;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.springframework.util.StringUtils;

import ru.prolib.aquila.core.BusinessEntities.FDecimal;
import ru.prolib.aquila.core.BusinessEntities.FMoney;
import ru.prolib.aquila.data.DataFormatException;
import ru.prolib.aquila.data.storage.file.PtmlDeltaUpdateConverter;

public class MoexContractPtmlConverter implements PtmlDeltaUpdateConverter {

	@Override
	public String toString(int token, Object value) throws DataFormatException {
		switch ( token ) {
		case MoexContractField.CONTRACT_DESCR:
		case MoexContractField.DELIVERY:
		case MoexContractField.EXERCISE_FEE:
		case MoexContractField.FEE:
		case MoexContractField.FIRST_TRADING_DAY:
		case MoexContractField.FX_EVENING_CLEARING:
		case MoexContractField.FX_INTRADAY_CLEARING:
		case MoexContractField.INITIAL_MARGIN:
		case MoexContractField.INITIAL_MARGIN_DATE:
		case MoexContractField.INTRADAY_FEE:
		case MoexContractField.LAST_TRADING_DAY:
		case MoexContractField.LOT_SIZE:
		case MoexContractField.LOWER_PRICE_LIMIT:
		case MoexContractField.NEGOTIATION_FEE:
		case MoexContractField.QUOTATION:
		case MoexContractField.SETTLEMENT:
		case MoexContractField.SETTLEMENT_PRICE:
		case MoexContractField.SETTLEMENT_PROC_DESCR:
		case MoexContractField.SYMBOL:
		case MoexContractField.SYMBOL_CODE:
		case MoexContractField.TICK_SIZE:
		case MoexContractField.TICK_VALUE:
		case MoexContractField.TYPE:
		case MoexContractField.UPPER_PRICE_LIMIT:
			return value.toString();
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
			return value;
		case MoexContractField.TYPE:
			try {
				return MoexContractType.valueOf(value);
			} catch ( IllegalArgumentException e ) {
				throw new DataFormatException("Cannot identify contract type: " + value, e);
			}
		case MoexContractField.SETTLEMENT:
			try {
				return MoexSettlementType.valueOf(value);
			} catch ( IllegalArgumentException e ) {
				throw new DataFormatException("Cannot identify settlement type: " + value, e);
			}
		case MoexContractField.LOT_SIZE:
			try {
				return Integer.valueOf(value);
			} catch ( NumberFormatException e ) {
				throw new DataFormatException("Cannot parse integer: " + value, e);
			}
		case MoexContractField.QUOTATION:
			try {
				return MoexQuotationType.valueOf(value);
			} catch ( IllegalArgumentException e ) {
				throw new DataFormatException("Cannot identify quotation type: " + value, e);
			}
		case MoexContractField.FIRST_TRADING_DAY:
		case MoexContractField.LAST_TRADING_DAY:
		case MoexContractField.DELIVERY:
		case MoexContractField.INITIAL_MARGIN_DATE:
			try {
				return LocalDate.parse(value);
			} catch ( DateTimeParseException e ) {
				throw new DataFormatException("Cannot parse date: " + value, e);
			}
		case MoexContractField.TICK_SIZE:
			try {
				return new FDecimal(removeTrailingZeroes(value));
			} catch ( NumberFormatException e ) {
				throw new DataFormatException("Cannot parse tick size: " + value, e);
			}
		case MoexContractField.TICK_VALUE:
			try {
				return new FMoney(removeTrailingZeroes(value), "RUB");
			} catch ( NumberFormatException e ) {
				throw new DataFormatException("Cannot parse tick value: " + value, e);
			}
		case MoexContractField.LOWER_PRICE_LIMIT:
		case MoexContractField.UPPER_PRICE_LIMIT:
		case MoexContractField.SETTLEMENT_PRICE:
		case MoexContractField.FEE:
		case MoexContractField.INTRADAY_FEE:
		case MoexContractField.NEGOTIATION_FEE:
		case MoexContractField.EXERCISE_FEE:
		case MoexContractField.INITIAL_MARGIN:
			try {
				return Double.valueOf(value);
			} catch ( NumberFormatException e ) {
				throw new DataFormatException("Cannot parse double: " + value, e);
			}
		case MoexContractField.FX_INTRADAY_CLEARING:
		case MoexContractField.FX_EVENING_CLEARING:
			try {
				return LocalTime.parse(value);
			} catch ( DateTimeParseException e ) {
				throw new DataFormatException("Cannot parse time: " + value, e);
			}
		default:
			throw new DataFormatException("Unknown token: " + token);
		}
	}
	
	private String removeTrailingZeroes(String doubleValue) {
		// it may contains a scientific notation
		doubleValue = new BigDecimal(doubleValue).toString();
		doubleValue = StringUtils.trimTrailingCharacter(doubleValue, '0');
		doubleValue = StringUtils.trimTrailingCharacter(doubleValue, '.');
		return doubleValue;
	}

}
