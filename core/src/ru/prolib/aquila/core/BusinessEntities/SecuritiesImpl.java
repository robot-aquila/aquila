package ru.prolib.aquila.core.BusinessEntities;

import java.util.*;
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
	private final Map<SecurityDescriptor, EditableSecurity> descrMap;
	
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
		descrMap = new LinkedHashMap<SecurityDescriptor, EditableSecurity>();
	}

	@Override
	public synchronized List<Security> getSecurities() {
		return new LinkedList<Security>(descrMap.values());
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
		Security security = descrMap.get(descr);
		if ( security == null ) {
			throw new SecurityNotExistsException(descr);
		}
		return security;
	}
	
	@Override
	public synchronized boolean isSecurityExists(SecurityDescriptor descr) {
		return descrMap.containsKey(descr);
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
		EditableSecurity security = descrMap.get(descr);
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
		return descrMap.size();
	}

	@Override
	public synchronized EditableSecurity
		createSecurity(EditableTerminal terminal, SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		if ( descrMap.containsKey(descr) ) {
			throw new SecurityAlreadyExistsException(descr);
		}
		EventSystem es = terminal.getEventSystem();
		EventDispatcher dispatcher =
			es.createEventDispatcher("Security[" + descr + "]");
		EditableSecurity s = new SecurityImpl(terminal, descr, dispatcher,
				es.createGenericType(dispatcher, "OnChanged"),
				es.createGenericType(dispatcher, "OnTrade"));
		descrMap.put(descr, s);
		return s;
	}
	
	@Override
	public boolean equals(Object other) {
		return other == this;
	}

}
