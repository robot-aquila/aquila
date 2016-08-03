package ru.prolib.aquila.data;

import java.io.Closeable;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface L1UpdateSource extends Closeable {
	
	public void subscribeL1(Symbol symbol, L1UpdateConsumer consumer);
	
	public void unsubscribeL1(Symbol symbol, L1UpdateConsumer consumer);

}
