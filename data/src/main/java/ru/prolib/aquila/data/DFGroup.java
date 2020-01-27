package ru.prolib.aquila.data;

import java.util.Map;

/**
 * Set of states of different data feeds united by same key.
 * <p>
 * @param <KeyType> - group key type
 * @param <FeedType> - feed identifier type
 */
public class DFGroup<KeyType, FeedType> {
	protected final KeyType key;
	protected final Map<FeedType, DFSubscrState> states;
	protected boolean notFound;
	
	public DFGroup(KeyType key, Map<FeedType, DFSubscrState> states) {
		this.key = key;
		this.states = states;
		this.notFound = false;
	}
	
	/**
	 * Get feed state instance.
	 * <p>
	 * @param feed_id - data feed ID
	 * @return state instance
	 * @throws NullPointerException - data feed not exists
	 */
	protected DFSubscrState getState(FeedType feed_id) {
		DFSubscrState state = states.get(feed_id);
		if ( state == null ) {
			throw new NullPointerException("Feed state not found: " + key + "#" + feed_id);
		}
		return state;
	}
	
	/**
	 * Get key of this group.
	 * <p>
	 * @return group key
	 */
	public KeyType getKey() {
		return key;
	}
	
	/**
	 * Check this group (key) was marked as non-existent.
	 * <p>
	 * @return true if key not exists. Getting false as result does not mean that key exists.
	 */
	public boolean isNotFound() {
		return notFound;
	}

	/**
	 * Get feed status.
	 * <p>
	 * @param feed_id - data feed ID
	 * @return data feed status
	 * @throws NullPointerException - data feed not exists
	 */
	public DFSubscrStatus getFeedStatus(FeedType feed_id) {
		return getState(feed_id).getStatus();
	}
	
	/**
	 * Change data feed status.
	 * <p>
	 * @param feed_id - data feed ID
	 * @param new_status - new status to switch to
	 * @throws NullPointerException - data feed not exists
	 */
	public void setFeedStatus(FeedType feed_id, DFSubscrStatus new_status) {
		getState(feed_id).switchTo(new_status);
	}
	
	/**
	 * Mark this group (key) as non-existent or not available.
	 * <p>
	 * @param not_found - true to mark, false to reset the mark
	 */
	public void setNotFound(boolean not_found) {
		this.notFound = not_found;
	}

	/**
	 * Mark to subscribe to data feed.
	 * <p>
	 * @param feed_id - data feed ID
	 * @return true if need to subscribe on data feed, false otherwise
	 * @throws NullPointerException - data feed not exists
	 * @throws IllegalStateException - illegal status detected
	 */
	public boolean markToSubscribe(FeedType feed_id) {
		DFSubscrState state = getState(feed_id);
		switch ( state.getStatus() ) {
		case NOT_SUBSCR:
		case PENDING_SUBSCR:
			state.switchTo(DFSubscrStatus.PENDING_SUBSCR);
			return true;
		case PENDING_UNSUBSCR:
			// Have to switch back to subscribed status.
			// And do nothing with actual feed - it is already subscribed as expected.
			state.switchTo(DFSubscrStatus.SUBSCR);
			return false;
		case SUBSCR:
			// Already subscribed - nothing to do.
			return false;
		case NOT_AVAILABLE:
			// This data feed is not available.
			// Nothing to do.
			return false;
		default:
			throw new IllegalStateException("Unexpected status: " + state.getStatus());
		}
	}
	
	/**
	 * Mark to unsubscribe of data feed.
	 * <p>
	 * @param feed_id - data feed ID
	 * @return true if need to stop subscription on data feed, false otherwise
	 * @throws NullPointerException - data feed not exists
	 * @throws IllegalStateException - illegal status detected
	 */
	public boolean markToUnsubscribe(FeedType feed_id) {
		DFSubscrState state = getState(feed_id);
		switch ( state.getStatus() ) {
		case SUBSCR:
		case PENDING_UNSUBSCR:
			state.switchTo(DFSubscrStatus.PENDING_UNSUBSCR);
			return true;
		case PENDING_SUBSCR:
			state.switchTo(DFSubscrStatus.NOT_SUBSCR);
			return false;
		case NOT_SUBSCR:
			return false;
		case NOT_AVAILABLE:
			return false;
		default:
			throw new IllegalStateException("Unexpected status: " + state.getStatus());
		}
	}
	
	/**
	 * Mark all data feeds as not subscribed.
	 * <p>
	 * @throws IllegalStateException - illegal status detected
	 */
	public void markAllNotSubscribed() {
		for ( DFSubscrState state : states.values() ) {
			switch ( state.getStatus() ) {
			case NOT_SUBSCR:
			case NOT_AVAILABLE:
				break;
			case PENDING_SUBSCR:
			case SUBSCR:
			case PENDING_UNSUBSCR:
				state.switchTo(DFSubscrStatus.NOT_SUBSCR);
				break;
			default:
				throw new IllegalStateException("Unexpected status: " + state.getStatus());
			}
		}
	}
	
}
