package ru.prolib.aquila.core.data;

import java.time.Instant;
import java.util.Set;

import ru.prolib.aquila.core.EventType;

/**
 * Observable and thread-safe data container.
 * <p>
 * The data container gives an access to a set of values which identified by
 * numeric token ID. Container allows track when and which tokens changed.
 */
public interface Container {
	
	/**
	 * Get container ID.
	 * <p>
	 * @return container ID
	 */
	public String getContainerID();
	
	/**
	 * Lock container.
	 */
	public void lock();
	
	/**
	 * Unlock container.
	 */
	public void unlock();
	
	/**
	 * Close container.
	 */
	public void close();
	
	/**
	 * When container is available for reading.
	 * <p>
	 * @return event type
	 */
	public EventType onAvailable();

	/**
	 * When container updated.
	 * <p>
	 * Allows catching events {@link ContainerEvent}.
	 * <p>
	 * @return event type
	 */
	public EventType onUpdate();
	
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
	 * Test tokens for changes.
	 * <p>
	 * Test those tokens were changed at last update.
	 * <p>
	 * @param tokens - array of token IDs
	 * @return true if at least one of specified tokens has changed within
	 * current update, false otherwise
	 */
	public boolean atLeastOneHasChanged(int[] tokens);
	
	public boolean isAvailable();
	
	public boolean isClosed();

}
