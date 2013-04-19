package ru.prolib.aquila.core.BusinessEntities.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер-валидатор наличия счета.
 * <p>
 * Проверяет наличие портфеля, которому соответствует счет, полученный с помощью
 * подчиненного геттера. Если подчиненный геттер возвращает нулевое значение,
 * то данный класс рассматривает это как уже обработанную ситуацию и не
 * выполняет дополнительной обработки. 
 * <p>
 * 2013-02-18<br>
 * $Id$
 */
public class GAccountExists implements G<Account> {
	private final EditableTerminal terminal;
	private final G<Account> gAccount;
	private final String msgPrefix;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param gAccount геттер счета
	 * @param msgPrefix префикс сообщения о паническом состоянии
	 */
	public GAccountExists(EditableTerminal terminal, G<Account> gAccount,
			String msgPrefix)
	{
		super();
		this.terminal = terminal;
		this.gAccount = gAccount;
		this.msgPrefix = msgPrefix;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}

	/**
	 * Получить геттер счета.
	 * <p>
	 * @return геттер
	 */
	public G<Account> getAccountGetter() {
		return gAccount;
	}
	
	/**
	 * Получить префикс сообщения об ошибке.
	 * <p>
	 * @return префикс сообщения
	 */
	public String getMessagePrefix() {
		return msgPrefix;
	}

	@Override
	public Account get(Object source) throws ValueException {
		Account account = gAccount.get(source);
		if ( account == null ) {
			return null;
		}
		if ( terminal.isPortfolioAvailable(account) ) {
			return account;
		}
		String msg = msgPrefix + "Portfolio not exists: {}";
		terminal.firePanicEvent(1, msg, new Object[] { account });
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GAccountExists.class ) {
			GAccountExists o = (GAccountExists) other;
			return new EqualsBuilder()
				.append(terminal, o.terminal)
				.append(gAccount, o.gAccount)
				.append(msgPrefix, o.msgPrefix)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[value=" + gAccount
			+ ", msgPfx='" + msgPrefix + "']";
	}

}
