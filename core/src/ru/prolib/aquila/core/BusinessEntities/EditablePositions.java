package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого набора позиций.
 * <p>
 * 2012-08-04<br>
 * $Id: EditablePositions.java 365 2012-12-24 06:58:03Z whirlwind $
 */
public interface EditablePositions extends Positions {
	
	/**
	 * Генерировать события позиции.
	 * <p>
	 * @param position позиция
	 */
	public void fireEvents(EditablePosition position);
	
	/**
	 * Получить экземпляр редактируемой позиции.
	 * <p>
	 * @param security инструмент
	 * @return позиция
	 */
	public EditablePosition getEditablePosition(Security security);

}
