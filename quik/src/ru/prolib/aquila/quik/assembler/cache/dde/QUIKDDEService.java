package ru.prolib.aquila.quik.assembler.cache.dde;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;

/**
 * Диспетчер импортируемых по DDE-таблиц.
 * <p>
 * Обеспечивает диспетчеризацию между соответствующими обработчиками таблиц.
 * В случае возникновения исключения на уровне обработчика таблицы, инициирует
 * генерацию события о паническом состоянии с полученным сообщением исключения.
 */
public class QUIKDDEService implements DDEService {
	private final String name;
	private final EditableTerminal terminal;
	private final Map<String, DDETableHandler> handlers;
	
	public QUIKDDEService(String name, EditableTerminal terminal) {
		super();
		this.name = name;
		this.terminal = terminal;
		handlers = new Hashtable<String, DDETableHandler>();
	}

	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Получить текущую карту обработчиков.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @return карта обработчиков
	 */
	Map<String, DDETableHandler> getHandlers() {
		return handlers;
	}
	
	/**
	 * Получить терминал.
	 * <p> 
	 * Только для тестов.
	 * <p>
	 * @return терминал
	 */
	EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Определить обработчик таблицы.
	 * <p>
	 * @param table имя (топик) таблицы
	 * @param handler обработчик таблицы
	 * @return ссылка на самого себя
	 */
	public synchronized
		QUIKDDEService setHandler(String table, DDETableHandler handler)
	{
		handlers.put(table, handler);
		return this;
	}

	@Override
	public boolean onConnect(String topic) {
		return getHandler(topic) != null;
	}

	@Override
	public void onConnectConfirm(String topic) {

	}

	@Override
	public boolean onData(String topic, String item, byte[] dataBuffer) {
		return false;
	}

	@Override
	public void onDisconnect(String topic) {

	}

	@Override
	public void onRegister() {
		
	}

	@Override
	public void onTable(DDETable table) {
		DDETableHandler handler = getHandler(table.getTopic());
		if ( handler != null ) {
			try {
				handler.handle(table);
			} catch ( DDEException e ) {
				terminal.firePanicEvent(1, e.getMessage());
				return;
			}
		}
	}

	@Override
	public void onUnregister() {

	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QUIKDDEService.class ) {
			return false;
		}
		QUIKDDEService o = (QUIKDDEService) other;
		return new EqualsBuilder()
			.appendSuper(terminal == o.terminal)
			.append(name, o.name)
			.append(handlers, o.handlers)
			.isEquals();
	}
	
	/**
	 * Удалить все обработчики.
	 */
	public synchronized void clearHandlers() {
		handlers.clear();
	}
	
	synchronized DDETableHandler getHandler(String name) {
		return handlers.get(name);
	}

}
