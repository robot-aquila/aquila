package ru.prolib.aquila.quik.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Фасад доступа к объектам по неполному набору атрибутов.
 * <p>
 * Данных некоторых таблиц (заявок, стоп-заявок, позиций) не содержат полей,
 * необходимых для однозначной идентификации соответствующих объектов
 * бизнес-процесса (торгового счета, дескриптора инструмента). Такие таблицы
 * содержат лишь частичную информацию, позволяющую сослаться на объект при
 * условии его предварительной регистрации и совпадении определенных атрибутов.
 * Данный класс представляет собой фасад к хранилищу таких объектов. Позволяет
 * регистрировать объекты соответствующих классов, выполнять проверку
 * возможности восстановления объекта и получать соответствующие объекты
 * по неполным идентификационным данным.
 */
public class PartiallyKnownObjects {
	private final AccountRegistry accounts;
	private final SecurityDescriptorRegistry descriptors;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param accounts реестр торговых счетов
	 * @param securityDescriptors реестр дескрипторов инструментов
	 */
	public PartiallyKnownObjects(AccountRegistry accounts,
			SecurityDescriptorRegistry securityDescriptors)
	{
		super();
		this.accounts = accounts;
		this.descriptors = securityDescriptors;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Инстанцирует соответствующие реестры по-умолчанию.
	 * <p>
	 * @param firePanic генератор критического события (необходим для
	 * инстанцирования реестров)
	 */
	public PartiallyKnownObjects(FirePanicEvent firePanic) {
		this(new AccountRegistry(firePanic),
				new SecurityDescriptorRegistry(firePanic));
	}
	
	/**
	 * Получить реестр торговых счетов.
	 * <p>
	 * @return реестр торговых счетов
	 */
	public AccountRegistry getAccountRegistry() {
		return accounts;
	}
	
	/**
	 * Получить реестр дескрипторов инструментов.
	 * <p>
	 * @return реестр дескрипторов инструментов
	 */
	public SecurityDescriptorRegistry getSecurityDescriptorRegistry() {
		return descriptors;
	}
	
	/**
	 * Зарегистрировать торговый счет.
	 * <p>
	 * см. {@link AccountRegistry#registerAccount(Account)}.
	 * <p>
	 * @param account торговый счет
	 */
	public synchronized void registerAccount(Account account) {
		accounts.registerAccount(account);
	}
	
	/**
	 * Получить счет по коду клиента и коду торгового счета.
	 * <p>
	 * см. {@link AccountRegistry#getAccount(String, String)}.
	 * <p>
	 * @param clientCode код клиента
	 * @param accountCode код торгового счета
	 * @return объект счета или null, если счет не найден
	 */
	public synchronized
		Account getAccount(String clientCode, String accountCode)
	{
		return accounts.getAccount(clientCode, accountCode);
	}
	
	/**
	 * Получить счет по коду торгового счета.
	 * <p>
	 * см. {@link AccountRegistry#getAccount(String)}.
	 * <p>
	 * @param accountCode код торгового счета
	 * @return объект счета или null, если счет не найден
	 */
	public synchronized Account getAccount(String accountCode) {
		return accounts.getAccount(accountCode);
	}
	
	/**
	 * Проверить возможность определения счета по коду клиента и коду счета.
	 * <p>
	 * см. {@link AccountRegistry#isAccountRegistered(String, String)}.
	 * <p>
	 * @param clientCode код клиента
	 * @param accountCode код торгового счета
	 * @return true - счет доступен, false - счет не доступен
	 */
	public synchronized
		boolean isAccountRegistered(String clientCode, String accountCode)
	{
		return accounts.isAccountRegistered(clientCode, accountCode);
	}
	
	/**
	 * Проверить возможность определения счета по коду торгового счета.
	 * <p>
	 * см. {@link AccountRegistry#isAccountRegistered(String)}.
	 * <p>
	 * @param accountCode код торгового счета
	 * @return true - счет доступен, false - счет не доступен
	 */
	public synchronized boolean isAccountRegistered(String accountCode) {
		return accounts.isAccountRegistered(accountCode);
	}
	
	/**
	 * Зарегистрировать дескриптор инструмента.
	 * <p>
	 * см. {@link SecurityDescriptorRegistry#registerSecurityDescriptor(SecurityDescriptor, String)}.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param name наименование инструмента
	 */
	public synchronized void
		registerSecurityDescriptor(SecurityDescriptor descr, String name)
	{
		descriptors.registerSecurityDescriptor(descr, name);
	}
	
	/**
	 * Получить дескриптор инструмента по наименованию инструмента.
	 * <p>
	 * см. {@link SecurityDescriptorRegistry#getSecurityDescriptorByName(String)}.
	 * <p>
	 * @param name наименование инструмента
	 * @return дескриптор инструмента или null, если нет дескриптора для
	 * указанного наименования
	 */
	public synchronized SecurityDescriptor
		getSecurityDescriptorByName(String name)
	{
		return descriptors.getSecurityDescriptorByName(name);
	}
	
	/**
	 * Получить дескриптор инструмента по коду инструмента и коду класса.
	 * <p>
	 * см. {@link SecurityDescriptorRegistry#getSecurityDescriptorByCodeAndClass(String, String)}.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return дескриптор инструмента или null, если нет дескриптора для
	 * указанной пары кодов
	 */
	public synchronized SecurityDescriptor
		getSecurityDescriptorByCodeAndClass(String code, String classCode)
	{
		return descriptors.getSecurityDescriptorByCodeAndClass(code, classCode);
	}
	
	/**
	 * Проверить доступность дескриптора по наименованию инструмента.
	 * <p>
	 * см. {@link SecurityDescriptorRegistry#isSecurityDescriptorRegistered(String)}.
	 * <p>
	 * @param name наименование инструмента
	 * @return true - дескриптор для указанного наименования зарегистрирован,
	 * false - не зарегистрирован
	 */
	public synchronized boolean
		isSecurityDescriptorRegistered(String name)
	{
		return descriptors.isSecurityDescriptorRegistered(name);
	}
	
	/**
	 * Проверить доступность дескриптора по коду инструмента и коду класса.
	 * <p>
	 * см. {@link SecurityDescriptorRegistry#isSecurityDescriptorRegistered(String, String)}.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return true - дескриптор для указанной пары кодов зарегистрирован,
	 * false - не зарегистрирован
	 */
	public synchronized boolean
		isSecurityDescriptorRegistered(String code, String classCode)
	{
		return descriptors.isSecurityDescriptorRegistered(code, classCode);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != PartiallyKnownObjects.class ) {
			return false;
		}
		PartiallyKnownObjects o = (PartiallyKnownObjects) other;
		return new EqualsBuilder()
			.append(accounts, o.accounts)
			.append(descriptors, o.descriptors)
			.isEquals();
	}

}
