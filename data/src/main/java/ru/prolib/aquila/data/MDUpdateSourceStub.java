package ru.prolib.aquila.data;

import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.MDUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class MDUpdateSourceStub implements MDUpdateSource {

	@Override
	public void close() throws IOException {

	}

	@Override
	public void subscribeMD(Symbol symbol, MDUpdateConsumer consumer) {

	}

	@Override
	public void unsubscribeMD(Symbol symbol, MDUpdateConsumer consumer) {

	}

}
