package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;

/**
 * Набор инструментов торговли.
 */
public class SecuritiesImpl implements EditableSecurities, EventListener {
	private final EventDispatcher dispatcher;
	private final EventType onAvailable;
	private final EventType onChanged;
	private final EventType onTrade;
	
	/**
	 * Карта определения инструмента по дескриптору.
	 * Содержит записи для каждого инструмента.
	 */
	private final Map<SecurityDescriptor, EditableSecurity> map;
	
	/**
	 * Конструктор
	 * <p>
	 * @param eventDispatcher диспетчер событий
	 * @param onAvailable тип события: доступен инструмент
	 * @param onChanged тип события: изменение инструмента
	 * @param onTrade тип события: новая сделка по инструменту
	 */
	public SecuritiesImpl(EventDispatcher eventDispatcher,
						  EventType onAvailable,
						  EventType onChanged,
						  EventType onTrade)
	{
		super();
		this.dispatcher = eventDispatcher;
		this.onAvailable = onAvailable;
		this.onChanged = onChanged;
		this.onTrade = onTrade;
		map = new LinkedHashMap<SecurityDescriptor, EditableSecurity>();
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
	public EventDispatcher getEventDispatcher() {
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
		return onAvailable;
	}

	@Override
	public synchronized
		EditableSecurity getEditableSecurity(SecurityDescriptor descr)
			throws SecurityNotExistsException
	{
		EditableSecurity security = map.get(descr);
		if ( security == null ) {
			throw new SecurityNotExistsException(descr);
		}
		return security;
	}

	@Override
	public void fireSecurityAvailableEvent(Security security) {
		dispatcher.dispatch(new SecurityEvent(OnSecurityAvailable(), security));
	}

	@Override
	public EventType OnSecurityChanged() {
		return onChanged;
	}

	@Override
	public EventType OnSecurityTrade() {
		return onTrade;
	}

	@Override
	public void onEvent(Event event) {
		if ( event instanceof SecurityEvent ) {
			Security security = ((SecurityEvent) event).getSecurity();
			if ( event.isType(security.OnChanged()) ) {
				dispatcher.dispatch(new SecurityEvent(onChanged, security));
			} else if ( event.isType(security.OnTrade()) ) {
				dispatcher.dispatch(new SecurityTradeEvent(onTrade, security,
					((SecurityTradeEvent) event).getTrade()));
			}
		}
	}

	@Override
	public synchronized int getSecuritiesCount() {
		return map.size();
	}

	@Override
	public synchronized EditableSecurity
		createSecurity(EditableTerminal terminal, SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		if ( map.containsKey(descr) ) {
			throw new SecurityAlreadyExistsException(descr);
		}
		EventSystem es = terminal.getEventSystem();
		EventDispatcher dispatcher =
			es.createEventDispatcher("Security[" + descr + "]");
		EditableSecurity s = new SecurityImpl(terminal, descr, dispatcher,
				es.createGenericType(dispatcher, "OnChanged"),
				es.createGenericType(dispatcher, "OnTrade"));
		s.OnChanged().addListener(this);
		s.OnTrade().addListener(this);
		map.put(descr, s);
		return s;
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
			.append(o.dispatcher, dispatcher)
			.append(o.map, map)
			.append(o.onAvailable, onAvailable)
			.append(o.onChanged, onChanged)
			.append(o.onTrade, onTrade)
			.isEquals();
	}

}
