package ru.prolib.aquila.data.storage.segstor;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Symbol data segment.
 */
abstract public class SymbolSegment {
	private final Symbol symbol;
	
	public SymbolSegment(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}

}
