package ru.prolib.aquila.core.BusinessEntities;

import java.time.Instant;

public interface MDUpdateHeader {

	public MDUpdateType getType();

	public Instant getTime();

	public Symbol getSymbol();

}
