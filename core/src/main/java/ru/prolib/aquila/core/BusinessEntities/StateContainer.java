package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

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
	 * Get integer value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Integer getInteger(int token);

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
	 * Get double value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Double getDouble(int token);

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
	 * Get java time value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 * @throws ClassCastException - cannot cast token value
	 */
	public Instant getInstant(int token);

	/**
	 * Get object value.
	 * <p>
	 * @param token - token ID
	 * @return token value or null if token is not available
	 * @throws IllegalStateException - container is closed or inaccessible
	 */
	public Object getObject(int token);

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
	 * Get updated tokens.
	 * <p>
	 * Get tokens which were changed at last update.
	 * <p>
	 * @return set of updated tokens
	 */
	public Set<Integer> getUpdatedTokens();

	/**
	 * Test token for changes.
	 * <p>
	 * Test that token was changed at last update.
	 * <p>
	 * @param token - token ID
	 * @return true if token has changed within current update, false otherwise
	 */
	public boolean hasChanged(int token);

	/**
	 * Test for changes.
	 * <p>
	 * Test that container was changed at last update.
	 * <p>
	 * @return true if token has changed within current update, false otherwise
	 */
	public boolean hasChanged();

	/**
	 * Test tokens for changes.
	 * <p>
	 * Test those tokens were changed at last update.
	 * <p>
	 * @param tokens - array of token IDs
	 * @return true if at least one of specified tokens has changed within
	 * current update, false otherwise
	 */
	public boolean atLeastOneHasChanged(int[] tokens);

	/**
	 * Get content of the container.
	 * <p>
	 * @return container content
	 */
	public Map<Integer, Object> getContent();

	/**
	 * Get updated content.
	 * <p>
	 * Get content which was changed at the last update.
	 * <p>
	 * @return updated content
	 */
	public Map<Integer, Object> getUpdatedContent();
	
	/**
	 * Test for data existence.
	 * <p>
	 * Test that container has at least one token.
	 * <p>
	 * @return true if tokens exists, false otherwise
	 */
	public boolean hasData();

}
