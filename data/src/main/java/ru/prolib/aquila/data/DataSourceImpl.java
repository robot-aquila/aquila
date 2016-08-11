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
	public synchronized void subscribeL1(Symbol symbol, L1UpdateConsumer consumer) {
		l1UpdateSource.subscribeL1(symbol, consumer);
	}

	@Override
	public synchronized void unsubscribeL1(Symbol symbol, L1UpdateConsumer consumer) {
		l1UpdateSource.unsubscribeL1(symbol, consumer);
	}

	@Override
	public synchronized void subscribeMD(Symbol symbol, MDUpdateConsumer consumer) {
		mdUpdateSource.subscribeMD(symbol, consumer);
	}

	@Override
	public synchronized void unsubscribeMD(Symbol symbol, MDUpdateConsumer consumer) {
		mdUpdateSource.unsubscribeMD(symbol, consumer);
	}

	@Override
	public synchronized void close() throws IOException {
		IOUtils.closeQuietly(l1UpdateSource);
		IOUtils.closeQuietly(mdUpdateSource);
	}

}
