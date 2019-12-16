package ru.prolib.aquila.core.BusinessEntities;

import java.io.Serializable;
import java.util.Currency;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.*;

/**
 * Symbol is a key entity of the trading system. It is used to identify unique security.
 * <p>
 * The symbol contains four parts: symbol code or ticker, exchange, type and currency codes.
 * Only one part is mandatory. It is the primary symbol code. Other three parts may be omitted.
 * All four parts are string values. It allows to use the symbol class for all possible combinations
 * even if it is not known by the system. The symbol class is also mutable. It is possible
 * derive new classes for specific purposes (for example to save symbols to a database).
 * <p>
 * Each unique symbol can be represented in string format. It is also possible parse a symbol string
 * to a symbol instance. This may be done using constructor with one string argument.
 * The string of full symbol representation does have the following structure: 
 * <pre>
 * S:AAPL@NASDAQ:USD
 * |   |     |    |_ currency code
 * |   |     |_ exchange ID
 * |   |_ symbol code
 * |_ symbol type code 
 * </pre>
 * <b>Symbol type</b> code is used to inform of type of the security. Known types may be automatically
 * transformed to one of {@link SymbolType} constant by calling of method {@link Symbol#getType()}.
 * If type code is unknown by the system then {@link SymbolType#UNKNOWN} will be returned.
 * <p>
 * <b>Symbol code</b> is a primary ticker of the security.
 * <p>
 * <b>Exchange ID</b> may be used to identify specific exchange or trading section. Omitting this
 * part may be interpreted as global ticker.
 * <p>
 * <b>Currency code</b> is any string to identify currency which is used to quote the ticker.
 * ISO 4217 currency codes may be automatically converted to a {@link Currency} instance by calling
 * the {@link Symbol#getCurrency()} method.
 * <p>
 * Those are some valid examples of full symbol representation in string format:
 * <pre>
 * AAPL
 * S:AAPL
 * AAPL@NASDAQ
 * S:AAPL@:USD
 * </pre>
 */
public class Symbol implements Serializable, Comparable<Symbol> {
	private static final long serialVersionUID = 1L;
	private final String code;
	private final String exchangeID;
	private final String currencyCode;
	private final String typeCode;
	
