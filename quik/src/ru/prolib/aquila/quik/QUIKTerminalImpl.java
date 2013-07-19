package ru.prolib.aquila.quik;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalController;
import ru.prolib.aquila.quik.api.QUIKClient;
import ru.prolib.aquila.quik.assembler.cache.Cache;

/**
 * Терминал QUIK.
 */
public class QUIKTerminalImpl extends TerminalImpl
	implements QUIKEditableTerminal
{
	private final Cache cache;
	private final QUIKClient client;
	
	/**
	 * Конструктор (полный).
	 * <p>
	 * Позволяет определять экземпляры всех используемых классов объектов. 
	 * <p>
	 * @param eventSystem фасад системы событий
	 * @param scheduler шедулер
	 * @param starter стартер
	 * @param securities набор инструментов
	 * @param portfolios набор портфелей
	 * @param orders набор заявок
	 * @param controller контроллер запуска/останова терминала
	 * @param dispatcher диспетчер событий
	 * @param onConnected тип события: при подключении
	 * @param onDisconnected тип события: при отключении
	 * @param onStarted тип события: при старте терминала
	 * @param onStopped тип события: при останове терминала
	 * @param onPanic тип критического события
	 * @param onReqSecurityError тип события: ошибка запроса инструмента
	 * @param cache кэш данных
	 * @param client подключение к API
	 */
	public QUIKTerminalImpl(EventSystem eventSystem, Scheduler scheduler,
			Starter starter, EditableSecurities securities,
			EditablePortfolios portfolios, EditableOrders orders,
			TerminalController controller, EventDispatcher dispatcher,
			EventType onConnected, EventType onDisconnected,
			EventType onStarted, EventType onStopped, EventType onPanic,
			EventType onReqSecurityError, Cache cache, QUIKClient client)
	{
		super(eventSystem, scheduler, starter, securities, portfolios, orders,
				controller, dispatcher, onConnected, onDisconnected, onStarted,
				onStopped, onPanic, onReqSecurityError);
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
	 * @param dispatcher диспетчер событий
	 * @param onConnected тип события: при подключении
	 * @param onDisconnected тип события: при отключении
	 * @param onStarted тип события: при старте терминала
	 * @param onStopped тип события: при останове терминала
	 * @param onPanic тип критического события
	 * @param onReqSecurityError тип события: ошибка запроса инструмента
	 * @param cache кэш данных
	 * @param client подключение к API
	 */
	public QUIKTerminalImpl(EventSystem eventSystem,
			Starter starter, EditableSecurities securities,
			EditablePortfolios portfolios, EditableOrders orders,
			EventDispatcher dispatcher,
			EventType onConnected, EventType onDisconnected,
			EventType onStarted, EventType onStopped, EventType onPanic,
			EventType onReqSecurityError, Cache cache, QUIKClient client)
	{
		super(eventSystem, starter, securities, portfolios, orders,
				dispatcher, onConnected, onDisconnected, onStarted,
				onStopped, onPanic, onReqSecurityError);
		this.cache = cache;
		this.client = client;
	}

	@Override
	public Cache getDataCache() {
		return cache;
	}

	@Override
	public QUIKClient getClient() {
		return client;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QUIKTerminalImpl.class ) {
			return false;
		}
		return fieldsEquals(other);
	}
	
	@Override
	protected boolean fieldsEquals(Object other) {
		QUIKTerminalImpl o = (QUIKTerminalImpl) other;
		return new EqualsBuilder()
			.appendSuper(super.fieldsEquals(other))
			.append(o.cache, cache)
			.append(o.client, client)
			.isEquals();
	}

}
