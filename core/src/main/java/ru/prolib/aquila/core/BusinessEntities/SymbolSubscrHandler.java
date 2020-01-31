package ru.prolib.aquila.core.BusinessEntities;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * General symbol subscription handler based on service and symbol subscription info.
 */
public class SymbolSubscrHandler implements SubscrHandler {
	
	public interface Owner {
		void onUnsubscribe(Symbol symbol, MDLevel level);
	}
	
	private final Owner service;
	private final Symbol symbol;
	private final MDLevel level;
	private final CompletableFuture<Boolean> confirm;
	private final AtomicBoolean closed;

	public SymbolSubscrHandler(Owner service,
			Symbol symbol,
			MDLevel level,
			CompletableFuture<Boolean> confirm)
	{
		this.service = service;
		this.symbol = symbol;
		this.level = level;
		this.confirm = confirm;
		this.closed = new AtomicBoolean(false);
	}
	
	public SymbolSubscrHandler(Owner service, Symbol symbol, MDLevel level) {
		this(service, symbol, level, new CompletableFuture<>());
	}
	
	public SymbolSubscrHandler(Owner service, Symbol symbol, MDLevel level, boolean confirm) {
		this(service, symbol, level);
		this.confirm.complete(confirm);
	}
	
	public Owner getOwner() {
		return service;
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
			service.onUnsubscribe(symbol, level);
		}
	}

	@Override
	public CompletableFuture<Boolean> getConfirmation() {
		return confirm;
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
				.append(o.service, service)
				.append(o.symbol, symbol)
				.append(o.level, level)
				.append(o.closed.get(), closed.get())
				.append(o.confirm, confirm)
				.build();
	}


}
