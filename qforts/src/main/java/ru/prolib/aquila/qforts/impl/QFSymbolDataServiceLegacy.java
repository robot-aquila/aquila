package ru.prolib.aquila.qforts.impl;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrHandler.Owner;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.data.DataSource;

public class QFSymbolDataServiceLegacy implements QFSymbolDataService, Owner {
	private final QForts facade;
	private final SymbolSubscrRepository symbolSubs;
	private EditableTerminal terminal;
	private DataSource dataSource;
	
	public QFSymbolDataServiceLegacy(QForts facade, SymbolSubscrRepository symbol_subs) {
		this.facade = facade;
		this.symbolSubs = symbol_subs;
	}

	@Override
	public synchronized void setTerminal(EditableTerminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public synchronized void setDataSource(DataSource data_source) {
		this.dataSource = data_source;
	}

	@Override
	public synchronized SubscrHandler onSubscribe(Symbol symbol, MDLevel level) {
		boolean doit = false;
		SymbolSubscrCounter subs = null;
		EditableSecurity security = terminal.getEditableSecurity(symbol);
		symbolSubs.lock();
		try {
			subs = symbolSubs.subscribe(symbol, level);
			subs.lock();
		} finally {
			symbolSubs.unlock();
		}
		try {
			if ( subs.getNumL0() == 1 ) {
				doit = true;
			}
		} finally {
			subs.unlock();
		}
		if ( doit ) {
			facade.registerSecurity(security);
			dataSource.subscribeL1(symbol, security);
			dataSource.subscribeMD(symbol, security);
			dataSource.subscribeSymbol(symbol, security);
		}
		return new SymbolSubscrHandler(this, symbol, level, true);
	}

	@Override
	public synchronized void onUnsubscribe(Symbol symbol, MDLevel level) {
		boolean doit = false;
		SymbolSubscrCounter subs = null;
		EditableSecurity security = terminal.getEditableSecurity(symbol);
		symbolSubs.lock();
		try {
			subs = symbolSubs.unsubscribe(symbol, level);
			subs.lock();
		} finally {
			symbolSubs.unlock();
		}
		try {
			if ( subs.getNumL0() == 0 ) {
				doit = true;
			}
		} finally {
			subs.unlock();
		}
		if ( doit ) {
			dataSource.unsubscribeL1(symbol, security);
			dataSource.unsubscribeMD(symbol, security);
			dataSource.unsubscribeSymbol(symbol, security);
		}
	}

	@Override
	public void onConnectionStatusChange(boolean connected) {
		
	}

}