	/**
	 * Constructor.
	 * <p>
	 * @param code - symbol code
	 * @param exchangeID - exchange or section ID
	 * @param currency - currency 
	 * @param type - symbol type
	 */
	public Symbol(String code, String exchangeID, Currency currency, SymbolType type) {
		this(code, exchangeID, currency.getCurrencyCode(), type.getCode());
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param code - symbol code
	 * @param exchangeID - exchange or section ID
	 * @param currencyCode - currency code
	 * @param type - symbol type
	 */
	public Symbol(String code, String exchangeID, String currencyCode, SymbolType type) {
		this(code, exchangeID, currencyCode, type.getCode());
	}
	
	/**
	 * Constructor.
	 * <p>
	 * Creates symbol of {@link SymbolType#STOCK} symbol type.
	 * <p>
	 * @param code - symbol code
	 * @param exchangeID - exchange or section ID
	 * @param currencyCode - ISO 4217 currency code
	 */
	public Symbol(String code, String exchangeID, String currencyCode) {
		this(code, exchangeID, currencyCode, SymbolType.STOCK);
	}
	
	/**
	 * Constructor.
	 * <p>
	 * Creates symbol of {@link SymbolType#STOCK} symbol type.
	 * <p>
	 * @param code - symbol code
	 * @param exchangeID - exchange or section ID
	 * @param currency - currency
	 */
	public Symbol(String code, String exchangeID, Currency currency) {
		this(code, exchangeID, currency, SymbolType.STOCK);
	}
	
	/**
	 * Constructor.
	 * <p>
	 * Creates symbol from string representation of symbol.
	 * <p>
	 * @param symbol - string representation of symbol
	 */
	public Symbol(String symbol) { 
		String tokens[] = StringUtils.splitByWholeSeparatorPreserveAllTokens(symbol, "@");
		if ( tokens.length == 1 ) {
			if ( symbol.indexOf(':') >= 0 ) {
				throw new IllegalArgumentException("Ambiguous token: " + symbol);
			}
			this.code = symbol;
			this.exchangeID = null;
			this.currencyCode = null;
			this.typeCode = null;
			return;
		} else if ( tokens.length == 2 ) {
			String left[] = StringUtils.splitByWholeSeparatorPreserveAllTokens(tokens[0], ":");
			String right[] = StringUtils.splitByWholeSeparatorPreserveAllTokens(tokens[1], ":");
			if ( (left.length == 1 || left.length == 2) && (right.length == 1 || right.length == 2) ) {
				if ( left.length == 1 ) {
					this.code = left[0];
					this.typeCode = null;
				} else {
					this.typeCode = (left[0].length() > 0 ? left[0] : null);
					this.code = left[1];
				}
				if ( right[0].length() > 0 ) {
					this.exchangeID = right[0];
				} else {
					this.exchangeID = null;
				}
				if ( right.length == 2 && right[1].length() > 0 ) {
					this.currencyCode = right[1];
				} else {
					this.currencyCode = null;
				}
				return;
			}
		}
		throw new IllegalArgumentException("Invalid symbol format: " + symbol);
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param code - symbol code
	 * @param exchangeID - exchange or section ID
	 * @param currencyCode - currency code
	 * @param typeCode - type code
	 */
	public Symbol(String code, String exchangeID, String currencyCode, String typeCode) {
		if ( code == null ) {
			throw new NullPointerException();
		}
		if ( code.length() == 0 ) {
			throw new IllegalArgumentException("The symbol code cannot be empty");
		}
		if ( exchangeID != null && exchangeID.length() == 0 ) {
			throw new IllegalArgumentException("Exchange ID cannot be empty");
		}
		if ( currencyCode != null && currencyCode.length() == 0 ) {
			throw new IllegalArgumentException("Currency code cannot be empty");
		}
		if ( typeCode != null && typeCode.length() == 0 ) {
			throw new IllegalArgumentException("Type code cannot be empty");
		}
		this.code = code;
		this.exchangeID = exchangeID;
		this.currencyCode = currencyCode;
		this.typeCode = typeCode;
	}
	
	/**
	 * Get symbol code.
	 * <p>
	 * @return symbol code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * Get exchange ID.
	 * <p>
	 * @return exchange ID
	 */
	public String getExchangeID() {
		return exchangeID;
	}
	
	/**
	 * Get currency.
	 * <p>
	 * @return currency
	 */
	public Currency getCurrency() {
		return Currency.getInstance(currencyCode);
	}
	
	/**
	 * Get currency code.
	 * <p>
	 * @return currency code
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	/**
	 * Get symbol type.
	 * <p>
	 * This method converts type code to symbol type constant.
	 * <p>
	 * @return type of symbol
	 */
	public SymbolType getType() {
		return SymbolType.valueOf(typeCode);
	}
	
	/**
	 * Get symbol type code.
	 * <p>
	 * @return type code
	 */
	public String getTypeCode() {
		return typeCode;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( !(other instanceof Symbol) ) {
			return false;
		}
		Symbol o = (Symbol)other;
		return new EqualsBuilder()
			.append(code, o.code)
			.append(exchangeID, o.exchangeID)
			.append(currencyCode, o.currencyCode)
			.append(typeCode, o.typeCode)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(code)
			.append(exchangeID)
			.append(currencyCode)
			.append(typeCode)
			.build();
	}
	
	@Override
	public String toString() {
		return (typeCode == null ? "" : typeCode + ":") + code +
				(exchangeID == null ? "" : "@" + exchangeID) +
				(currencyCode == null ? "" : ":" + currencyCode);
	}

	@Override
	public int compareTo(Symbol o) {
		return toString().compareTo(o.toString());
	}

}
