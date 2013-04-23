package ru.prolib.aquila.quik.dde;

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
 * <p>
 * Так как механизм кэширования в данной версии является прототипом, все вызовы
 * дублируются в базовый сервис, который представляет собой точку входа в
 * текущую реализацию механизма импорта данных по DDE. В последствии
 * делегирование базовому сервису будет удалено из реализации данного сервиса.
 */
public class CacheService implements DDEService {
	private final String name;
	private final FirePanicEvent panic;
	private final DDEService underlyingService;
	private final Map<String, DDETableHandler> handlers;
	
	public CacheService(String name, FirePanicEvent panic,
			DDEService underlyingService)
	{
		super();
		this.name = name;
		this.panic = panic;
		this.underlyingService = underlyingService;
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
	protected Map<String, DDETableHandler> getHandlers() {
		return handlers;
	}
	
	protected FirePanicEvent getFirePanicEvent() {
		return panic;
	}
	
	protected DDEService getUnderlyingService() {
		return underlyingService;
	}
	
	/**
	 * Определить обработчик таблицы.
	 * <p>
	 * @param tableName имя (топик) таблицы
	 * @param handler обработчик таблицы
	 * @return ссылка на самого себя
	 */
	public CacheService setHandler(String tableName, DDETableHandler handler) {
		handlers.put(tableName, handler);
		return this;
	}

	@Override
	public boolean onConnect(String topic) {
		return underlyingService.onConnect(topic);
	}

	@Override
	public void onConnectConfirm(String topic) {
		underlyingService.onConnectConfirm(topic);
	}

	@Override
	public boolean onData(String topic, String item, byte[] dataBuffer) {
		underlyingService.onData(topic, item, dataBuffer);
		return false;
	}

	@Override
	public void onDisconnect(String topic) {
		underlyingService.onDisconnect(topic);
	}

	@Override
	public void onRegister() {
		underlyingService.onRegister();
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
		underlyingService.onTable(table);
	}

	@Override
	public void onUnregister() {
		underlyingService.onUnregister();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( other.getClass() != CacheService.class ) {
			return false;
		}
		CacheService o = (CacheService) other;
		return new EqualsBuilder()
			.append(name, o.name)
			.append(panic, o.panic)
			.append(underlyingService, o.underlyingService)
			.append(handlers, o.handlers)
			.isEquals();
	}

}
