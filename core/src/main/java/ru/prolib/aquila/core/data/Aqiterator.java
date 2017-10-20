package ru.prolib.aquila.core.data;

/**
 * Итератор.
 * <p>
 * @param <T> - тип элементов
 */
public interface Aqiterator<T> {
	
	/**
	 * Освободить используемые ресурсы. 
	 */
	public void close();
	
	/**
	 * Получить элемент под курсором.
	 * <p>
	 * @return текущий элемент
	 * @throws DataException - If error occured.
	 */
	public T item() throws DataException;
	
	/**
	 * Переместиться на следующий элемент.
	 * <p>
	 * @return true - доступен очередной элемент, false - конец данных
	 * @throws DataException - If error occured.
	 */
	public boolean next() throws DataException;

}
