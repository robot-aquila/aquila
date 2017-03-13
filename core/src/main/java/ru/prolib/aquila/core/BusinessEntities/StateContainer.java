package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;

public interface StateContainer extends AbstractContainer {

	/**
	 * Get string value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public String getString(int token);
	
	/**
	 * Get string value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public String getString(int token, String defaultValue);

	/**
	 * Get integer value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Integer getInteger(int token);
	
	/**
	 * Get integer value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Integer getInteger(int token, Integer defaultValue);
	
	/**
	 * Get integer value.
	 * <p>
	 * @param token - token ID
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Integer getIntegerOrZero(int token);

	/**
	 * Get long value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Long getLong(int token);
	
	/**
	 * Get long value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Long getLong(int token, Long defaultValue);

	/**
	 * Get long value.
	 * <p>
	 * @param token - token ID
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Long getLongOrZero(int token);

	/**
	 * Get double value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Double getDouble(int token);
	
	/**
	 * Get double value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Double getDouble(int token, Double defaultValue);
	
	/**
	 * Get double value.
	 * <p>
	 * @param token - token ID
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Double getDoubleOrZero(int token);

	/**
	 * Get boolean value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Boolean getBoolean(int token);
	
	/**
	 * Get boolean value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Boolean getBoolean(int token, Boolean defaultValue);

	/**
	 * Get java time value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Instant getInstant(int token);
	
	/**
	 * Get java time value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Instant getInstant(int token, Instant defaultValue);

	/**
	 * Get object value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 */
	public Object getObject(int token);
	
	/**
	 * Get object value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Object getObject(int token, Object defaultValue);
	
	/**
	 * Get decimal value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FDecimal getDecimal(int token);
	
	/**
	 * Get decimal value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FDecimal getDecimal(int token, FDecimal defaultValue);
	
	/**
	 * Get decimal value or zero if not available.
	 * <p>
	 * @param token - token ID
	 * @param scale - scale to create zero value
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FDecimal getDecimalOrZero(int token, int scale);
	
	/**
	 * Get decimal value or zero of scale 0 if not available.
	 * <p>
	 * @param token - token ID
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FDecimal getDecimalOrZero0(int token);
	
	/**
	 * Get decimal value or zero of scale 1 if not available.
	 * <p>
	 * @param token - token ID
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FDecimal getDecimalOrZero1(int token);
	
	/**
	 * Get decimal value or zero of scale 2 if not available.
	 * <p>
	 * @param token - token ID
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FDecimal getDecimalOrZero2(int token);
	
	/**
	 * Get decimal value or zero of scale 3 if not available.
	 * <p>
	 * @param token - token ID
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FDecimal getDecimalOrZero3(int token);
	
	/**
	 * Get decimal value or zero of scale 4 if not available.
	 * <p>
	 * @param token - token ID
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FDecimal getDecimalOrZero4(int token);
	
	/**
	 * Get money value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FMoney getMoney(int token);
	
	/**
	 * Get money value.
	 * <p>
	 * @param token - token ID
	 * @param defaultValue - default value
	 * @return token value or default value if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FMoney getMoney(int token, FMoney defaultValue);
	
	/**
	 * Get money value or zero if not available.
	 * <p>
	 * @param token - token ID
	 * @param scale - scale to create zero value
	 * @param currencyCode - currency code to create zero value
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FMoney getMoneyOrZero(int token, int scale, String currencyCode);
	
	/**
	 * Get money value or zero of scale 0 if not available.
	 * <p>
	 * @param token - token ID
	 * @param currencyCode - currency code to create zero value
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FMoney getMoneyOrZero0(int token, String currencyCode);
	
	/**
	 * Get money value or zero of scale 1 if not available.
	 * <p>
	 * @param token - token ID
	 * @param currencyCode - currency code to create zero value
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FMoney getMoneyOrZero1(int token, String currencyCode);
	
	/**
	 * Get money value or zero of scale 2 if not available.
	 * <p>
	 * @param token - token ID
	 * @param currencyCode - currency code to create zero value
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FMoney getMoneyOrZero2(int token, String currencyCode);
	
	/**
	 * Get money value or zero of scale 3 if not available.
	 * <p>
	 * @param token - token ID
	 * @param currencyCode - currency code to create zero value
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FMoney getMoneyOrZero3(int token, String currencyCode);
	
	/**
	 * Get money value or zero of scale 4 if not available.
	 * <p>
	 * @param token - token ID
	 * @param currencyCode - currency code to create zero value
	 * @return token value or zero if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public FMoney getMoneyOrZero4(int token, String currencyCode);

	/**
	 * Test that specified tokens are defined.
	 * <p>
	 * This method tests that the specified tokens are exists and associated
	 * with not null values.
	 * <p>
	 * @param tokens - array of tokens to test
	 * @return true if all tokens has non-null values, false otherwise
	 */
	public boolean isDefined(int[] tokens);

	/**
	 * Test that specified token is defined.
	 * <p>
	 * @param token - token to test
	 * @return true if token has non-null value, false otherwise
	 */
	public boolean isDefined(int token);

	/**
	 * Get content of the container.
	 * <p>
	 * @return container content
	 */
	public Map<Integer, Object> getContent();

	/**
	 * Test for data existence.
	 * <p>
	 * Test that container has at least one token.
	 * <p>
	 * @return true if tokens exists, false otherwise
	 */
	public boolean hasData();

}
