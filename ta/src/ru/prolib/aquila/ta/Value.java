package ru.prolib.aquila.ta;

/**
 * Интерфейс контейнера значений параметра технического анализа (ТА).
 *
 * @param <T>
 */
public interface Value<T> {
	
	/**
	 * Получить строковый идентификатор значения.
	 * @return
	 */
	public String getId();
	
	/**
	 * Установить текущее значение.
	 * 
	 * @param value
	 * @throws ValueException нет значения для замены
	 * (ни одно значение не было добавлено ранее)
	 */
	public void set(T value) throws ValueException;
	
	/**
	 * Получить текущее значение.
	 */
	public T get() throws ValueException;
	
	/**
	 * Получить значение по индексу
	 * 
	 * @param index Индекс значения на шкале времени. 0 - самое первое значение.
	 * Отрицательные индексы используются для обращению к данным в прошлом
	 * относительно конца данных. -1 - предпоследнее значение, -2 - значение,
	 * которое было два момента назад и т. д. 
	 * @return
	 * @throws ValueException
	 */
	public T get(int index) throws ValueException;
	
	/**
	 * Добавить значение
	 * 
	 * @param value
	 * @throws ValueException
	 */
	public void add(T value) throws ValueException;
	
	/**
	 * Получить размер истории.
	 * Возвращает общее количество исторических значений.
	 * @return
	 */
	public int getLength();
	
	/**
	 * Обновить значение.
	 * @throws ValueUpdateException
	 */
	public void update() throws ValueUpdateException;

}
