package ru.prolib.aquila.datatools.tickdatabase;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;

public class L1UpdateImpl implements L1Update {
	private final Symbol symbol;
	private final Tick tick;
	
	public L1UpdateImpl(Symbol symbol, Tick tick) {
		super();
		this.symbol = symbol;
		this.tick = tick;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
	}

	@Override
	public Tick getTick() {
		return tick;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(symbol).append(tick).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != L1UpdateImpl.class ) {
			return false;
		}
		L1UpdateImpl o = (L1UpdateImpl) other;
		return new EqualsBuilder()
			.append(symbol, o.getSymbol())
			.append(tick, o.getTick())
			.isEquals();
	}

}
