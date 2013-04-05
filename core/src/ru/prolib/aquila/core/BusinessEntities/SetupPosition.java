package ru.prolib.aquila.core.BusinessEntities;


/**
 * Интерфейс спецификации позиции.
 * <p>
 * 2012-12-26<br>
 * $Id: SetupPosition.java 411 2013-01-12 10:55:36Z whirlwind $
 */
public interface SetupPosition extends Cloneable {
	
	/**
	 * Получить дескриптор инструмента спецификации позиции.
	 * <p>
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getSecurityDescriptor();
	
	/**
	 * Получить долю элемента в портфеле.
	 * <p>
	 * @return доля
	 */
	public Price getQuota();
	
	/**
	 * Установить долю элемента в портфеле.
	 * <p>
	 * @param value доля
	 */
	public void setQuota(Price value);
	
	/**
	 * Получить тип позиции.
	 * <p>
	 * @return тип позиции
	 */
	public PositionType getType();
	
	/**
	 * Установить тип позиции.
	 * <p>
	 * @param value тип позиции
	 */
	public void setType(PositionType value);
	
	/**
	 * Создать копию объекта.
	 * <p>
	 * @return копия объекта
	 */
	public SetupPosition clone();

}