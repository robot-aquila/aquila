package ru.prolib.aquila.qforts.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.Action;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.FeedStatus;

public class QFSymbolAutosubscrActionDS implements Action {
	static final Logger logger = LoggerFactory.getLogger(QFSymbolAutosubscrActionDS.class);
	private final DataSource dataSource;
	private final L1UpdateConsumer consumer;
	
	public QFSymbolAutosubscrActionDS(DataSource data_source, L1UpdateConsumer consumer) {
		this.dataSource = data_source;
		this.consumer = consumer;
	}

	@Override
	public void change(Symbol symbol, FeedStatus old_status, FeedStatus new_status) {
		Object args[] = { symbol, old_status, new_status };
		switch ( new_status ) {
		case NOT_REQUIRED:
			logger.debug("Unsubscribe: {} (from {} -> to {})", args);
			dataSource.unsubscribeL1(symbol, consumer);
			break;
		case MAX_DETAILS:
			if ( old_status == FeedStatus.NOT_REQUIRED ) {
				logger.debug("Subscribe: {} (from {} -> to {})", args);
				dataSource.subscribeL1(symbol, consumer);
			} else {
				logger.debug("Skip request for {} (from {} -> to {})", args);
			}
			break;
		case LESS_DETAILS:
			break;
		}
	}

}
