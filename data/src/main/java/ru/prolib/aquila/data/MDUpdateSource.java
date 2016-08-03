package ru.prolib.aquila.data;

import java.io.Closeable;

import ru.prolib.aquila.core.BusinessEntities.MDUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * A market depth data source.
 */
public interface MDUpdateSource extends Closeable {
	
	public void subscribeMD(Symbol symbol, MDUpdateConsumer consumer);
	
	public void unsubscribeMD(Symbol symbol, MDUpdateConsumer consumer);

}
