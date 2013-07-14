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
	private final FirePanicEvent panic;
	private final Map<String, DDETableHandler> handlers;
	
	public QUIKDDEService(String name, FirePanicEvent panic) {
		super();
		this.name = name;
		this.panic = panic;
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
	 * Получить обработчик критического состояния.
	 * <p> 
	 * Только для тестов.
	 * <p>
	 * @return обработчик критического состояния
	 */
	FirePanicEvent getFirePanicEvent() {
		return panic;
	}
	
	/**
	 * Определить обработчик таблицы.
	 * <p>
	 * @param table имя (топик) таблицы
	 * @param handler обработчик таблицы
	 * @return ссылка на самого себя
	 */
	public QUIKDDEService setHandler(String table, DDETableHandler handler) {
		handlers.put(table, handler);
		return this;
	}

	@Override
	public boolean onConnect(String topic) {
		return handlers.containsKey(topic);
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
		DDETableHandler handler = handlers.get(table.getTopic());
		if ( handler != null ) {
			try {
				handler.handle(table);
			} catch ( DDEException e ) {
				panic.firePanicEvent(1, e.getMessage());
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
			.append(name, o.name)
			.append(panic, o.panic)
			.append(handlers, o.handlers)
			.isEquals();
	}

}
