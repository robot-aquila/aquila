package ru.prolib.aquila.core.BusinessEntities;

import java.util.Map;
import java.util.Set;

public interface UpdatableStateContainer extends StateContainer, DeltaUpdateConsumer {
	
	public void update(Map<Integer, Object> tokens);

	public void update(int token, Object value);
	
	public void resetChanges();

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
	 * Get updated contents.
	 * <p>
	 * Get contents which was changed at the last update.
	 * <p>
	 * @return updated contents
	 */
	public Map<Integer, Object> getUpdatedContents();

}
