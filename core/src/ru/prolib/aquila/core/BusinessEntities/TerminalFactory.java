package ru.prolib.aquila.core.BusinessEntities;

import java.util.Properties;

/**
 * Фабрика терминала.
 * <p>
 * 2013-01-31<br>
 * $Id: TerminalFactory.java 472 2013-02-01 11:21:06Z huan.kaktus $
 */
public interface TerminalFactory {
	
	/**
	 * Создать терминал.
	 * <p>
	 * @param props параметры конфигурации
	 * @return терминал
	 * @throws Exception ошибка инстанцирования
	 */
	public Terminal createTerminal(Properties props) throws Exception;
	
}
