package ru.prolib.aquila.datatools.storage;

import java.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SecurityStateUpdate extends StateUpdate {
	private Symbol symbol;

	public SecurityStateUpdate(Symbol symbol, LocalDateTime timestamp, byte[] data,
			boolean isFullRefresh)
	{
		super(timestamp, data, isFullRefresh);
		this.symbol = symbol;
	}
	
	public SecurityStateUpdate(Symbol symbol, LocalDateTime timestamp, byte[] data) {
		this(symbol, timestamp, data, false);
	}
	
	public Symbol getSymbol() {
		return symbol;
	}

}
