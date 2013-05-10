package ru.prolib.aquila.core.BusinessEntities.utils;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Интерфейс фабрики инструментов биржевой торговли.
 * <p>
 * Объекты данного типа используются в классе набора инструментов для
 * создания новых экземпляров инструментов.
 * <p>
 * 2012-07-05<br>
 * $Id: SecurityFactory.java 254 2012-08-14 08:14:35Z whirlwind $
 */
@Deprecated
public interface SecurityFactory {
	
	/**
	 * Создать редактируемый экземпляр инструмента биржевой торговли.
	 * <p>
	 * @param termina экземпляр терминала
	 * @param descr дескриптор инструмента
	 * @return экземпляр инструмента
	 */
	public EditableSecurity createSecurity(EditableTerminal terminal,
			SecurityDescriptor descr);

}
