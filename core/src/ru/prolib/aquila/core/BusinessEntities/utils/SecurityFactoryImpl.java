package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityImpl;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

/**
 * Фабрика инструментов биржевой торговли.
 * <p>
 * 2012-07-05<br>
 * $Id: SecurityFactoryImpl.java 522 2013-02-12 12:07:35Z whirlwind $
 */
public class SecurityFactoryImpl implements SecurityFactory {
	private final EventSystem eventSystem;
	private final Terminal terminal;
	
	/**
	 * Создать фабрику инструментов.
	 * <p>
	 * @param eventSystem фасад событийной системы
	 * @param terminal терминал
	 */
	public SecurityFactoryImpl(EventSystem eventSystem, Terminal terminal) {
		super();
		if ( eventSystem == null ) {
			throw new NullPointerException("Event system cannot be null");
		}
		if ( terminal == null ) {
			throw new NullPointerException("Terminal cannot be null");
		}
		this.eventSystem = eventSystem;
		this.terminal = terminal;
	}

	@Override
	public EditableSecurity createSecurity(SecurityDescriptor descr) {
		EventDispatcher dispatcher =
			eventSystem.createEventDispatcher("Security[" + descr + "]");
		return new SecurityImpl(terminal, descr, dispatcher,
				eventSystem.createGenericType(dispatcher, "OnChanged"),
				eventSystem.createGenericType(dispatcher, "OnTrade"));
	}
	
	/**
	 * Получить фасад событийной системы.
	 * <p>
	 * @return фасад событийной системы
	 */
	public EventSystem getEventSystem() {
		return eventSystem;
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
	public int hashCode() {
		return new HashCodeBuilder(20121109, 124513)
			.append(eventSystem)
			.append(terminal)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof SecurityFactoryImpl ) {
			SecurityFactoryImpl o = (SecurityFactoryImpl) other;
			return new EqualsBuilder()
				.append(eventSystem, o.eventSystem)
				.append(terminal, o.terminal)
				.isEquals();
		} else {
			return false;
		}
	}

}
