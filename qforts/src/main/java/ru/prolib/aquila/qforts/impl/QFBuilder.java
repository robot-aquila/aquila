package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounterFactory;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.data.DataSource;

public class QFBuilder {
	private AtomicLong seqOrderID, seqExecutionID;
	private QForts facade;
	private QFObjectRegistry registry;
	private QFTransactionService transactions;
	private QFSessionSchedule schedule;
	private DataSource dataSource;
	private EventQueue eventQueue;
	private int liquidityMode = QForts.LIQUIDITY_LIMITED;
	
	public DataProvider buildDataProvider() {
		return new QFReactor(
				getFacade(),
				getRegistry(),
				getSchedule(),
				getOrderIDSequence(),
				getDataSource(),
				getSymbolSubscrRepository()
			);
	}
	
	public QFortsEnv buildEnvironment(EditableTerminal terminal) {
		return new QFortsEnv(terminal, getFacade());
	}
	
	private QForts getFacade() {
		if ( facade == null ) {
			facade = new QForts(getRegistry(), getTransactions(), liquidityMode);
		}
		return facade;
	}
	
	private QFObjectRegistry getRegistry() {
		if ( registry == null ) {
			registry = new QFObjectRegistry();
		}
		return registry;
	}
	
	private QFTransactionService getTransactions() {
		if ( transactions == null ) {
			transactions = new QFTransactionService(getRegistry(), getExecutionIDSequence());
		}
		return transactions;
	}
	
	private AtomicLong getExecutionIDSequence() {
		if ( seqExecutionID == null ) {
			seqExecutionID = new AtomicLong();
		}
		return seqExecutionID;
	}
	
	private AtomicLong getOrderIDSequence() {
		if ( seqOrderID == null ) {
			seqOrderID = new AtomicLong();
		}
		return seqOrderID;
	}
	
	private QFSessionSchedule getSchedule() {
		if ( schedule == null ) {
			schedule = new QFSessionSchedule();
		}
		return schedule;
	}
	
	private DataSource getDataSource() {
		if ( dataSource == null ) {
			throw new IllegalStateException("Data source was not specified");
		}
		return dataSource;
	}
	
	public QFBuilder withDataSource(DataSource data_source) {
		this.dataSource = data_source;
		return this;
	}
	
	private SymbolSubscrRepository getSymbolSubscrRepository() {
		return new SymbolSubscrRepository(new SymbolSubscrCounterFactory(getEventQueue()), "QFORTS-SYMBOL-SUBSCR");
	}
	
	private EventQueue getEventQueue() {
		if ( eventQueue == null ) {
			throw new IllegalStateException("Event queue was not specified");
		}
		return eventQueue;
	}
	
	public QFBuilder withEventQueue(EventQueue queue) {
		this.eventQueue = queue;
		return this;
	}
	
	public QFBuilder withLiquidityMode(int liquidity_mode) {
		if ( liquidity_mode != QForts.LIQUIDITY_LIMITED
		  && liquidity_mode != QForts.LIQUIDITY_APPLY_TO_ORDER
		  && liquidity_mode != QForts.LIQUIDITY_UNLIMITED )
		{
			throw new IllegalArgumentException("Unknown liquidity mode: " + liquidity_mode);
		}
		this.liquidityMode = liquidity_mode;
		return this;
	}

}
