package ru.prolib.aquila.ib.assembler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ib.client.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.ib.api.*;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Базовый обработчик данных IB.
 * <p>
 * Реализует выполнение процедуры установки/разрыва соединения с IB API.
 * Направляет входящие данные соответствующим методам сборщика объектов. 
 */
public class IBMainHandler implements MainHandler {
	private static final Logger logger;

	
	static {
		logger = LoggerFactory.getLogger(IBMainHandler.class);
	}
	
	private final EditableTerminal terminal;
	private final IBClient client;
	private final Counter requestId;
	private final Assembler assembler;
	
	public IBMainHandler(EditableTerminal terminal, IBClient client,
			Counter requestId, Assembler assembler)
	{
		super();
		this.terminal = terminal;
		this.client = client;
		this.requestId = requestId;
		this.assembler = assembler;
	}
	
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	public IBClient getClient() {
		return client;
	}
	
	public Counter getRequestNumerator() {
		return requestId;
	}
	
	public Assembler getAssembler() {
		return assembler;
	}

	@Override
	public void error(int reqId, int errorCode, String errorMsg) {
		Object args[] = { reqId, errorCode, errorMsg };
		logger.error("req#{}: [{}] {}", args);
	}

	@Override
	public void connectionOpened() {
		client.reqAutoOpenOrders(true);
		client.reqAllOpenOrders();
		terminal.fireTerminalConnectedEvent();
	}

	@Override
	public void connectionClosed() {
		terminal.fireTerminalDisconnectedEvent();
	}

	@Override
	public void accountDownloadEnd(String accountName) {

	}

	@Override
	public void commissionReport(CommissionReport report) {

	}

	@Override
	public void currentTime(long time) {

	}

	@Override
	public void managedAccounts(String accounts) {
		String account[] = StringUtils.split(accounts, ',');
		for ( int i = 0; i < account.length; i ++ ) {
			client.reqAccountUpdates(true, account[i]);
			logger.debug("Start listening account: {}", account[i]);
		}
	}

	@Override
	public void nextValidId(int nextId) {
		synchronized ( requestId ) {
			int currId = requestId.get();
			if ( currId < nextId ) {
				requestId.set(nextId);
				logger.debug("Request ID updated to: {}", nextId);
			}
		}
	}

	@Override
	public void updateAccount(String key, String value, String cur,
			String account)
	{
		assembler.update(new PortfolioValueEntry(account, key, cur, value));
	}

	@Override
	public void updatePortfolio(Contract contract, int position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName)
	{
		assembler.update(new PositionEntry(contract, position, marketValue,
				averageCost, realizedPNL, accountName));
	}

	@Override
	public void contractDetails(int reqId, ContractDetails details) {
		assembler.update(new ContractEntry(details));
	}

	@Override
	public void bondContractDetails(int reqId, ContractDetails details) {
		contractDetails(reqId, details);
	}

	@Override
	public void contractDetailsEnd(int reqId) {
		
	}

	@Override
	public void tickPrice(int reqId, int tickType, double value) {
		
	}

	@Override
	public void tickSize(int reqId, int tickType, int size) {
		
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order,
			OrderState orderState)
	{
		assembler.update(new OrderEntry(orderId, contract, order, orderState));
	}

	@Override
	public void orderStatus(int orderId, String status, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld)
	{
		assembler.update(new OrderStatusEntry(orderId, status, remaining,
				avgFillPrice));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != IBMainHandler.class ) {
			return false;
		}
		IBMainHandler o = (IBMainHandler) other;
		return new EqualsBuilder()
			.append(o.assembler, assembler)
			.append(o.requestId, requestId)
			.appendSuper(o.terminal == terminal)
			.isEquals();
	}

}
