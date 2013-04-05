package ru.prolib.aquila.quik.subsys.portfolio;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Хранилище счетов.
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
public class QUIKAccounts {
	private static final Logger logger;
	private static final String SEPARATOR = "@";
	private final FirePanicEvent firePanic;
	private final Map<String, Account> cache;
	
	static {
		logger = LoggerFactory.getLogger(QUIKAccounts.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события о паническом состоянии
	 */
	public QUIKAccounts(FirePanicEvent firePanic) {
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
	 * Если указанный счет не содержит вторичного суб-кода, то вместо него
	 * используется суб-код. Это позволяет обеспечить доступ к счетам ФОРТС
	 * и ММВБ посредством единого метода. 
	 * <p>
	 * @param account объект счета
	 */
	public synchronized void register(Account account) {
		String code = account.getSubCode() + SEPARATOR
			+ (account.getSubCode2() == null
					? account.getSubCode() : account.getSubCode2());
		if ( ! cache.containsKey(code) ) {
			Object args[] = { account };
			logger.debug("New account registered: {}", args);
			cache.put(code, account);
		}
	}
	
	/**
	 * Получить счет по коду клиента и коду счета.
	 * <p>
	 * @param clientCode код клиента
	 * @param accCode код счета
	 * @return объект счета или null, если счет не найден
	 */
	public synchronized Account getAccount(String clientCode, String accCode) {
		String code = clientCode + SEPARATOR + accCode;
		Account account = cache.get(code);
		if ( account == null ) {
			String msg = "Accounts: NULL account for clientId & code: {}";
			firePanic.firePanicEvent(1, msg, new Object[] { code });
		}
		return account;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == QUIKAccounts.class ) {
			QUIKAccounts o = (QUIKAccounts) other;
			return new EqualsBuilder()
				.append(firePanic, o.firePanic)
				.append(cache, o.cache)
				.isEquals();
		} else {
			return false;
		}
	}

}
