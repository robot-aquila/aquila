package ru.prolib.aquila.ib.assembler;

import java.util.Map;
import java.util.Hashtable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.ib.assembler.cache.Cache;
import ru.prolib.aquila.ib.assembler.cache.ContractEntry;
import ru.prolib.aquila.ib.subsys.api.IBConfig;
import com.ib.client.*;

/**
 * Обработчик данных IB API.
 * <p>
 * 
 */
public class IBHandler implements EWrapper {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IBHandler.class);
	}
	
	private final EClientSocket client;
	private final Counter reqNumerator;
	private final EditableTerminal terminal;
	private final EventDispatcher dispatcher;
	private final EventType onConnected, onDisconnected;
	private final Cache cache;
	/**
	 * Активная конфигурация подключения.
	 */
	private IBConfig activeConfig;
	/**
	 * Карта соответствия номера запроса номеру контракта. 
	 */
	private final Map<Integer, Integer> reqId2ContId;
	
	IBHandler(EClientSocket client, Counter reqNumerator,
			EditableTerminal terminal, Cache cache, EventDispatcher dispatcher,
			EventType onConnected, EventType onDisconnected)
	{
		super();
		this.client = client;
		this.reqNumerator = reqNumerator;
		this.terminal = terminal;
		this.cache = cache;
		this.dispatcher = dispatcher;
		this.onConnected = onConnected;
		this.onDisconnected = onDisconnected;
		reqId2ContId = new Hashtable<Integer, Integer>();
	}
	
	public synchronized IBConfig getActiveConfig() {
		return activeConfig;
	}
	
	public synchronized void connect(IBConfig config) {
		if ( client.isConnected() ) {
			return;
		}
		client.eConnect(config.getHost(),config.getPort(),config.getClientId());
		if ( client.isConnected() ) {
			activeConfig = config;
			connectionOpened();
		}
	}
	
	public synchronized void disconnect() {
		if ( client.isConnected() ) {
			client.eDisconnect();
			connectionClosed();
		}
	}
	
	public synchronized boolean isConnected() {
		return client.isConnected();
	}
	
	@Override
	public synchronized void managedAccounts(String accounts) {
		String account[] = StringUtils.split(accounts, ',');
		for ( int i = 0; i < account.length; i ++ ) {
			client.reqAccountUpdates(true, account[i]);
		}
	}

	@Override
	public synchronized void nextValidId(int nextId) {
		synchronized ( reqNumerator ) {
			int currId = reqNumerator.get();
			if ( currId < nextId ) {
				reqNumerator.set(nextId);
			}
		}
	}
	
	/**
	 * Получить номер следующего запроса.
	 * <p>
	 * @return номер запроса
	 */
	private synchronized int nextReqId() {
		return reqNumerator.getAndIncrement();
	}
	
	private synchronized void startMarketData(ContractEntry entry) {
		int reqId = nextReqId();
		reqId2ContId.put(reqId, entry.getContractId());
		client.reqMktData(reqId, entry.getContract(), null, false);
	}
	
	private synchronized void connectionOpened() {
		if ( activeConfig.getClientId() == 0 ) {
			client.reqAutoOpenOrders(true);
		}
		client.reqOpenOrders();
		client.reqExecutions(nextReqId(), new ExecutionFilter());
		for ( ContractEntry entry : cache.getContractEntries() ) {
			startMarketData(entry);
		}
		dispatcher.dispatch(new EventImpl(onConnected));
	}
	
	@Override
	public synchronized void connectionClosed() {
		activeConfig = null;
		reqId2ContId.clear();
		dispatcher.dispatch(new EventImpl(onDisconnected));
	}
	
	@Override
	public synchronized void contractDetails(int n, ContractDetails details) {
		ContractEntry entry = new ContractEntry(details);
		if ( cache.getContract(entry.getContractId()) == null ) {
			startMarketData(entry);
		}
		cache.update(entry);
	}
	
	@Override
	public void execDetails(int n, Contract contract, Execution exec) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void openOrder(int arg0, Contract arg1, Order arg2, OrderState arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void orderStatus(int arg0, String arg1, int arg2, int arg3,
			double arg4, int arg5, int arg6, double arg7, int arg8, String arg9) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickGeneric(int reqId, int field, double price) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void tickPrice(int reqId, int field, double price, int unused) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickSize(int reqId, int field, int size) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void updateAccountValue(String arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePortfolio(Contract arg0, int arg1, double arg2,
			double arg3, double arg4, double arg5, double arg6, String arg7) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(Exception e) {
		logger.error("Unexpected exception", e);
	}

	@Override
	public void error(String msg) {
		logger.error("Unexpected error: {}", msg);
	}

	@Override
	public void error(int id, int errorCode, String errorMsg) {
		if ( logger.isDebugEnabled() ) {
			Object args[] = { id, errorCode, errorMsg };
			logger.debug("error: reqid={}, code={}, msg={}", args);
		}
	}

	@Override
	public void accountDownloadEnd(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bondContractDetails(int arg0, ContractDetails arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commissionReport(CommissionReport arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contractDetailsEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void currentTime(long arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deltaNeutralValidation(int arg0, UnderComp arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execDetailsEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fundamentalData(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void historicalData(int arg0, String arg1, double arg2, double arg3,
			double arg4, double arg5, int arg6, int arg7, double arg8,
			boolean arg9) {
		// TODO Auto-generated method stub

	}

	@Override
	public void marketDataType(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void openOrderEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void realtimeBar(int arg0, long arg1, double arg2, double arg3,
			double arg4, double arg5, long arg6, double arg7, int arg8) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFA(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scannerData(int arg0, int arg1, ContractDetails arg2,
			String arg3, String arg4, String arg5, String arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scannerDataEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scannerParameters(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickEFP(int arg0, int arg1, double arg2, String arg3,
			double arg4, int arg5, String arg6, double arg7, double arg8) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickOptionComputation(int arg0, int arg1, double arg2,
			double arg3, double arg4, double arg5, double arg6, double arg7,
			double arg8, double arg9) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickSnapshotEnd(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tickString(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAccountTime(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMktDepth(int arg0, int arg1, int arg2, int arg3,
			double arg4, int arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMktDepthL2(int arg0, int arg1, String arg2, int arg3,
			int arg4, double arg5, int arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNewsBulletin(int arg0, int arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

}
