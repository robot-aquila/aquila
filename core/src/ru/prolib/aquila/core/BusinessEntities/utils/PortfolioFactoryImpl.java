package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FirePanicEvent;
import ru.prolib.aquila.core.BusinessEntities.PortfolioImpl;
import ru.prolib.aquila.core.BusinessEntities.PositionsImpl;

/**
 * Стандартная фабрика портфелей.
 * <p>
 * 2012-09-10<br>
 * $Id$
 */
public class PortfolioFactoryImpl implements PortfolioFactory {
	private final EventSystem eventSystem;
	private final EditableTerminal terminal;
	
	/**
	 * Создать фабрику портфелей.
	 * <p>
	 * @param eventSystem фасад системы событий
	 * @param terminal терминал
	 */
	public PortfolioFactoryImpl(EventSystem eventSystem,
			EditableTerminal terminal)
	{
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
	
	/**
	 * Получить используемый терминал.
	 * <p>
	 * @return терминал
	 */
	public FirePanicEvent getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить фасад событийной системы.
	 * <p>
	 * @return фасад
	 */
	public EventSystem getEventSystem() {
		return eventSystem;
	}

	@Override
	public EditablePortfolio createPortfolio(Account account) {
		EventDispatcher posDispatcher =
			eventSystem.createEventDispatcher("Positions[" + account + "]");
		PositionsImpl positions = new PositionsImpl(
				new PositionFactoryImpl(eventSystem, account, terminal),
				posDispatcher,
				eventSystem.createGenericType(posDispatcher, "OnAvailable"),
				eventSystem.createGenericType(posDispatcher, "OnChanged"));
		EventDispatcher dispatcher =
			eventSystem.createEventDispatcher("Portfolio[" + account + "]");
		EditablePortfolio portfolio = new PortfolioImpl(terminal, account,
			positions, dispatcher,
			eventSystem.createGenericType(dispatcher, "OnChanged"));
		return portfolio;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof PortfolioFactoryImpl ) {
			PortfolioFactoryImpl o = (PortfolioFactoryImpl) other;
			return new EqualsBuilder()
				.append(eventSystem, o.eventSystem)
				.append(terminal, o.terminal)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, 220013)
			.append(eventSystem)
			.append(terminal)
			.toHashCode();
	}

}
