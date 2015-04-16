package ru.prolib.aquila.core.data.row;

/**
 * Интерфейс ряда.
 * <p>
 * Определяет абстракцию набора ряда элементов, доступ к которым осуществляется
 * по уникальному строковому идентификатору.
 * <p>
 * 2012-08-21<br>
 * $Id$
 */
public interface Row {

	/**
	 * Получить объект по идентификатору.
	 * <p>
	 * @param name идентификатор объкта
	 * @return объект
	 * @throws RowException - TODO:
	 */
	public Object get(String name) throws RowException;
	
	/**
	 * Получить копию ряда.
	 * <p>
	 * @return копия ряда
	 * @throws RowException - TODO:
	 */
	public Row getRowCopy() throws RowException;

}