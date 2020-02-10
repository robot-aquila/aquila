package ru.prolib.aquila.core.BusinessEntities;

import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.osc.impl.OrderParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PortfolioParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PositionParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.SecurityParamsBuilder;

/**
 * Object factory of standard model entities.
 * <p>
 * <b>This class is not thread-safe!</b>
 * <p>
 * This implementation works considering that the:
 * <ul>
 * <li>All objects excepting terminal were built by this factory;</li>
 * <li>This thread owns an exclusive lock on the terminal during the call;</li>
 * </ul>
 * Failure to comply with these conditions may result in a deadlock or unexpected exceptions.
 * <p>
 * Change log:
 * <p>
 * <b>2020-02-10</b> - dumb solution of deadlock issue require using of same
 * lock instance among all objects. So, the factory require a lock instance injected.
 * <p>
 */
public class ObjectFactoryImpl implements ObjectFactory {
	private final Lock globalLock;
	
	public ObjectFactoryImpl(Lock global_lock) {
		this.globalLock = global_lock;
	}

	@Override
	public EditableSecurity createSecurity(EditableTerminal terminal, Symbol symbol) {
		return new SecurityImpl(new SecurityParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withSymbol(symbol)
				.withLock(globalLock)
				.buildParams());
	}

	@Override
	public EditablePortfolio createPortfolio(EditableTerminal terminal, Account account) {
		return new PortfolioImpl(new PortfolioParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withAccount(account)
				.withObjectFactory(this)
				.withLock(globalLock)
				.buildParams());
	}

	@Override
	public EditableOrder createOrder(EditableTerminal terminal, Account account, Symbol symbol, long id) {
		return new OrderImpl(new OrderParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withOrderID(id)
				.withLock(globalLock)
				.buildParams());
	}

	@Override
	public EditablePosition createPosition(EditableTerminal terminal, Account account, Symbol symbol) {
		terminal.getEditablePortfolio(account);
		return new PositionImpl(new PositionParamsBuilder(terminal.getEventQueue())
				.withTerminal(terminal)
				.withAccount(account)
				.withSymbol(symbol)
				.withLock(globalLock)
				.buildParams());
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(globalLock)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ObjectFactoryImpl.class ) {
			return false;
		}
		ObjectFactoryImpl o = (ObjectFactoryImpl) other;
		return new EqualsBuilder()
				.append(o.globalLock, globalLock)
				.build();
	}

}
