package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;

/**
 * Набор инструментов торговли.
 */
public class SecuritiesImpl implements EditableSecurities {
	private final SecuritiesEventDispatcher dispatcher;
	private final SecurityFactory factory;
	
	/**
	 * Карта определения инструмента по дескриптору.
	 * Содержит записи для каждого инструмента.
	 */
	private final Map<SecurityDescriptor, EditableSecurity> map;
	
	/**
	 * Конструктор
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param factory фабрика инструментов
	 */
	public SecuritiesImpl(SecuritiesEventDispatcher dispatcher,
						  SecurityFactory factory)
	{
		super();
		this.dispatcher = dispatcher;
		map = new LinkedHashMap<SecurityDescriptor, EditableSecurity>();
		this.factory = factory;
	}
	
	/**
	 * Конструктор
	 * <p>
	 * @param dispatcher диспетчер событий
	 */
	public SecuritiesImpl(SecuritiesEventDispatcher dispatcher) {
		this(dispatcher, new SecurityFactory());
	}
	
	SecurityFactory getFactory() {
		return factory;
	}

	@Override
	public synchronized List<Security> getSecurities() {
		return new Vector<Security>(map.values());
	}
	
	/**
	 * Получить используемый диспетчер событий
	 * <p>
	 * @return диспетчер событий
	 */
	public SecuritiesEventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	@Override
	public synchronized Security getSecurity(SecurityDescriptor descr)
			throws SecurityException
	{
		Security security = map.get(descr);
		if ( security == null ) {
			throw new SecurityNotExistsException(descr);
		}
		return security;
	}
	
	@Override
	public synchronized boolean isSecurityExists(SecurityDescriptor descr) {
		return map.containsKey(descr);
	}

	@Override
	public EventType OnSecurityAvailable() {
		return dispatcher.OnAvailable();
	}

	@Override
	public synchronized EditableSecurity
		getEditableSecurity(EditableTerminal terminal, SecurityDescriptor descr)
	{
		EditableSecurity security = map.get(descr);
		if ( security == null ) {
			security = factory.createInstance(terminal, descr);
			map.put(descr, security);
			dispatcher.startRelayFor(security);
		}
		return security;
	}

	@Override
	public void fireEvents(EditableSecurity security) {
		synchronized ( security ) {
			if ( security.isAvailable() ) {
				security.fireChangedEvent();
			} else {
				security.setAvailable(true);
				dispatcher.fireAvailable(security);
			}
			security.resetChanges();
		}
	}

	@Override
	public EventType OnSecurityChanged() {
		return dispatcher.OnChanged();
	}

	@Override
	public EventType OnSecurityTrade() {
		return dispatcher.OnTrade();
	}

	@Override
	public synchronized int getSecuritiesCount() {
		return map.size();
	}

	/**
	 * Установить экземпляр инструмента.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param security экземпляр инструмента
	 */
	protected synchronized void
		setSecurity(SecurityDescriptor descr, EditableSecurity security)
	{
		map.put(descr, security);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SecuritiesImpl.class ) {
			return false;
		}
		SecuritiesImpl o = (SecuritiesImpl) other;
		return new EqualsBuilder()
			.append(o.map, map)
			.append(o.factory, factory)
			.isEquals();
	}

}
