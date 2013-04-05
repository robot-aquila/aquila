package ru.prolib.aquila.core.BusinessEntities;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.utils.SecurityFactory;

/**
 * Базовая реализации набора инструментов торговли.
 * <p>
 * 2012-06-09<br>
 * $Id: SecuritiesImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class SecuritiesImpl implements EditableSecurities,EventListener {
	private final SecurityFactory factory;
	private final EventDispatcher dispatcher;
	private final EventType onAvailable;
	private final EventType onChanged;
	private final EventType onTrade;
	private final String defaultCurrency;
	private final SecurityType defaultType;
	
	/**
	 * Карта определения инструмента по дескриптору.
	 * Содержит записи для каждого инструмента.
	 */
	private final Map<SecurityDescriptor, EditableSecurity> descMap;
	
	/**
	 * Карта определения инструмента по коду.
	 * Содержит запись только для тех инструментов, которые могут быть
	 * обнозначно идентифицированы по коду инструмента. Если элемента с
	 * запрошенным кодом в данной карте нет, это может означать как отсутствие
	 * инструмента в наборе, так и наличие нескольких инструментов с идентичным
	 * кодом.
	 */
	private final Map<String, EditableSecurity> codeMap;
	
	/**
	 * Создать объект
	 * <p>
	 * @param securityFactory фабрика инструментов
	 * @param eventDispatcher диспетчер событий
	 * @param onAvailable тип события на добавление инструмента
	 * @param defaultCurrency код валюты по умолчанию
	 * @param defaultType тип инструмента по кмолчанию
	 */
	public SecuritiesImpl(SecurityFactory securityFactory,
						  EventDispatcher eventDispatcher,
						  EventType onAvailable,
						  EventType onChanged,
						  EventType onTrade,
						  String defaultCurrency,
						  SecurityType defaultType)
	{
		super();
		if ( securityFactory == null ) {
			throw new NullPointerException("Security factory cannot be null");
		}
		this.factory = securityFactory;
		if ( eventDispatcher == null ) {
			throw new NullPointerException("Event dispatcher cannot be null");
		}
		this.dispatcher = eventDispatcher;
		if ( onAvailable == null || onChanged == null || onTrade == null ) {
			throw new NullPointerException("Event type cannot be null");
		}
		this.onAvailable = onAvailable;
		this.onChanged = onChanged;
		this.onTrade = onTrade;
		if ( defaultCurrency == null ) {
			throw new NullPointerException("Default currency cannot be null");
		}
		this.defaultCurrency = defaultCurrency;
		if ( defaultType == null ) {
			throw new NullPointerException("Default type cannot be null");
		}
		this.defaultType = defaultType;
		descMap = new LinkedHashMap<SecurityDescriptor, EditableSecurity>();
		codeMap = new Hashtable<String, EditableSecurity>();
	}

	@Override
	public synchronized List<Security> getSecurities() {
		return new LinkedList<Security>(descMap.values());
	}
	
	/**
	 * Получить используемый диспетчер событий
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить фабрику инструментов
	 * <p>
	 * @return фабрика инструментов
	 */
	public SecurityFactory getSecurityFactory() {
		return factory;
	}
	
	@Override
	public String getDefaultCurrency() {
		return defaultCurrency;
	}
	
	@Override
	public SecurityType getDefaultType() {
		return defaultType;
	}

	@Override
	public synchronized Security getSecurity(String code, String classCode)
			throws SecurityException
	{
		return getSecurity(new SecurityDescriptor(code, classCode,
				defaultCurrency, defaultType));
	}

	@Override
	public synchronized Security getSecurity(String code)
			throws SecurityException
	{
		if ( ! isSecurityExists(code) ) {
			throw new SecurityNotExistsException(code);
		}
		if ( isSecurityAmbiguous(code) ) {
			throw new SecurityAmbiguousException(code);
		}
		return codeMap.get(code);
	}
	
	@Override
	public synchronized Security getSecurity(SecurityDescriptor descr)
			throws SecurityException
	{
		Security security = descMap.get(descr);
		if ( security == null ) {
			throw new SecurityNotExistsException(descr);
		}
		return security;
	}

	@Override
	public synchronized
		boolean isSecurityExists(String code, String classCode)
	{
		return isSecurityExists(new SecurityDescriptor(code, classCode,
				defaultCurrency, defaultType));
	}

	@Override
	public synchronized boolean isSecurityExists(String code) {
		for ( SecurityDescriptor desc : descMap.keySet() ) {
			if ( desc.getCode().equals(code) ) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized boolean isSecurityExists(SecurityDescriptor descr) {
		return descMap.containsKey(descr);
	}

	@Override
	public synchronized boolean isSecurityAmbiguous(String code) {
		return (isSecurityExists(code) && codeMap.get(code) == null)
			? true : false;
	}

	@Override
	public EventType OnSecurityAvailable() {
		return onAvailable;
	}

	@Override
	public synchronized
		EditableSecurity getEditableSecurity(SecurityDescriptor descr)
	{
		EditableSecurity security = descMap.get(descr);
		if ( security == null ) {
			// TODO: по уму, инстанцирование нужно перенести в обработчик ряда,
			// так как может возникнуть необходимость собирать в одном наборе
			// инструменты из различных источников и возможно различных классов.
			security = factory.createSecurity(descr);
			security.OnChanged().addListener(this);
			security.OnTrade().addListener(this);
			// Если инструмент в карте определения по коду, значит инструмент
			// с таким кодом уже есть. Это значит, что после добавления уже
			// нельзя будет однозначно определить инструмент используя только
			// код инструмента
			if ( codeMap.containsKey(descr.getCode()) ) {
				codeMap.remove(descr.getCode());
			} else {
				codeMap.put(descr.getCode(), security);
			}
			descMap.put(descr, security);
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
			EventType map[][] = {
					{ security.OnChanged(), onChanged },
					{ security.OnTrade(), onTrade },
			};
			for ( int i = 0; i < map.length; i ++ ) {
				if ( event.isType(map[i][0]) ) {
					dispatcher.dispatch(new SecurityEvent(map[i][1], security));
					break;
				}
			}
		}
	}

	@Override
	public synchronized int getSecuritiesCount() {
		return descMap.size();
	}

}
