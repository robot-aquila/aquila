package ru.prolib.aquila.core.data;

/**
 * Интерфейс сканера по основанию.
 * <p>
 * Данный итнтерфейс описывает сканер, основанием работы которого и результатом
 * являются объекты одного типа. Например, файлы, даты, строки и т.п. 
 * <p>
 * @param <T> - тип значений
 */
public interface SubScanner<T> {
	
	/**
	 * Выполнить сканирование.
	 * <p> 
	 * @param basis значение-основание
	 * @return результат сканирования
	 * @throws DataException TODO
	 */
	public Aqiterator<T> makeScan(T basis) throws DataException;

}
