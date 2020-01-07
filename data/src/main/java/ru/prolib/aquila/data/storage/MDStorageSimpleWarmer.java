package ru.prolib.aquila.data.storage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;

public class MDStorageSimpleWarmer<KeyType, DataType> implements MDStorage<KeyType, DataType> {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MDStorageSimpleWarmer.class);
	}
	
	static class ID_CR_KIT<KeyType> {
		final KeyType key;
		final int count, hashCode;
		final Instant to;
		
		ID_CR_KIT(KeyType key, int count, Instant to) {
			this.key = key;
			this.count = count;
			this.to = to;
			this.hashCode = new HashCodeBuilder(21897, 513)
					.append(key)
					.append(count)
					.append(to)
					.build();
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != ID_CR_KIT.class ) {
				return false;
			}
			ID_CR_KIT<?> o = (ID_CR_KIT<?>) other;
			return new EqualsBuilder()
					.append(o.key, key)
					.append(o.count, count)
					.append(o.to, to)
					.build();
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
				.append("CR_KIT[").append(key).append("#").append(count).append("@").append(to).append("]").toString();
		}
		
	}
	
	static class WARMUP_ENTRY<DataType> {
		final CompletableFuture<List<DataType>> result;
		long time_updated;
		
		WARMUP_ENTRY(CompletableFuture<List<DataType>> result, long time_created) {
			this.result = result;
			this.time_updated = time_created;
		}
		
	}
	
	private final MDStorage<KeyType, DataType> basic;
	private final int entry_max_elements;
	private final long entry_max_ttl;
	private final Lock cr_kit_lock;
	private final Map<ID_CR_KIT<KeyType>, WARMUP_ENTRY<DataType>> cr_kit_entries;
	private CompletableFuture<Void> cr_kit_cleanup;
	private long cr_kit_time_current;
	
	public MDStorageSimpleWarmer(MDStorage<KeyType, DataType> basic_storage,
			int entry_max_elements,
			long entry_max_ttl,
			Lock cr_kit_lock, Map<ID_CR_KIT<KeyType>, WARMUP_ENTRY<DataType>> cr_kit_entries)
	{
		this.basic = basic_storage;
		this.entry_max_elements = entry_max_elements;
		this.entry_max_ttl = entry_max_ttl;
		this.cr_kit_lock = cr_kit_lock;
		this.cr_kit_entries = cr_kit_entries;
	}
	
	public MDStorageSimpleWarmer(MDStorage<KeyType, DataType> basic_storage,
			int entry_max_elements,
			long entry_max_ttl)
	{
		this(basic_storage, entry_max_elements, entry_max_ttl, new ReentrantLock(), new HashMap<>());
	}
	
	public MDStorageSimpleWarmer(MDStorage<KeyType, DataType> basic_storage) {
		this(basic_storage, 2048, 64);
	}
	
	public int getEntryMaxElements() {
		return entry_max_elements;
	}
	
	public long getEntryMaxTTL() {
		return entry_max_ttl;
	}
	
	CompletableFuture<Void> getCrKITCleanup() {
		cr_kit_lock.lock();
		try {
			return cr_kit_cleanup;
		} finally {
			cr_kit_lock.unlock();
		}
	}
	
	boolean isCrKITEntryExists(KeyType key, int count, Instant to) {
		cr_kit_lock.lock();
		try {
			return cr_kit_entries.containsKey(new ID_CR_KIT<>(key, count, to));
		} finally {
			cr_kit_lock.unlock();
		}
	}
	
	WARMUP_ENTRY<DataType> getCrKITEntry(KeyType key, int count, Instant to) {
		cr_kit_lock.lock();
		try {
			ID_CR_KIT<KeyType> id = new ID_CR_KIT<>(key, count, to);
			WARMUP_ENTRY<DataType> entry = cr_kit_entries.get(id);
			if ( entry == null ) {
				throw new IllegalStateException("Entry not found: " + id);
			}
			return entry;
		} finally {
			cr_kit_lock.unlock();
		}
	}
	
	@Override
	public void warmingUpReader(KeyType key, int count, Instant to) {
		if ( count > entry_max_elements ) {
			return;
		}
		ID_CR_KIT<KeyType> id = new ID_CR_KIT<>(key, count, to);
		cr_kit_lock.lock();
		try {
			WARMUP_ENTRY<DataType> entry = cr_kit_entries.get(id);
			if ( entry == null ) {
				entry = new WARMUP_ENTRY<>(
					CompletableFuture.supplyAsync(() -> {
						List<DataType> result = new ArrayList<>();
						try ( CloseableIterator<DataType> it = basic.createReader(key, count, to) ) {
							while ( it.next() ) {
								result.add(it.item());
							}
						} catch ( Exception e ) {
							throw new IllegalStateException(e);
						}
						return result;
					}), cr_kit_time_current);
				cr_kit_entries.put(id, entry);
			} else {
				entry.time_updated = cr_kit_time_current;
			}
		} finally {
			cr_kit_lock.unlock();
		}
	}

	@Override
	public CloseableIterator<DataType> createReader(KeyType key, int count, Instant to) throws DataStorageException {
		ID_CR_KIT<KeyType> id = new ID_CR_KIT<>(key, count, to);
		cr_kit_lock.lock();
		cr_kit_time_current ++;
		try {
			WARMUP_ENTRY<DataType> entry = cr_kit_entries.get(id);
			if ( entry != null ) {
				try {
					List<DataType> result = entry.result.get();
					if ( result.size() == count ) {
						entry.time_updated = cr_kit_time_current;
						return new CloseableIteratorStub<>(result);
					} else {
						// Number of elements loaded mismatch. Possible data loss.
						cr_kit_entries.remove(id);
					}					
				} catch ( ExecutionException e ) {
					cr_kit_entries.remove(id);
					logger.error("Unexpected exception: ", e);
				}
			}
		} catch ( InterruptedException e ) {
			// We don't know who can do this and what to do next.
			// Delegate the call to underlying storage to give it one more chance.
			logger.error("Unexpected exception: ", e);
		} finally {
			if ( cr_kit_cleanup == null || cr_kit_cleanup.isDone() ) {
				cr_kit_cleanup = CompletableFuture.runAsync(() -> {
					cr_kit_lock.lock();
					try {
						long min_time = cr_kit_time_current - entry_max_ttl;
						cr_kit_entries.entrySet().removeIf(x -> x.getValue().time_updated < min_time );
					} finally {
						cr_kit_lock.unlock();
					}
				});
			}
			cr_kit_lock.unlock();
		}
		return basic.createReader(key, count, to);
	}

	@Override
	public Set<KeyType> getKeys() throws DataStorageException {
		return basic.getKeys();
	}

	@Override
	public CloseableIterator<DataType> createReader(KeyType key) throws DataStorageException {
		return basic.createReader(key);
	}

	@Override
	public CloseableIterator<DataType> createReaderFrom(KeyType key, Instant from) throws DataStorageException {
		return basic.createReaderFrom(key, from);
	}

	@Override
	public CloseableIterator<DataType> createReader(KeyType key, Instant from, int count) throws DataStorageException {
		return basic.createReader(key, from, count);
	}

	@Override
	public CloseableIterator<DataType> createReader(KeyType key, Instant from, Instant to) throws DataStorageException {
		return basic.createReader(key, from, to);
	}

	@Override
	public CloseableIterator<DataType> createReaderTo(KeyType key, Instant to) throws DataStorageException {
		return basic.createReaderTo(key, to);
	}

}
