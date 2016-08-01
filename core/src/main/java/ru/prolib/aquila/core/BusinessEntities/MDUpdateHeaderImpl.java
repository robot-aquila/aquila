package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class MDUpdateHeaderImpl implements MDUpdateHeader {
	private final MDUpdateType type;
	private final Instant time;
	private final Symbol symbol;

	public MDUpdateHeaderImpl(MDUpdateType type, Instant time, Symbol symbol) {
		super();
		this.type = type;
		this.time = time;
		this.symbol = symbol;
	}
	
	@Override
	public MDUpdateType getType() {
		return type;
	}

	@Override
	public Instant getTime() {
		return time;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null || !(other instanceof MDUpdateHeader) ) {
			return false;
		}
		MDUpdateHeader o = (MDUpdateHeader) other;
		return new EqualsBuilder()
			.append(type, o.getType())
			.append(time, o.getTime())
			.append(symbol, o.getSymbol())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + type + " " + symbol + "@" + time + "]";
	}

}
