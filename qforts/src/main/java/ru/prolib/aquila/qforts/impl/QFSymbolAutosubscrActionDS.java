package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.Action;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.FeedStatus;

public class QFSymbolAutosubscrActionDS implements Action {
	private final DataSource dataSource;
	private final L1UpdateConsumer consumer;
	
	public QFSymbolAutosubscrActionDS(DataSource data_source, L1UpdateConsumer consumer) {
		this.dataSource = data_source;
		this.consumer = consumer;
	}

	@Override
	public void change(Symbol symbol, FeedStatus old_status, FeedStatus new_status) {
		switch ( new_status ) {
		case NOT_REQUIRED:
			dataSource.unsubscribeL1(symbol, consumer);
			break;
		case MAX_DETAILS:
			dataSource.subscribeL1(symbol, consumer);
			break;
		case LESS_DETAILS:
			break;
		}
	}

}
