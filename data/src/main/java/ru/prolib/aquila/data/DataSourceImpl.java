package ru.prolib.aquila.data;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Simple data source facade implementation.
 */
public class DataSourceImpl implements DataSource {
	private L1UpdateSource l1UpdateSource;
	private MDUpdateSource mdUpdateSource;
	
	public synchronized void setL1UpdateSource(L1UpdateSource source) {
		this.l1UpdateSource = source;
	}
	
	public synchronized void setMDUpdateSource(MDUpdateSource source) {
		this.mdUpdateSource = source;
	}

	@Override
	public synchronized void subscribe(Symbol symbol, L1UpdateConsumer consumer) {
		l1UpdateSource.subscribe(symbol, consumer);
	}

	@Override
	public synchronized void unsubscribe(Symbol symbol, L1UpdateConsumer consumer) {
		l1UpdateSource.unsubscribe(symbol, consumer);
	}

	@Override
	public synchronized void subscribe(Symbol symbol, MDUpdateConsumer consumer) {
		mdUpdateSource.subscribe(symbol, consumer);
	}

	@Override
	public synchronized void unsubscribe(Symbol symbol, MDUpdateConsumer consumer) {
		mdUpdateSource.unsubscribe(symbol, consumer);
	}

	@Override
	public synchronized void close() throws IOException {
		IOUtils.closeQuietly(l1UpdateSource);
		IOUtils.closeQuietly(mdUpdateSource);
		l1UpdateSource = null;
		mdUpdateSource = null;
	}

}
