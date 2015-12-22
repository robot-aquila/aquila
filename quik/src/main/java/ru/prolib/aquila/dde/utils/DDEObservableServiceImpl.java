package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.dde.DDEService;
import ru.prolib.aquila.dde.DDETable;

/**
 * Реализация обозреваемого сервиса DDE.
 * <p>
 * 2012-07-29<br>
 * $Id: DDEObservableServiceImpl.java 304 2012-11-06 09:17:07Z whirlwind $
 */
public class DDEObservableServiceImpl
	implements DDEObservableService, DDEService
{
	private final String name;
	private final DDEAccessControl access;
	private final EventDispatcher dispatcher;
	private final EventType etConnect,etDisconnect;
	private final EventType etRegister,etUnregister;
	private final EventType etData,etTable;
	
	/**
	 * Создать экземпляр обозреваемого DDE-сервиса.
	 * <p>
	 * Создает сервис с указанным именем. В качестве контроллера доступа
	 * использует новый экземпляр {@link DDEAllowAllAccess}. 
	 * <p>
	 * @param name имя сервиса
	 * @param eventSystem фасад системы событий
	 * @return экземпляр сервиса
	 */
	public static DDEObservableServiceImpl createService(String name,
			EventSystem eventSystem)
	{
		return createService(name, new DDEAllowAllAccess(), eventSystem);
	}
	
	/**
	 * Создать экземпляр обозреваемого DDE-сервиса.
	 * <p>
	 * Создает сервис с указанным именем и контроллером доступа.
	 * <p>
	 * @param name имя сервиса
	 * @param access контроллер доступа
	 * @param eventSystem фасад системы событий
	 * @return экземпляр сервиса
	 */
	public static DDEObservableServiceImpl createService(String name,
			DDEAccessControl access, EventSystem eventSystem)
	{
		EventDispatcher dispatcher = eventSystem.createEventDispatcher();
		return new DDEObservableServiceImpl(name, access, dispatcher,
				dispatcher.createType(),
				dispatcher.createType(),
				dispatcher.createType(),
				dispatcher.createType(),
				dispatcher.createType(),
				dispatcher.createType());
	}
	
	/**
	 * Создать сервис.
	 * <p>
	 * @param name имя DDE-сервиса
	 * @param access контроллер доступа
	 * @param dispatcher диспетчер событий
	 * @param onConnect тип события при новом клиентском подключении
	 * @param onDisconnect тип события при отключении клиента
	 * @param onRegister тип события при регистрации сервиса
	 * @param onUnregister тип события при удалении сервиса
	 * @param onData тип события при поступлении данных
	 * @param onTable тип события при поступлении таблицы
	 */
	public DDEObservableServiceImpl(String name, DDEAccessControl access,
			EventDispatcher dispatcher,
			EventType onConnect, EventType onDisconnect,
			EventType onRegister, EventType onUnregister,
			EventType onData, EventType onTable)
	{
		super();
		if ( name == null ) {
			throw new NullPointerException("Name cannot be null");
		}
		if ( access == null ) {
			throw new NullPointerException("Access controller cannot be null");
		}
		if ( dispatcher == null ) {
			throw new NullPointerException("Dispatcher cannot be null");
		}
		if ( onConnect == null ) {
			throw new NullPointerException("onConnect type cannot be null");
		}
		if ( onDisconnect == null ) {
			throw new NullPointerException("onDisconnect type cannot be null");
		}
		if ( onRegister == null ) {
			throw new NullPointerException("onRegister type cannot be null");
		}
		if ( onUnregister == null ) {
			throw new NullPointerException("onUnregister type cannot be null");
		}
		if ( onData == null ) {
			throw new NullPointerException("onData type cannot be null");
		}
		if ( onTable == null ) {
			throw new NullPointerException("onTable type cannot be null");
		}
		this.name = name;
		this.access = access;
		this.dispatcher = dispatcher;
		this.etConnect = onConnect;
		this.etDisconnect = onDisconnect;
		this.etRegister = onRegister;
		this.etUnregister = onUnregister;
		this.etData = onData;
		this.etTable = onTable;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean onConnect(String topic) {
		return access.isAllowed(new DDEAccessSubject(getName(), topic));
	}

	@Override
	public void onConnectConfirm(String topic) {
		dispatcher.dispatch(new DDETopicEvent(etConnect, getName(), topic));
	}

	@Override
	public boolean onData(String topic, String item, byte[] dataBuffer) {
		dispatcher.dispatch(new DDEDataEvent(etData, getName(),
							topic, item, dataBuffer));
		return false;
	}

	@Override
	public void onDisconnect(String topic) {
		dispatcher.dispatch(new DDETopicEvent(etDisconnect, getName(),topic));
	}

	@Override
	public void onRegister() {
		dispatcher.dispatch(new DDEEvent(etRegister, getName()));
	}

	@Override
	public void onTable(DDETable table) {
		dispatcher.dispatch(new DDETableEvent(etTable, getName(), table));
	}

	@Override
	public void onUnregister() {
		dispatcher.dispatch(new DDEEvent(etUnregister, getName()));
	}

	@Override
	public EventType OnConnect() {
		return etConnect;
	}

	@Override
	public EventType OnDisconnect() {
		return etDisconnect;
	}

	@Override
	public EventType OnRegister() {
		return etRegister;
	}

	@Override
	public EventType OnUnregister() {
		return etUnregister;
	}

	@Override
	public EventType OnData() {
		return etData;
	}

	@Override
	public EventType OnTable() {
		return etTable;
	}

	/**
	 * Получить используемый контроллер доступа.
	 * <p>
	 * @return контроллер доступа
	 */
	public DDEAccessControl getAccessControl() {
		return access;
	}

	/**
	 * Получить используемый диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, /*0*/65919)
			.append(name)
			.append(access)
			.append(dispatcher)
			.append(etConnect)
			.append(etDisconnect)
			.append(etRegister)
			.append(etUnregister)
			.append(etData)
			.append(etTable)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDEObservableServiceImpl ) {
			DDEObservableServiceImpl o = (DDEObservableServiceImpl) other;
			return new EqualsBuilder()
				.append(name, o.name)
				.append(access, o.access)
				.append(dispatcher, o.dispatcher)
				.append(etConnect, o.etConnect)
				.append(etDisconnect, o.etDisconnect)
				.append(etRegister, o.etRegister)
				.append(etUnregister, o.etUnregister)
				.append(etData, o.etData)
				.append(etTable, o.etTable)
				.isEquals();
		} else {
			return false;
		}
	}

}
