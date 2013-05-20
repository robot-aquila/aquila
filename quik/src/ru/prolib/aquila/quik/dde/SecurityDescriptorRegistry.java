package ru.prolib.aquila.quik.dde;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Реализация хранилища дескрипторов инструментов.
 * <p>
 * 2013-01-23<br>
 * $Id: QUIKSecurityDescriptorsImpl.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class SecurityDescriptorRegistry {
	private static final Logger logger;
	private final Map<String, SecurityDescriptor> name2descr;
	private final Map<String, SecurityDescriptor> codeClass2descr;
	private final FirePanicEvent firePanic;
	
	static {
		logger = LoggerFactory.getLogger(SecurityDescriptorRegistry.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор критического события
	 */
	public SecurityDescriptorRegistry(FirePanicEvent firePanic) {
		super();
		name2descr = new Hashtable<String, SecurityDescriptor>();
		codeClass2descr = new Hashtable<String, SecurityDescriptor>();
		this.firePanic = firePanic;
	}
	
	/**
	 * Получить генератор критического события.
	 * <p>
	 * @return генератор события
	 */
	public FirePanicEvent getFirePanicEvent() {
		return firePanic;
	}

	/**
	 * Зарегистрировать дескриптор инструмента.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param name наименование инструмента (для доступа по наименованию)
	 */
	public synchronized void
		registerSecurityDescriptor(SecurityDescriptor descr, String name)
	{
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

	/**
	 * Получить дескриптор инструмента по наименованию.
	 * <p>
	 * Попытка запроса несуществующего дескриптора приводит к генерации
	 * критического события (останову терминала) и возврату null.
	 * <p>
	 * @param name наименование инструмента
	 * @return дескриптор инструмента или null, если соответствующего
	 * инструмента нет в реестре
	 */
	public synchronized SecurityDescriptor
		getSecurityDescriptorByName(String name)
	{
		SecurityDescriptor descr = name2descr.get(name);
		if ( descr == null ) {
			String msg = "NULL security descriptor by name: {}";
			Object[] args = { name }; 
			firePanic.firePanicEvent(1, msg, args);
		}
		return descr;
	}

	/**
	 * Получить дескриптор инструмента по коду инструмента и коду класса.
	 * <p>
	 * Попытка запроса несуществующего дескриптора приводит к генерации
	 * критического события (останову терминала) и возврату null.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return дескриптор инструмента или null, если соответствующего
	 * инструмента нет в реестре
	 */
	public synchronized SecurityDescriptor
		getSecurityDescriptorByCodeAndClass(String code, String classCode)
	{
		String search = code + "@" + classCode;
		SecurityDescriptor descr = codeClass2descr.get(search);
		if ( descr == null ) {
			String msg = "NULL security descriptor by code & class: {}";
			Object[] args = { search };
			firePanic.firePanicEvent(1, msg, args);
		}
		return descr;
	}
	
	/**
	 * Проверить наличие дескриптора инструмента по наименованию.
	 * <p>
	 * @param name наименование инструмента
	 * @return true - дескриптор инструмента с таким именем зарегистрирован,
	 * false - не зарегистрирован
	 */
	public synchronized boolean
		isSecurityDescriptorRegistered(String name)
	{
		return name2descr.containsKey(name);
	}
	
	/**
	 * Проверить наличие дескриптора инструмента по коду и классу инструмента.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return true - дескриптор инструмента с таким кодом инструмента и кодом
	 * класса зарегистрирован, false - не зарегистрирован
	 */
	public synchronized boolean
		isSecurityDescriptorRegistered(String code, String classCode)
	{
		return codeClass2descr.containsKey(code + "@" + classCode);
	}
	
	/**
	 * Сравнить два реестра.
	 * <p>
	 * Два реестра считаются эквивалентными, если они используют один и тот же
	 * экземпляр генератора критического события и содержат эквивалентные карты
	 * дескрипторов инструментов. Такой способ сравнения выбран с целью
	 * подавления бесконечной рекурсии при сравнении, когда генератор прямо или
	 * косвенно ссылается на экземпляр реестра дескрипторов. Например генератор
	 * - это терминал, который содержит стартер кэша, который в свою очередь
	 * содержит реестр дескрипторов, ссылающийся на терминал, как на генератор
	 * критических событий.
	 */
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null
		  || other.getClass() != SecurityDescriptorRegistry.class )
		{
			return false;
		}
		SecurityDescriptorRegistry o = (SecurityDescriptorRegistry) other;
		return new EqualsBuilder()
			.appendSuper(firePanic == o.firePanic)
			.append(name2descr, o.name2descr)
			.append(codeClass2descr, o.codeClass2descr)
			.isEquals();
	}

}
