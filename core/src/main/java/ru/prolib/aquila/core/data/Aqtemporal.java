package ru.prolib.aquila.core.data;

import org.joda.time.DateTime;

/**
 * Хранилище темпорального значения.
 * <p>
 * Данный интерфейс определяет способ доступа к переменной, значение которой
 * изменяется с течением времени (например, курс валюты на определенную дату).  
 * <p>
 * @param <T> - тип значения
 */
public interface Aqtemporal<T> {
	
	/**
	 * Освободить используемые ресурсы. 
	 */
	public void close();
	
	/**
	 * Получить значение соответствующее времени.
	 * <p>
	 * @param time момент времени
	 * @return значение, актуальное на указанное время.
	 * @throws DataException - If error occured.
	 */
	public T at(DateTime time) throws DataException;

}
