package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.CommissionReport;
import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.ExecutionFilter;
import com.ib.client.Order;
import com.ib.client.OrderState;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.ib.api.MainHandler;
import ru.prolib.aquila.ib.assembler.cache.ContractEntry;

public class IBMainHandler implements MainHandler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IBMainHandler.class);
	}

	@Override
	public void error(int reqId, int errorCode, String errorMsg) {

	}

	@Override
	public void connectionOpened() {
		/*
		client.reqAutoOpenOrders(true);
		client.reqOpenOrders();
		client.reqExecutions(nextReqId(), new ExecutionFilter());
		for ( ContractEntry entry : cache.getContractEntries() ) {
			startMarketData(entry);
		}
		dispatcher.dispatch(new EventImpl(onConnected));
		*/
	}

	@Override
	public void connectionClosed() {
		//dispatcher.dispatch(new EventImpl(onDisconnected));
	}

	@Override
	public void accountDownloadEnd(String accountName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void commissionReport(CommissionReport report) {
		// TODO Auto-generated method stub

	}

	@Override
	public void currentTime(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void managedAccounts(String accounts) {
		/*
		String account[] = StringUtils.split(accounts, ',');
		for ( int i = 0; i < account.length; i ++ ) {
			client.reqAccountUpdates(true, account[i]);
		}
		*/
	}

	@Override
	public void nextValidId(int orderId) {
		/*
		synchronized ( reqNumerator ) {
			int currId = reqNumerator.get();
			if ( currId < nextId ) {
				reqNumerator.set(nextId);
			}
		}
		*/
	}

	@Override
	public void updateAccount(String key, String value, String currency,
			String accountName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePortfolio(Contract contract, int position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contractDetails(int reqId, ContractDetails details) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails details) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contractDetailsEnd(int reqId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickPrice(int reqId, int tickType, double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tickSize(int reqId, int tickType, int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order,
			OrderState orderState)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void orderStatus(int orderId, String status, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld)
	{
		// TODO Auto-generated method stub
		
	}

}
