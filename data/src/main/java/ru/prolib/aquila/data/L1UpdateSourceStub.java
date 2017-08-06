package ru.prolib.aquila.data;

import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class L1UpdateSourceStub implements L1UpdateSource {

	@Override
	public void close() throws IOException {

	}

	@Override
	public void subscribeL1(Symbol symbol, L1UpdateConsumer consumer) {

	}

	@Override
	public void unsubscribeL1(Symbol symbol, L1UpdateConsumer consumer) {

	}

	@Override
	public void setStartTimeL1(Symbol symbol, Instant time) {
		
	}

}
