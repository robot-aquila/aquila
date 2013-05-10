package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

/**
 * Интерфейс фабрики торговых позиций.
 * <p>
 * Объекты данного типа используются в классе набора позиций для инстанцирования
 * новых экземпляров позиций.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionFactory.java 365 2012-12-24 06:58:03Z whirlwind $
 */
@Deprecated
public interface PositionFactory {
	
	/**
	 * Создать редактируемый экземпляр позиции.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return экземпляр позиции
	 */
	public EditablePosition createPosition(SecurityDescriptor descr);

}
