package ru.prolib.aquila.data;

import java.io.Closeable;

import ru.prolib.aquila.core.BusinessEntities.MDUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * A market depth data source.
 */
public interface MDUpdateSource extends Closeable {
	
	public void subscribe(Symbol symbol, MDUpdateConsumer consumer);
	
	public void unsubscribe(Symbol symbol, MDUpdateConsumer consumer);

}
