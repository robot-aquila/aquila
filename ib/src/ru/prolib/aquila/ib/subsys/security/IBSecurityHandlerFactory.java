package ru.prolib.aquila.ib.subsys.security;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;

/**
 * Интерфейс фабрики поставщиков инструментов.
 * <p>
 * 2012-11-23<br>
 * $Id: IBSecurityHandlerFactory.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public interface IBSecurityHandlerFactory {
	
	/**
	 * Создать поставщик инструмента.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return поставщик инструмента
	 */
	public IBSecurityHandler createHandler(SecurityDescriptor descr);

}
