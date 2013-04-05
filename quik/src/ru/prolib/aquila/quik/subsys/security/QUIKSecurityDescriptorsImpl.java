package ru.prolib.aquila.quik.subsys.security;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Реализация хранилища дескрипторов инструментов.
 * <p>
 * 2013-01-23<br>
 * $Id: QUIKSecurityDescriptorsImpl.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class QUIKSecurityDescriptorsImpl implements QUIKSecurityDescriptors {
	private static final Logger logger;
	private final Map<String, SecurityDescriptor> name2descr;
	private final Map<String, SecurityDescriptor> codeClass2descr;
	private final QUIKServiceLocator locator;
	
	static {
		logger = LoggerFactory.getLogger(QUIKSecurityDescriptorsImpl.class);
	}
	
	/**
	 * Конструктор.
	 */
	public QUIKSecurityDescriptorsImpl(QUIKServiceLocator locator) {
		super();
		name2descr = new Hashtable<String, SecurityDescriptor>();
		codeClass2descr = new Hashtable<String, SecurityDescriptor>();
		this.locator = locator;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}

	@Override
	public synchronized void register(SecurityDescriptor descr, String name) {
		String msg = "New security descriptor registered for ";
		String code_class = descr.getCode() +"@"+ descr.getClassCode();
		if ( ! name2descr.containsKey(name) ) {
			name2descr.put(name, descr);
			logger.debug(msg + "name: {}", name);
		}
		if ( ! codeClass2descr.containsKey(code_class) ) {
			codeClass2descr.put(code_class, descr);
			logger.debug(msg + "code@class: {}", code_class);
		}
	}

	@Override
	public synchronized SecurityDescriptor getByName(String name) {
		SecurityDescriptor descr = name2descr.get(name);
		if ( descr == null ) {
			String msg = "NULL security descriptor by name: {}";
			Object[] args = { name }; 
			locator.getTerminal().firePanicEvent(1, msg, args);
		}
		return descr;
	}

	@Override
	public synchronized
			SecurityDescriptor getByCodeAndClass(String code, String classCode)
	{
		String search = code + "@" + classCode;
		SecurityDescriptor descr = codeClass2descr.get(search);
		if ( descr == null ) {
			String msg = "NULL security descriptor by code & class: {}";
			Object[] args = { search };
			locator.getTerminal().firePanicEvent(1, msg, args);
		}
		return descr;
	}

}
