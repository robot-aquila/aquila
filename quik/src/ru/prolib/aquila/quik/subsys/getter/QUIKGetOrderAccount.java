package ru.prolib.aquila.quik.subsys.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Геттер объекта счета на основе кода клиента и кода счета.
 * <p>
 * Используется для восстановления объекта счета для таблиц заявок и
 * стоп-заявок.
 * <p>
 * 2013-02-21<br>
 * $Id: QUIKGetOrderAccount.java 542 2013-02-23 04:15:34Z whirlwind $
 */
public class QUIKGetOrderAccount implements G<Account> {
	private final QUIKServiceLocator locator;
	private final G<String> gSubCode;
	private final G<String> gSubCode2;
	
	public QUIKGetOrderAccount(QUIKServiceLocator locator,
			G<String> gSubCode, G<String> gSubCode2)
	{
		super();
		this.locator = locator;
		this.gSubCode = gSubCode;
		this.gSubCode2 = gSubCode2;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Получить геттер строки суб-кода.
	 * <p> 
	 * @return геттер
	 */
	public G<String> getSubCodeGetter() {
		return gSubCode;
	}

	/**
	 * Получить геттер строки вторичного суб-кода.
	 * <p> 
	 * @return геттер
	 */
	public G<String> getSubCode2Getter() {
		return gSubCode2;
	}

	@Override
	public Account get(Object source) throws ValueException {
		return locator.getAccounts()
				.getAccount(gSubCode.get(source), gSubCode2.get(source));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == QUIKGetOrderAccount.class ) {
			QUIKGetOrderAccount o = (QUIKGetOrderAccount) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.append(gSubCode, o.gSubCode)
				.append(gSubCode2, o.gSubCode2)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[subCode=" + gSubCode
			+ ", subCode2=" + gSubCode2 + "]";
	}

}
