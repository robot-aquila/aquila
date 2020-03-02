package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.SymbolDataService;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.Action;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.FeedStatus;

/**
 * Autosubscriber action based on Symbol Data Service subscriptions.
 */
class QFSymbolAutosubscrActionSDS implements Action {
	private final SymbolDataService dataService;
	private SubscrHandler handler;
	
	public QFSymbolAutosubscrActionSDS(SymbolDataService data_service, SubscrHandler handler) {
		this.dataService = data_service;
		this.handler = handler;
	}
	
	public QFSymbolAutosubscrActionSDS(SymbolDataService data_service) {
		this(data_service, null);
	}

	@Override
	public void change(Symbol symbol, FeedStatus old_status, FeedStatus new_status) {
		switch ( new_status ) {
		case NOT_REQUIRED:
			if ( handler != null ) {
				handler.close();
				handler = null;
			}
			break;
		case MAX_DETAILS:
			if ( handler == null ) {
				handler = dataService.onSubscribe(symbol, MDLevel.L1);
			}
			break;
		case LESS_DETAILS:
			break;
		}
		
	}
	
}