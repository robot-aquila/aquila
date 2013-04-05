package ru.prolib.aquila.core.BusinessEntities;


/**
 * Интерфейс генератора события, связанного с модифицируемым объектом.
 * <p>
 * 2012-11-29<br>
 * $Id: FireEvent.java 327 2012-12-05 19:58:26Z whirlwind $
 */
public interface FireEditableEvent {
	
	/**
	 * Генерировать событие.
	 * <p>
	 * @param object объект, с которым связано событие
	 */
	public void fireEvent(Editable object);

}
