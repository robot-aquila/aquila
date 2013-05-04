package ru.prolib.aquila.quik.dde;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Реестр счетов.
 * <p>
 * Таблицы заявок и стоп-заявок не содержат поля идентификатора фирмы, который
 * необходим для конструирования объекта счета, соответствующего заявке. Для
 * заявок можно получить идентификатор фирмы через TRANS2QUIK API, но это
 * потребует сильно специфической работы и кроме того не работает для
 * стоп-заявок и . Гораздо проще сделать кэш (аналогичный кэшу по инструментам),
 * который позволит по неполным данным (суб-коду и вторичному суб-коду) получить
 * полный объект счета, предварительно сохраненный в процессе обработки таблицы
 * позиций.
 * <p>
 * Данный класс обеспечивает доступ ко всем зарегистрированным счетам. При
 * запросе счета, по которому нет информации, генерируется соответствующее
 * событие о паническом состоянии терминала.
 * <p>
 * 2013-02-19<br>
 * $Id$
 */
public class AccountRegistry {
	private static final Logger logger;
	private static final String SEPARATOR = "@";
	private final FirePanicEvent firePanic;
	private final Map<String, Account> cache;
	
	static {
		logger = LoggerFactory.getLogger(AccountRegistry.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события о паническом состоянии
	 */
	public AccountRegistry(FirePanicEvent firePanic) {
		super();
		this.firePanic = firePanic;
		cache = new HashMap<String, Account>();
	}
	
	/**
	 * Получить генератор события о паническом состоянии.
	 * <p>
	 * @return генератор события
	 */
	public FirePanicEvent getFirePanicEvent() {
		return firePanic;
	}
	
	/**
	 * Зарегистрировать счет.
	 * <p>
	 * Полученный экземпляр рассматривается как объект, однозначно
	 * идентифицирующий торговый счет. После регистрации, данный счет можно
	 * получить по неполным данным: коду счета (суб-код) и коду счета
	 * (вторичный суб-код). Если указанный экземпляр не содержит вторичного
	 * суб-кода, то вместо него используется суб-код. Это позволяет обеспечить
	 * доступ к счетам ФОРТС и ММВБ посредством единого метода. Если
	 * образованной комбинации суб-кода и вторичного суб-кода уже соответствует
	 * иной экземпляр счета, то запись реестра будет переписана тем объектом,
	 * который был зарегистрирован последним.   
	 * <p>
	 * @param account объект счета
	 */
	public synchronized void registerAccount(Account account) {
		String code = account.getSubCode() + SEPARATOR
			+ (account.getSubCode2() == null
					? account.getSubCode() : account.getSubCode2());
		if ( ! cache.containsKey(code) ) {
			Object args[] = { account, code };
			logger.debug("New account registered: {} for the pair {}", args);
			cache.put(code, account);
		}
	}
	
	/**
	 * Получить счет по коду клиента и коду счета.
	 * <p>
	 * Если соответствующий указанным кодам счет не был зарегистрирован, то
	 * вызов метода завершится остановом терминала и возвратом null.
	 * <p>
	 * @param clientCode код клиента
	 * @param accountCode код счета
	 * @return объект счета или null, если счет не найден
	 */
	public synchronized
			Account getAccount(String clientCode, String accountCode)
	{
		String code = clientCode + SEPARATOR + accountCode;
		Account account = cache.get(code);
		if ( account == null ) {
			String msg = "Accounts: NULL account for clientId & code: {}";
			firePanic.firePanicEvent(1, msg, new Object[] { code });
		}
		return account;
	}
	
	/**
	 * Получить счет по коду счета.
	 * <p>
	 * Используется для запроса счетов, для которых суб-код и вторичный суб-код
	 * совпадают. То есть для счетов ФОРТС. Работает через вызов
	 * {@link #getAccount(String, String)}, передавая в качестве кода клиента
	 * значение кода счета. Для ММВБ счетов будет приводить к останову
	 * терминала, так как для таких счетов код клиента и код счета различны. 
	 * <p>
	 * @param accountCode код счета
	 * @return объект счета или null, если счет не найден
	 */
	public synchronized Account getAccount(String accountCode) {
		return getAccount(accountCode, accountCode);
	}
	
	/**
	 * Проверка на доступность счета по паре кодов: кода клиента и кода счета.
	 * <p>
	 * Код клиента и код счета представляют собой неполный набор атрибутов
	 * счета. Однозначно идентифицировать торговый счет по этим атрибутам
	 * возможно только при условии предварительной регистрации счета. Данный
	 * метод позволяет проверить, доступен ли счет, соответствующий указанным
	 * коду клиента и коду счета. То есть, фактически определить - был ли
	 * соответствующий счет зарегистрирован ранее. 
	 * <p>
	 * @param clientCode код клиента
	 * @param accountCode код торгового счета
	 * @return true - счет зарегистрирован и может быть получен посредством
	 * вызова метода {@link #getAccount(String, String)}, false - счет
	 * соответствующий указанным кода не зарегистрирован и попытка получить
	 * счет посредством вызова метода {@link #getAccount(String, String)}
	 * завершится остановом терминала
	 */
	public synchronized
		boolean isAccountRegistered(String clientCode, String accountCode)
	{
		return cache.containsKey(clientCode + SEPARATOR + accountCode);
	}
	
	/**
	 * Проверка на доступность счета по коду счета.
	 * <p>
	 * Используется для запроса счетов, для которых суб-код и вторичный суб-код
	 * совпадают. То есть для счетов ФОРТС. Работает через вызов
	 * {@link #isAccountRegistered(String, String)}, передавая в качестве кода
	 * клиента значение кода счета. Для ММВБ счетов будет всегда возвращать
	 * false, так как для таких счетов код клиента и код счета различны.
	 * <p>
	 * @param accountCode код торгового счета
	 * @return true - счет зарегистрирован, false - счет не зарегистрирован
	 */
	public synchronized boolean isAccountRegistered(String accountCode) {
		return isAccountRegistered(accountCode, accountCode);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == AccountRegistry.class ) {
			AccountRegistry o = (AccountRegistry) other;
			return new EqualsBuilder()
				.append(firePanic, o.firePanic)
				.append(cache, o.cache)
				.isEquals();
		} else {
			return false;
		}
	}

}
