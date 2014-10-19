package ru.prolib.aquila.core.data;

/**
 * Итератор.
 * <p>
 * @param <T> - тип элементов
 */
public interface CloseableIterator<T> {
	
	/**
	 * Освободить используемые ресурсы. 
	 */
	public void close();
	
	/**
	 * Получить элемент под курсором.
	 * <p>
	 * @return текущий элемент
	 * @throws NoSuchElementException нет элемента под курсором
	 * @throws DataException
	 */
	public T current() throws DataException;
	
	/**
	 * Переместиться на следующий элемент.
	 * <p>
	 * @return true - доступен очередной элемент, false - конец данных
	 * @throws DataException
	 */
	public boolean next() throws DataException;

}
