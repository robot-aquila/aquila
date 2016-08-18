package ru.prolib.aquila.utils.experimental.experiment.moex;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.data.storage.DeltaUpdate;
import ru.prolib.aquila.data.storage.DeltaUpdateBuilder;

/**
 * Accumulator of changes.
 */
public class ChangesAccumulator {
	
	/**
	 * This is set of tokens which should be changed. The works still in
	 * progress until all those tokens changed at least once.  
	 */
	private final Set<Integer> expectedChangedTokens;
	
	/**
	 * This is set of all tokens which were actually changed at least once.
	 * When this set contains all tokens of {@link #expectedChangedTokens} then
	 * the job is done. 
	 */
	private final Set<Integer> actualChangedTokens;
	
	/**
	 * This is container to store tokens data.
	 */
	private final UpdatableStateContainer container;
	
	/**
	 * Constructor.
	 * <p>
	 * @param container - the container to store data. It may contain initial data.
	 * @param expectedChangedTokens - the set of tokens which are expected to be changed
	 */
	public ChangesAccumulator(UpdatableStateContainer container, Set<Integer> expectedChangedTokens) {
		this.expectedChangedTokens = expectedChangedTokens;
		this.actualChangedTokens = new HashSet<>();
		this.container = container;
	}
	
	/**
	 * Accumulate portion of data.
	 * <p>
	 * @param tokens - data to accumulate
	 * @return true if all expected tokens were changed or if the set of
	 * expected tokens is empty then returns true when at least one (any) token
	 * changed
	 */
	public boolean accumulate(Map<Integer, Object> tokens) {
		container.update(tokens);
		actualChangedTokens.addAll(container.getUpdatedTokens());
		return expectedChangedTokens.isEmpty() ? container.hasChanged() :
				actualChangedTokens.containsAll(expectedChangedTokens);
	}
	
	/**
	 * Create a delta-update for the container changes.
	 * <p>
	 * @param snapshot - if true then create a snapshot update. In this case the
	 * delta-update will be marked as a snapshot and all tokens of the container
	 * will be placed to the delta-update. Otherwise the delta-update will be
	 * marked as a regular update and only actually changed tokens will be
	 * placed to the delta-update.
	 * @param updateTime - the time of delta-update
	 * @return the delta-update
	 */
	public DeltaUpdate createDeltaUpdate(boolean snapshot, Instant updateTime) {
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
			.withSnapshot(snapshot)
			.withTime(updateTime);
		if ( snapshot ) {
			builder.withTokens(container.getContent());
		} else {
			for ( Integer token : actualChangedTokens ) {
				builder.withToken(token, container.getObject(token));
			}
		}
		return builder.buildUpdate();
	}
	
	/**
	 * Check for any data stored in the accumulator.
	 * <p>
	 * This method is useful to check for data existence after a series of
	 * updates. If no data then it's time to stop trying.
	 * <p>
	 * @return true if it has at least one token, false otherwise
	 */
	public boolean hasData() {
		return container.hasData();
	}
	
	/**
	 * Check for any changes of the data.
	 * <p>
	 * @return true if it has at least token which was changed among all calls,
	 * false otherwise
	 */
	public boolean hasChanges() {
		return ! actualChangedTokens.isEmpty();
	}
	
}
