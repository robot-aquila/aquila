package ru.prolib.aquila.data;

import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolDeltaUpdateSourceStub implements SymbolDeltaUpdateSource {

	@Override
	public void close() throws IOException {

	}

	@Override
	public void subscribeSymbol(Symbol symbol, DeltaUpdateConsumer consumer) {

	}

	@Override
	public void unsubscribeSymbol(Symbol symbol, DeltaUpdateConsumer consumer) {

	}

}
