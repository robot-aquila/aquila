package ru.prolib.aquila.core.utils;

import java.util.LinkedList;

/**
 * Список фиксированной длины.
 * <p>
 * Автоматически удаляет более ранние элементы, при выходе размера за пределы
 * установленного значения. Удаление выполняется только при вызове метода
 * {@link #addLast}.
 * <p>
 * @param <T> тип значения
 */
public class FixedList<T> extends LinkedList<T> {
	private static final long serialVersionUID = 1L;
	private final int capacity;
	
	/**
	 * Создать список указанной длины.
	 * <p>
	 * @param capacity максимальная длина списка
	 */
	public FixedList(int capacity) {
		super();
		this.capacity = capacity;
	}
	
	/**
	 * Получить максимальный размер списка.
	 * <p>
	 * @return максимальный размер списка
	 */
	public int getMaximalCapacity() {
		return capacity;
	}

	/**
	 * Добавить значение в конец списка.
	 * При этом выполняется автоматическая проверка размера списка. При
	 * нарушении установленных границ, первый элемент автоматически удаляется.
	 * <p>
	 * @param e объект для добавления
	 */
	@Override
	public void addLast(T e) {
		super.addLast(e);
		if ( size() > capacity ) {
			super.removeFirst();
		}
	}

}
