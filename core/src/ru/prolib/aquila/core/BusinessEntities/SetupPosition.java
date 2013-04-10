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
	 * @throws IllegalArgumentException указан тип {@link PositionType#BOTH}
	 */
	public void setType(PositionType value);
	
	/**
	 * Создать копию объекта.
	 * <p>
	 * @return копия объекта
	 */
	public SetupPosition clone();
	
	/**
	 * Установить разрешенный тип позиции.
	 * <p>
	 * Подразумевается:<p>
	 * {@link PositionType#CLOSE} - запретить открытие позиций<br>
	 * {@link PositionType#LONG} - разрешить только длиные позиции<br>
	 * {@link PositionType#SHORT} - разрешить только короткие позиции<br>
	 * {@link PositionType#BOTH} - разрешить открытие позиций либого типа<br>
	 * <p>
	 * @param value разрешенный тип
	 */
	public void setAllowedType(PositionType value);
	
	/**
	 * Получить разрешенный тип позиции.
	 * <p>
	 * см. {@link SetupPosition#setAllowedType(PositionType)}.
	 * <p>
	 * @return разрешенный тип позиции
	 */
	public PositionType getAllowedType();
	
	/**
	 * Проверить разрешение на открытие позиции указанного типа.
	 * <p>
	 * Для проверки используется настройка разрешенного типа позиции.
	 * <p>
	 * @param type тип для проверки {@link PositionType#LONG} или
	 * {@link PositionType#SHORT}. Ответ на прочие значения всегда false.
	 * @return true - разрешено открыть позицию данного типа, false - запрещено
	 */
	public boolean isOpenAllowed(PositionType type);
	
	/**
	 * Проверить необходимость закрытия текущей позиции.
	 * <p>
	 * Проверяет нужно-ли закрыть текущую позицию в соответствии с настройками.
	 * Учитывается целевой тип позиции и установленный разрешенный тип.
	 * Когда текущий тип {@link PositionType#SHORT}, целевой тип
	 * {@link PositionType#CLOSE} или {@link PositionType#LONG}, но при этом
	 * разрешенный тип {@link PositionType#CLOSE} или только
	 * {@link PositionType#SHORT}, то позицию следует закрыть. Для текущего
	 * {@link PositionType#LONG} позиция закрывается, если целевой тип
	 * {@link PositionType#CLOSE} или {@link PositionType#SHORT}, но при этом
	 * разрешенный тип {@link PositionType#CLOSE} или только
	 * {@link PositionType#LONG}. В остальных случаях закрывать позицию не
	 * следует.
	 * <p>
	 * @param current тип текущей позиции
	 * @return true - следует закрыть позицию, false - ничего делать не нужно
	 */
	public boolean isShouldClose(PositionType current);

}