package ru.prolib.aquila.ib.subsys.security;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;

/**
 * Набор инструментов терминала Interactive Brokers.
 * <p>
 * Работает синхронно, обособлен от других элементов бизнес-модели.
 * <br>
 * TODO: обрабатка тиковых данных.  
 * <p>
 * 2012-11-19<br>
 * $Id: IBSecurities.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBSecurities implements EditableSecurities {
	private static final Logger logger;
	public static final String DEFAULT_SECCLASS = "SMART";
	protected final Map<SecurityDescriptor, IBSecurityHandler> map;
	private final IBSecurityHandlerFactory factory;
	private final EditableSecurities storage;
	
	static {
		logger = LoggerFactory.getLogger(IBSecurities.class);
	}

	public IBSecurities(EditableSecurities storage,
			IBSecurityHandlerFactory factory)
	{
		super();
		map = new Hashtable<SecurityDescriptor, IBSecurityHandler>();
		this.storage = storage;
		this.factory = factory;
	}
	
	/**
	 * Получить хранилище инструментов.
	 * <p>
	 * @return хранилище инструментов
	 */
	public EditableSecurities getStorageSecurities() {
		return storage;
	}
	
	/**
	 * Получить фабрику поставщиков инструментов.
	 * <p>
	 * @return фабрика поставшиков
	 */
	public IBSecurityHandlerFactory getSecurityHandlerFactory() {
		return factory;
	}

	@Override
	public EventType OnSecurityAvailable() {
		return storage.OnSecurityAvailable();
	}

	/**
	 * Возвращает список известных (загруженных) инструментов.
	 * <p>
	 * Возвращаются только те инструменты, по которым выполнялся запрос
	 * и были получены детали контракта. 
	 */
	@Override
	public List<Security> getSecurities() {
		return storage.getSecurities();
	}
	
	@Override
	public Security getSecurity(SecurityDescriptor descr)
			throws SecurityException
	{
		if ( getSecurityStatus(descr) == IBSecurityStatus.DONE ) {
			return storage.getSecurity(descr);
		} else {
			throw new SecurityNotExistsException(descr);
		}
	}

	@Override
	public boolean isSecurityExists(SecurityDescriptor descr) {
		try {
			return getSecurityStatus(descr) == IBSecurityStatus.DONE;
		} catch ( SecurityException e ) {
			logger.error("Couldn't get security status", e);
			return false;
		}
	}

	@Override
	public void fireSecurityAvailableEvent(Security security) {
		storage.fireSecurityAvailableEvent(security);
	}

	@Override
	public EditableSecurity getEditableSecurity(SecurityDescriptor descr)
			throws SecurityNotExistsException
	{
		return storage.getEditableSecurity(descr);
	}
	
	/**
	 * Получить статус инструмента.
	 * <p>
	 * При необходимости, создает и выполняет запрос к IB API.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return статус инструмента
	 */
	private IBSecurityStatus getSecurityStatus(SecurityDescriptor descr)
			throws SecurityException
	{
		IBSecurityHandler handler = null;
		synchronized ( map ) {
			handler = map.get(descr);
			if ( handler == null ) {
				handler = factory.createHandler(descr);
				map.put(descr, handler);
				handler.start();
			}
		}
		return handler.getSecurityStatus();
	}

	@Override
	public EventType OnSecurityChanged() {
		return storage.OnSecurityChanged();
	}

	@Override
	public EventType OnSecurityTrade() {
		return storage.OnSecurityTrade();
	}

	@Override
	public int getSecuritiesCount() {
		return storage.getSecuritiesCount();
	}

	@Override
	public EditableSecurity
		createSecurity(EditableTerminal terminal, SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		return storage.createSecurity(terminal, descr);
	}

}
