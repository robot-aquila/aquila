package ru.prolib.aquila.quik.dde;

import java.util.Date;

/**
 * Базовый класс кэш-записи.
 */
abstract public class CacheEntry {
	private final Date entryTime;
	
	/**
	 * Конструктор.
	 */
	public CacheEntry() {
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
