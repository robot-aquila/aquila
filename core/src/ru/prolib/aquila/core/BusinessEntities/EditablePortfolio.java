package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс управляемого портфеля.
 * <p>
 * 2012-09-05<br>
 * $Id$
 */
public interface EditablePortfolio
	extends Portfolio, Editable, EditablePositions
{
	
	/**
	 * Установить размер вариационной маржи.
	 * <p>
	 * @param margin вариационная маржа
	 */
	public void setVariationMargin(Double margin);
	
	/**
	 * Установить объем доступных денежных средств.
	 * <p>
	 * @param cash объем денежных средств
	 */
	public void setCash(Double cash);
	
	/**
	 * Установить значение баланса портфеля.
	 * <p>
	 * @param value новое значение
	 */
	public void setBalance(Double value);

}
