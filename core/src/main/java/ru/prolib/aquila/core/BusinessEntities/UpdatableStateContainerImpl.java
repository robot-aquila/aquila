package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.prolib.aquila.core.concurrency.LID;

/**
 * Updatable state container implementation.
 */
public class UpdatableStateContainerImpl implements UpdatableStateContainer {
	private final LID lid;
	protected final Lock lock;
	private final Map<Integer, Object> values;
	private final Set<Integer> updated;
	private final String id;
	private boolean closed = false;
	
	public UpdatableStateContainerImpl(String id, Lock lock) {
		this.id = id;
		this.lock = lock;
		this.lid = LID.createInstance();
		this.values = new HashMap<Integer, Object>();
		this.updated = new HashSet<Integer>();
	}

	public UpdatableStateContainerImpl(String id) {
		this(id, new ReentrantLock());
	}

	@Override
	public String getContainerID() {
		return id;
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}

	@Override
	public void close() {
		lock.lock();
		try {
			closed = true;
			values.clear();
			updated.clear();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getString(int token) {
		return (String) getObject(token);
	}
	
	@Override
	public String getString(int token, String defaultValue) {
		String x = getString(token);
		return x == null ? defaultValue : x;
	}

	@Override
	public Integer getInteger(int token) {
		return (Integer) getObject(token);
	}
	
	@Override
	public Integer getInteger(int token, Integer defaultValue) {
		Integer x = getInteger(token);
		return x == null ? defaultValue : x;
	}
	
	@Override
	public Integer getIntegerOrZero(int token) {
		return getInteger(token, 0);
	}

	@Override
	public Long getLong(int token) {
		return (Long) getObject(token);
	}
	
	@Override
	public Long getLong(int token, Long defaultValue) {
		Long x = getLong(token);
		return x == null ? defaultValue : x;
	}
	
	@Override
	public Long getLongOrZero(int token) {
		return getLong(token, 0L);
	}

	@Override
	public Double getDouble(int token) {
		return (Double) getObject(token);
	}
	
	@Override
	public Double getDouble(int token, Double defaultValue) {
		Double x = getDouble(token);
		return x == null ? defaultValue : x;
	}
	
	@Override
	public Double getDoubleOrZero(int token) {
		return getDouble(token, 0.0d);
	}

	@Override
	public Boolean getBoolean(int token) {
		return (Boolean) getObject(token);
	}
	
	@Override
	public Boolean getBoolean(int token, Boolean defaultValue) {
		Boolean x = getBoolean(token);
		return x == null ? defaultValue : x;
	}

	@Override
	public Instant getInstant(int token) {
		return (Instant) getObject(token);
	}
	
	@Override
	public Instant getInstant(int token, Instant defaultValue) {
		Instant x = getInstant(token);
		return x == null ? defaultValue : x;
	}

	@Override
	public Object getObject(int token) {
		lock.lock();
		try {
			return values.get(token);
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public Object getObject(int token, Object defaultValue) {
		Object x = getObject(token);
		return x == null ? defaultValue : x;
	}

	@Override
	public FDecimal getDecimal(int token) {
		return (FDecimal) getObject(token);
	}
	
	@Override
	public FDecimal getDecimal(int token, FDecimal defaultValue) {
		FDecimal x = getDecimal(token);
		return x == null ? defaultValue : x;
	}
	
	@Override
	public FDecimal getDecimalOrZero(int token, int scale) {
		return getDecimal(token, FDecimal.of(0, scale));
	}
	
	@Override
	public FDecimal getDecimalOrZero0(int token) {
		return getDecimalOrZero(token, 0);
	}

	@Override
	public FDecimal getDecimalOrZero1(int token) {
		return getDecimalOrZero(token, 1);
	}

	@Override
	public FDecimal getDecimalOrZero2(int token) {
		return getDecimalOrZero(token, 2);
	}

	@Override
	public FDecimal getDecimalOrZero3(int token) {
		return getDecimalOrZero(token, 3);
	}

	@Override
	public FDecimal getDecimalOrZero4(int token) {
		return getDecimalOrZero(token, 4);
	}

	@Override
	public FMoney getMoney(int token) {
		return (FMoney) getObject(token);
	}
	
	@Override
	public FMoney getMoney(int token, FMoney defaultValue) {
		FMoney x = getMoney(token);
		return x == null ? defaultValue : x;
	}
	
	@Override
	public FMoney getMoneyOrZero(int token, int scale, String currencyCode) {
		return getMoney(token, FMoney.of(0, scale, currencyCode));
	}
	
	@Override
	public FMoney getMoneyOrZero0(int token, String currencyCode) {
		return getMoneyOrZero(token, 0, currencyCode);
	}

	@Override
	public FMoney getMoneyOrZero1(int token, String currencyCode) {
		return getMoneyOrZero(token, 1, currencyCode);
	}

	@Override
	public FMoney getMoneyOrZero2(int token, String currencyCode) {
		return getMoneyOrZero(token, 2, currencyCode);
	}

	@Override
	public FMoney getMoneyOrZero3(int token, String currencyCode) {
		return getMoneyOrZero(token, 3, currencyCode);
	}

	@Override
	public FMoney getMoneyOrZero4(int token, String currencyCode) {
		return getMoneyOrZero(token, 4, currencyCode);
	}

	@Override
	public boolean isDefined(int[] tokens) {
		lock.lock();
		try {
			for ( int token : tokens ) {
				if ( values.get(token) == null ) {
					return false;
				}
			}
			return true;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isDefined(int token) {
		lock.lock();
		try {
			return values.get(token) != null;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<Integer> getUpdatedTokens() {
		lock.lock();
		try {
			return new HashSet<Integer>(updated);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean hasChanged(int token) {
		lock.lock();
		try {
			return updated.contains(token);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean hasChanged() {
		lock.lock();
		try {
			return updated.size() > 0;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean atLeastOneHasChanged(int[] tokens) {
		lock.lock();
		try {
			for ( int token : tokens ) {
				if ( updated.contains(token) ) {
					return true;
				}
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void update(Map<Integer, Object> tokens) {
		lock.lock();
		try {
			if ( closed ) {
				throw new IllegalStateException("Container is closed: " + id);
			}
			updated.clear();
			for ( Map.Entry<Integer, Object> entry : tokens.entrySet() ) {
				Integer key = entry.getKey();
				Object value = entry.getValue();
				Object current = values.get(key);
				if ( current == null ) {
					if ( value != null ) {
						updated.add(key);
						values.put(key, value);
					}
				} else {
					if ( ! current.equals(value) ) {
						updated.add(key);
						values.put(key, value);			
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void update(int token, Object value) {
		Map<Integer, Object> tokens = new HashMap<>();
		tokens.put(token, value);
		update(tokens);
	}

	@Override
	public void resetChanges() {
		lock.lock();
		try {
			updated.clear();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isClosed() {
		lock.lock();
		try {
			return closed;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<Integer, Object> getContents() {
		lock.lock();
		try {
			return new HashMap<>(values);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Map<Integer, Object> getUpdatedContents() {
		lock.lock();
		try {
			Map<Integer, Object> tokens = new HashMap<>();
			for ( Integer key : updated ) {
				tokens.put(key, values.get(key));
			}
			return tokens;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public LID getLID() {
		return lid;
	}

	@Override
	public boolean hasData() {
		lock.lock();
		try {
			return ! values.isEmpty();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void consume(DeltaUpdate update) {
		update(update.getContents());
	}

}