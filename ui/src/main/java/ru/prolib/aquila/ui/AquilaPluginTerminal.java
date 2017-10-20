package ru.prolib.aquila.ui;

import ru.prolib.aquila.core.BusinessEntities.TerminalFactory;

/**
 * Интерфейс плагина терминала.
 * <p>
 * Отличается от обычного плагина только наличием фабричного метода, отвечающего
 * за инстанцирование терминала.
 * <p>
 * 2013-02-28<br>
 * $Id: AquilaPluginTerminal.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public interface AquilaPluginTerminal extends AquilaPlugin, TerminalFactory {

}
