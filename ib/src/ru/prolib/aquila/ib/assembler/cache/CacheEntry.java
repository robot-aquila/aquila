package ru.prolib.aquila.ib.assembler.cache;

import java.util.Date;

/**
 * Базовый класс кэш-записи.
 */
abstract class CacheEntry {
	private final Date entryTime;
	
	/**
	 * Конструктор.
	 */
	CacheEntry() {
		super();
		entryTime = new Date();
	}
	
	/**
	 * Получить время создания кэш-записи.
	 * <p>
	 * @return время создания кэш-записи
	 */
	public Date getEntryTime() {
		return entryTime;
	}

}
