package ru.prolib.aquila.datatools.storage.model;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolEntity {
	private Long id;
	private Symbol symbol;

	public SymbolEntity() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

}
