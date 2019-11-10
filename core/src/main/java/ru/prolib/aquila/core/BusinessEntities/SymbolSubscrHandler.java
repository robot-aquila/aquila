package ru.prolib.aquila.core.BusinessEntities;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Symbol subscription handler based on symbol subscription repository.
 */
public class SymbolSubscrHandler implements SubscrHandler {
	private final SymbolSubscrRepository repository;
	private final Symbol symbol;
	private final MDLevel level;
	private final AtomicBoolean closed;
	
	public SymbolSubscrHandler(SymbolSubscrRepository repository, Symbol symbol, MDLevel level) {
		this.repository = repository;
		this.symbol = symbol;
		this.level = level;
		this.closed = new AtomicBoolean(false);
	}
	
	public SymbolSubscrRepository getRepository() {
		return repository;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public MDLevel getLevel() {
		return level;
	}
	
	public boolean isClosed() {
		return closed.get();
	}

	@Override
	public void close() {
		if ( closed.compareAndSet(false, true) ) {
			repository.unsubscribe(symbol, level);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolSubscrHandler.class ) {
			return false;
		}
		SymbolSubscrHandler o = (SymbolSubscrHandler) other;
		return new EqualsBuilder()
				.append(o.repository, repository)
				.append(o.symbol, symbol)
				.append(o.level, level)
				.append(o.closed.get(), closed.get())
				.build();
	}

}
