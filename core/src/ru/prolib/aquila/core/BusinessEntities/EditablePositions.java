package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого набора позиций.
 * <p>
 * 2012-08-04<br>
 * $Id: EditablePositions.java 365 2012-12-24 06:58:03Z whirlwind $
 */
public interface EditablePositions extends Positions {
	
	/**
	 * Генерировать событие о появлении информации о новой позиции.
	 * <p>
	 * @param position инициализированная позиция
	 */
	public void firePositionAvailableEvent(Position position);
	
	/**
	 * Получить экземпляр редактируемой позиции.
	 * <p>
	 * @param descr идентификатор инструмента
	 * @return позиция
	 */
	public EditablePosition getEditablePosition(SecurityDescriptor descr);

}
