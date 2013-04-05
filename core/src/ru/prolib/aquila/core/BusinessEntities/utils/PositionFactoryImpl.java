package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditablePosition;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.PositionImpl;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

/**
 * Фабрика торговых позиций.
 * <p>
 * 2012-08-03<br>
 * $Id: PositionFactoryImpl.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class PositionFactoryImpl implements PositionFactory {
	private final EventSystem eventSystem;
	private Account account;
	private EditableTerminal terminal;
	
	/**
	 * Конструктор фабрики.
	 * <p>
	 * @param eventSystem фасад событийной системы
	 * @param account счет
	 * @param terminal терминал
	 */
	public PositionFactoryImpl(EventSystem eventSystem,
			Account account, EditableTerminal terminal)
	{
		super();
		if ( eventSystem == null ) {
			throw new NullPointerException("Event system cannot be null");
		}
		if ( account == null ) {
			throw new NullPointerException("Account cannot be null");
		}
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		this.eventSystem = eventSystem;
		this.terminal = terminal;
		this.account = account;
	}
	
	/**
	 * Получить фасад событийной системы.
	 * <p>
	 * @return фасад системы событий
	 */
	public EventSystem getEventSystem() {
		return eventSystem;
	}
	
	/**
	 * Получить торговый счет.
	 * <p>
	 * @return счет
	 */
	public Account getAccount() {
		return account;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public synchronized
		EditablePosition createPosition(SecurityDescriptor descr)
	{
		EventDispatcher dispatcher =
			eventSystem.createEventDispatcher("Position[" + descr + "]");
		PositionImpl p = new PositionImpl(account, terminal, descr,
				dispatcher,
				eventSystem.createGenericType(dispatcher, "OnChanged"));
		p.setChanged();
		return p;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == PositionFactoryImpl.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		PositionFactoryImpl o = (PositionFactoryImpl) other;
		return new EqualsBuilder()
			.append(eventSystem, o.eventSystem)
			.append(account, o.account)
			.append(terminal, o.terminal)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121109, 10637)
			.append(eventSystem)
			.append(account)
			.append(terminal)
			.toHashCode();
	}

}
