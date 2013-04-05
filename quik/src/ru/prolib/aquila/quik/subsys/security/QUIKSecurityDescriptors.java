package ru.prolib.aquila.quik.subsys.security;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Интерфейс хранилища дескрипторов инструментов.
 * <p>
 * 2013-01-23<br>
 * $Id: QUIKSecurityDescriptors.java 520 2013-02-12 10:12:53Z whirlwind $
 */
public interface QUIKSecurityDescriptors {
	
	/**
	 * Зарегистрировать дескриптор.
	 * <p>
	 * Регистрирует дескриптор с указанными параметрами. Наименование
	 * инструмента используется для обратного разрешения дескриптора.
	 * <p>
	 * @param descr дескриптор
	 * @param name наименование инструмента
	 * @throws SecurityAlreadyExistsException
	 */
	public void register(SecurityDescriptor descr, String name);
	
	/**
	 * Получить дескриптор по наименованию инструмента.
	 * <p>
	 * @param name наименование инструмента
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getByName(String name);
	
	/**
	 * Получить дескриптор по кодам инструмента и класса.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getByCodeAndClass(String code, String classCode);

}
