package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.atomic.AtomicLong;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.data.DataProvider;

public class QFBuilder {
	private AtomicLong seqOrderID, seqExecutionID;
	private QForts facade;
	private QFObjectRegistry registry;
	private QFTransactionService transactions;
	private QFSessionSchedule schedule;
	
	public DataProvider buildDataProvider() {
		return new QFReactor(getFacade(), getRegistry(), getSchedule(), getOrderIDSequence());
	}
	
	public QFortsEnv buildEnvironment(EditableTerminal terminal) {
		return new QFortsEnv(terminal, getFacade());
	}
	
	private QForts getFacade() {
		if ( facade == null ) {
			facade = new QForts(getRegistry(), getTransactions());
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

}
