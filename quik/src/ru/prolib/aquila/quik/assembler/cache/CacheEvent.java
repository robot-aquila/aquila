package ru.prolib.aquila.quik.assembler.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;

/**
 * Событие в связи с обновлением кэша данных.
 */
public class CacheEvent extends EventImpl {
	private final boolean isDataAdded;

	/**
	 * Конструктор.
	 * <p>
	 * @param type тип события
	 * @param isDataAdded признак добавления в кэш новых данных
	 */
	public CacheEvent(EventType type, boolean isDataAdded) {
		super(type);
		this.isDataAdded = isDataAdded;
	}
	
	/**
	 * Проверить признак добавления данных.
	 * <p>
	 * @return true - доступны новые данные
	 */
	public boolean isDataAdded() {
		return isDataAdded;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CacheEvent.class ) {
			return false;
		}
		CacheEvent o = (CacheEvent) other;
		return new EqualsBuilder()
			.append(o.getType(), getType())
			.append(o.isDataAdded, isDataAdded)
			.isEquals();
	}

}
