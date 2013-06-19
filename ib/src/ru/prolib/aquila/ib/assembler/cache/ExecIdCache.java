package ru.prolib.aquila.ib.assembler.cache;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.core.utils.SimpleCounter;

/**
 * Кэш идентификаторов сделок.
 * <p>
 * Номера сделок IB представлены строковыми значениями, а локально требуются
 * целочисленные. Данный объект сопоставляет строковым идентификаторам
 * целочисленные.
 */
public class ExecIdCache {
	private final Counter sequence;
	private final Map<String, Long> cache;
	
	public ExecIdCache() {
		this(new SimpleCounter());
	}
	
	public ExecIdCache(Counter sequence) {
		super();
		this.sequence = sequence;
		this.cache = new Hashtable<String, Long>();
	}
	
	/**
	 * Получить целочисленный идентификатор сделки.
	 * <p>
	 * Формирует в случае необходимости и возвращает целочисленный идентификатор
	 * соответствующий идентификатору сделки IB.
	 * <p>
	 * @param execId идентификатор сделки IB
	 * @return локальный идентификатор сделки
	 */
	public synchronized long getId(String execId) {
		Long id = cache.get(execId);
		if ( id == null ) {
			id = new Long(sequence.incrementAndGet());
			cache.put(execId, id);
		}
		return id;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ExecIdCache.class ) {
			return false;
		}
		ExecIdCache o = (ExecIdCache) other;
		return new EqualsBuilder()
			.append(o.cache, cache)
			.append(o.sequence, sequence)
			.isEquals();
	}

}
