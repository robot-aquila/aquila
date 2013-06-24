package ru.prolib.aquila.ib;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.ib.api.ContractHandler;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.IBRequestSecurityHandler;
import ru.prolib.aquila.ib.assembler.cache.Cache;

/**
 * Реализация терминала Interactive Brokers.
 */
public class IBTerminalImpl extends TerminalImpl implements IBEditableTerminal {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IBTerminalImpl.class);
	}
	
	private final EventType onSecurityRequestError;
	private final Cache cache;
	private final IBClient client;
	
	/**
	 * Конструктор (полный).
	 * <p>
	 * Позволяет определять экземпляры всех используемых классов объектов. 
	 * <p>
	 * @param eventSystem фасад системы событий
	 * @param timer шедулер
	 * @param starter стартер
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param stopOrders набор стоп-заявок
	 * @param controller контроллер запуска/останова терминала
	 * @param dispatcher диспетчер событий
	 * @param onConnected тип события: при подключении
	 * @param onDisconnected тип события: при отключении
	 * @param onStarted тип события: при старте терминала
	 * @param onStopped тип события: при останове терминала
	 * @param onPanic тип критического события
	 * @param onSecurityRequestError тип события: ошибка запроса инструмента
	 * @param cache кэш данных IB
	 * @param client экземпляр подключения к IB API
	 */
	public IBTerminalImpl(EventSystem eventSystem, Timer timer,
			Starter starter, EditableSecurities securities,
			EditablePortfolios portfolios, EditableOrders orders,
			EditableOrders stopOrders, TerminalController controller,
			EventDispatcher dispatcher, EventType onConnected,
			EventType onDisconnected, EventType onStarted, EventType onStopped,
			EventType onPanic, EventType onSecurityRequestError, Cache cache,
			IBClient client)
	{
		super(eventSystem, timer, starter, securities, portfolios, orders,
				stopOrders, controller, dispatcher, onConnected, onDisconnected,
				onStarted, onStopped, onPanic);
		this.onSecurityRequestError = onSecurityRequestError;
		this.cache = cache;
		this.client = client;
	}
	
	/**
	 * Конструктор (короткий).
	 * <p>
	 * Создает объекты некоторых используемых классов автоматически. Использует
	 * конструктор базового терминала с короткой сигнатурой, который создает
	 * контроллер запуска/останова терминала и шедулер.
	 * <p>
	 * @param eventSystem фасад системы событий 
	 * @param starter стартер
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param stopOrders набор стоп-заявок
	 * @param dispatcher диспетчер событий
	 * @param onConnected тип события: при подключении
	 * @param onDisconnected тип события: при отключении
	 * @param onStarted тип события: при старте терминала
	 * @param onStopped тип события: при останове терминала
	 * @param onPanic тип критического события
	 * @param onSecurityRequestError тип события: ошибка запроса инструмента
	 * @param cache кэш данных IB
	 * @param client экземпляр подключения к IB API
	 */
	public IBTerminalImpl(EventSystem eventSystem, Starter starter,
		EditableSecurities securities, EditablePortfolios portfolios,
		EditableOrders orders, EditableOrders stopOrders,
		EventDispatcher dispatcher, EventType onConnected,
		EventType onDisconnected, EventType onStarted,
		EventType onStopped, EventType onPanic,
		EventType onSecurityRequestError, Cache cache, IBClient client)
	{
		super(eventSystem, starter, securities, portfolios, orders, stopOrders,
				dispatcher, onConnected, onDisconnected, onStarted, onStopped,
				onPanic);
		this.onSecurityRequestError = onSecurityRequestError;
		this.cache = cache;
		this.client = client;
	}

	@Override
	public void requestSecurity(SecurityDescriptor descr) {
		int id = client.nextReqId();
		ContractHandler handler = new IBRequestSecurityHandler(this, id, descr);
		client.setContractHandler(id, handler);
		handler.connectionOpened();
	}

	@Override
	public EventType OnSecurityRequestError() {
		return onSecurityRequestError;
	}

	@Override
	public IBClient getClient() {
		return client;
	}

	@Override
	public Cache getCache() {
		return cache;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != IBTerminalImpl.class ) {
			return false;
		}
		return fieldsEquals(other);
	}
	
	@Override
	protected boolean fieldsEquals(Object other) {
		IBTerminalImpl o = (IBTerminalImpl) other;
		return new EqualsBuilder()
			.appendSuper(super.fieldsEquals(other))
			.append(o.onSecurityRequestError, onSecurityRequestError)
			.append(o.cache, cache)
			.append(o.client, client)
			.isEquals();
	}

	@Override
	public void fireSecurityRequestError(SecurityDescriptor descr,
			int errorCode, String errorMsg)
	{
		Object args[] = { descr, errorCode, errorMsg };
		logger.error("TODO: fire request {} error: [{}] {}", args);
	}

}
