package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемой позиции.
 * <p>
 * Данный интерфейс предназначен для использования поставщиками сервисов
 * и определяет методы изменения позиции.
 * <p>
 * 2012-08-03<br>
 * $Id: EditablePosition.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public interface EditablePosition extends Position, Editable {
	
	/**
	 * Установить размер вариационной маржи.
	 * <p>
	 * @param margin вариационная маржа
	 */
	public void setVarMargin(double margin);
	
	/**
	 * Установить размер позиции на открытии сессии.
	 * <p>
	 * @param value размер позиции
	 */
	public void setOpenQty(long value);
	
	/**
	 * Установить размер позиции, заблокированный под текущие операции.
	 * <p>
	 * @param value размер позиции
	 */
	public void setLockQty(long value);
	
	/**
	 * Установить текущий размер позиции.
	 * <p>
	 * @param value размер позиции
	 */
	public void setCurrQty(long value);
	
	/**
	 * Установить рыночную стоимость позиции.
	 * <p>
	 * @param value рыночная стоимость позиции
	 */
	public void setMarketValue(Double value);
	
	/**
	 * Установить балансовую стоимость позиции.
	 * <p>
	 * @param value балансовая стоимость позиции
	 */
	public void setBookValue(Double value);
	
	/**
	 * Установить объект счета.
	 * <p>
	 * @param account объект счета
	 */
	public void setAccount(Account account);

}
