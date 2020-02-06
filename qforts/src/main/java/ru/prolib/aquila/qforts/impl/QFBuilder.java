package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounterFactory;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.data.DataSource;

public class QFBuilder {
	private AtomicLong seqOrderID, seqExecutionID;
	private QForts facade;
	private QFTransactionService transactions;
	private QFSessionSchedule schedule;
	private DataSource dataSource;
	private EventQueue eventQueue;
	private int liquidityMode = QForts.LIQUIDITY_LIMITED;
	private boolean sds_legacy = false;
	private ApplicationContext context;
	
	public DataProvider buildDataProvider() {
		return new QFReactor(
				getFacade(),
				getRegistry(),
				getSchedule(),
				getOrderIDSequence(),
				getSymbolDataService()
			);
	}
	
	public QFortsEnv buildEnvironment(EditableTerminal terminal) {
		return new QFortsEnv(terminal, getFacade());
	}
	
	public void setBuildingContext(ApplicationContext context) {
		this.context = context;
	}
	
	private ApplicationContext getContext() {
		if ( context == null ) {
			context = new AnnotationConfigApplicationContext(QFBuildingContextConfig.class);
		}
		return context;
	}
	
	private QForts getFacade() {
		if ( facade == null ) {
			facade = new QForts(getRegistry(), getTransactions(), liquidityMode);
		}
		return facade;
	}
	
	private IQFObjectRegistry getRegistry() {
		return getContext().getBean(IQFObjectRegistry.class);
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
	
	private QFSymbolDataService getSymbolDataServiceModern() {
		return new QFSymbolDataServiceModern(getEventQueue(), true);
	}
	
	private QFSymbolDataService getSymbolDataServiceLegacy() {
		return new QFSymbolDataServiceLegacy(getFacade(), getSymbolSubscrRepository());
	}
	
	private QFSymbolDataService getSymbolDataService() {
		QFSymbolDataService service = sds_legacy ? getSymbolDataServiceLegacy() : getSymbolDataServiceModern();
		service.setDataSource(getDataSource());
		return service;
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
