package ru.prolib.aquila.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;

public class DFGroupRepo<KeyType, FeedType> implements Lockable {
	private final LID lid;
	private final Lock lock;
	private final Map<KeyType, DFGroup<KeyType, FeedType>> groups;
	private final DFGroupFactory<KeyType, FeedType> factory;
	
	DFGroupRepo(LID lid,
			Lock lock,
			Map<KeyType, DFGroup<KeyType, FeedType>> groups,
			DFGroupFactory<KeyType, FeedType> factory)
	{
		this.lid = lid;
		this.lock = lock;
		this.groups = groups;
		this.factory = factory;
	}
	
	public DFGroupRepo(DFGroupFactory<KeyType, FeedType> factory) {
		this(LID.createInstance(), new ReentrantLock(), new LinkedHashMap<>(), factory);
	}

	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}
	
	private DFGroup<KeyType, FeedType> getOrCreate(KeyType key) {
		DFGroup<KeyType, FeedType> x = groups.get(key);
		if ( x == null ) {
			x = factory.produce(key);
			groups.put(key, x);
		}
		return x;
	}
	
	private Collection<KeyType> getKeysWithStatus(FeedType feed_id, DFSubscrStatus expected_status) {
		lock();
		try {
			List<KeyType> keys = new ArrayList<>();
			Iterator<Map.Entry<KeyType, DFGroup<KeyType, FeedType>>> it = groups.entrySet().iterator();
			while ( it.hasNext() ) {
				Map.Entry<KeyType, DFGroup<KeyType, FeedType>> entry = it.next();
				if ( entry.getValue().getFeedStatus(feed_id) == expected_status ) {
					keys.add(entry.getKey());
				}
			}
			return keys;
		} finally {
			unlock();
		}		
	}
	
	private void setFeedStatus(Collection<KeyType> keys, FeedType feed_id, DFSubscrStatus new_status) {
		lock();
		try {
			for ( KeyType key : keys ) {
				getOrCreate(key).setFeedStatus(feed_id, new_status);
			}
		} finally {
			unlock();
		}
	}
	
	private void setFeedStatus(KeyType key, FeedType feed_id, DFSubscrStatus new_status) {
		lock();
		try {
			getOrCreate(key).setFeedStatus(feed_id, new_status);
		} finally {
			unlock();
		}
	}
	
	/**
	 * Get keys which are pending to subscribe for specified data feed.
	 * <p>
	 * @param feed_id - data feed ID
	 * @return collection of keys
	 */
	public Collection<KeyType> getPendingSubscr(FeedType feed_id) {
		return getKeysWithStatus(feed_id, DFSubscrStatus.PENDING_SUBSCR);
	}
	
	/**
	 * Get keys which are subscribed for specified data feed.
	 * <p>
	 * @param feed_id - data feed ID
	 * @return collection of keys
	 */
	public Collection<KeyType> getSubscribed(FeedType feed_id) {
		return getKeysWithStatus(feed_id, DFSubscrStatus.SUBSCR);
	}
	
	/**
	 * Get keys which are pending to unsubscribe for specified data feed.
	 * <p>
	 * @param feed_id - data feed ID
	 * @return collection of keys
	 */
	public Collection<KeyType> getPendingUnsubscr(FeedType feed_id) {
		return getKeysWithStatus(feed_id, DFSubscrStatus.PENDING_UNSUBSCR);
	}
	
	/**
	 * Mark that specified keys are subscribed for the data feed.
	 * <p>
	 * @param keys - keys
	 * @param feed_id - data feed ID
	 */
	public void subscribed(Collection<KeyType> keys, FeedType feed_id) {
		setFeedStatus(keys, feed_id, DFSubscrStatus.SUBSCR);
	}
	
	/**
	 * Mark specified key is subscribed for the data feed.
	 * <p>
	 * @param key - key
	 * @param feed_id - data feed ID
	 */
	public void subscribed(KeyType key, FeedType feed_id) {
		setFeedStatus(key, feed_id, DFSubscrStatus.SUBSCR);
	}
	
	/**
	 * Mark that specified keys are not subscribed for the data feed.
	 * <p>
	 * @param keys - keys
	 * @param feed_id - data feed ID
	 */
	public void unsubscribed(Collection<KeyType> keys, FeedType feed_id) {
		setFeedStatus(keys, feed_id, DFSubscrStatus.NOT_SUBSCR);
	}
	
	/**
	 * Mark specified key is not subscribed for the data feed.
	 * <p>
	 * @param key - key
	 * @param feed_id - data feed ID
	 */
	public void unsubscribed(KeyType key, FeedType feed_id) {
		setFeedStatus(key, feed_id, DFSubscrStatus.NOT_SUBSCR);
	}
	
	/**
	 * Mark all data feeds not subscribed.
	 */
	public void unsubscribed() {
		lock();
		try {
			for ( DFGroup<KeyType, FeedType> group : groups.values() ) {
				group.markAllNotSubscribed();
			}
		} finally {
			unlock();
		}
	}
	
	/**
	 * Mark that data is not available for the key.
	 * <p>
	 * @param key - key
	 */
	public void notAvailable(KeyType key) {
		lock();
		try {
			getOrCreate(key).setNotFound(true);
		} finally {
			unlock();
		}
	}

	/**
	 * Mark that system has to be subscribed to specified data feed.
	 * <p>
	 * @param key - key
	 * @param feed_id - data feed ID
	 * @return true if need to subscribe, false otherwise
	 */
	public boolean haveToSubscribe(KeyType key, FeedType feed_id) {
		lock();
		try {
			return getOrCreate(key).markToSubscribe(feed_id);
		} finally {
			unlock();
		}
	}
	
	/**
	 * Mark that the system has to be not subscribed to specified data feed.
	 * <p>
	 * @param key - key
	 * @param feed_id - data feed ID
	 * @return true if need to unsubscribe, false otherwise
	 */
	public boolean haveToUnsubscribe(KeyType key, FeedType feed_id) {
		lock();
		try {
			return getOrCreate(key).markToUnsubscribe(feed_id);
		} finally {
			unlock();
		}
	}
	
	/**
	 * Check that data for specified key is not available.
	 * <p>
	 * @param key - the key
	 * @return true if data is not available. Note that false does not mean
	 * that data is available. It may be just not determined yet.
	 */
	public boolean isNotAvailable(KeyType key) {
		lock();
		try {
			DFGroup<KeyType, FeedType> group = groups.get(key);
			return group != null && group.isNotFound();
		} finally {
			unlock();
		}
	}
	
	/**
	 * Prepare all groups to cancel subscriptions for specified data feed completely.
	 * <p>
	 * In result of this call:
	 * <li>data feeds in {@link DFSubscrStatus#NOT_AVAILABLE}, {@link DFSubscrStatus#NOT_SUBSCR},
	 * {@link DFSubscrStatus#PENDING_UNSUBSCR} statuses aren't changed;</li>
	 * <li>data feeds in {@link DFSubscrStatus#PENDING_SUBSCR} status switched to
	 * {@link DFSubscrStatus#NOT_SUBSCR};</li>
	 * <li>data feeds in {@link DFSubscrStatus#SUBSCR} status switched to
	 * {@link DFSubscrStatus#PENDING_UNSUBSCR}</li>
	 * Thus, the next step is to cancel subscription of all which were actually established and
	 * switch them to {@link DFSubscrStatus#NOT_SUBSCR}.
	 * <p>
	 * @param feed_id - data feed ID
	 * @return collection of keys in {@link DFSubscrStatus#PENDING_UNSUBSCR} status
	 */
	public Collection<KeyType> haveToUnsubscribeAll(FeedType feed_id) {
		lock();
		try {
			List<KeyType> keys = new ArrayList<>();
			Iterator<Map.Entry<KeyType, DFGroup<KeyType, FeedType>>> it = groups.entrySet().iterator();
			while ( it.hasNext() ) {
				Map.Entry<KeyType, DFGroup<KeyType, FeedType>> entry = it.next();
				switch ( entry.getValue().getFeedStatus(feed_id) ) {
				case PENDING_SUBSCR:
					entry.getValue().setFeedStatus(feed_id, DFSubscrStatus.NOT_SUBSCR);
					break;
				case SUBSCR:
					entry.getValue().setFeedStatus(feed_id, DFSubscrStatus.PENDING_UNSUBSCR);
					keys.add(entry.getKey());
					break;
				case PENDING_UNSUBSCR:
					keys.add(entry.getKey());
				default:
				}
			}
			return keys;
		} finally {
			unlock();
		}
	}

}
