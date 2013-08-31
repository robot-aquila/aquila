package ru.prolib.aquila.ib;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.ib.api.ContractHandler;
import ru.prolib.aquila.ib.api.IBClient;
import ru.prolib.aquila.ib.assembler.*;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Реализация терминала Interactive Brokers.
 */
public class IBTerminalImpl extends TerminalImpl implements IBEditableTerminal {
	private final Cache cache;
	private final IBClient client;
	
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
	 * @param cache кэш данных IB
	 * @param client экземпляр подключения к IB API
	 */
	public IBTerminalImpl(EventSystem eventSystem, Scheduler scheduler,
			Starter starter, EditableSecurities securities,
			EditablePortfolios portfolios, EditableOrders orders,
			TerminalController controller,
			TerminalEventDispatcher dispatcher, Cache cache, IBClient client)
	{
		super(eventSystem, scheduler, starter, securities, portfolios, orders,
				controller, dispatcher);
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
	 * @param cache кэш данных IB
	 * @param client экземпляр подключения к IB API
	 */
	public IBTerminalImpl(EventSystem eventSystem, Starter starter,
		EditableSecurities securities, EditablePortfolios portfolios,
		EditableOrders orders, 
		TerminalEventDispatcher dispatcher, Cache cache, IBClient client)
	{
		super(eventSystem, starter, securities, portfolios, orders, dispatcher);
		this.cache = cache;
		this.client = client;
	}

	@Override
	public synchronized void requestSecurity(SecurityDescriptor descr) {
		int id = nextReqId();
		ContractHandler handler = new IBRequestSecurityHandler(this, id, descr);
		client.setContractHandler(id, handler);
		handler.connectionOpened();
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
			.append(o.cache, cache)
			.append(o.client, client)
			.isEquals();
	}

	@Override
	public synchronized void requestContract(int conId) {
		int id = nextReqId();
		ContractHandler handler = new IBRequestContractHandler(this, id, conId);
		client.setContractHandler(id, handler);
		handler.connectionOpened();
	}
	
	private int nextReqId() {
		return getOrderNumerator().incrementAndGet();
	}

}
