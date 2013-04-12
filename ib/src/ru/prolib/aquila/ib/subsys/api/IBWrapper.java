package ru.prolib.aquila.ib.subsys.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.event.IBEvent;
import ru.prolib.aquila.ib.event.IBEventAccounts;
import ru.prolib.aquila.ib.event.IBEventContract;
import ru.prolib.aquila.ib.event.IBEventError;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;
import ru.prolib.aquila.ib.event.IBEventRequest;
import ru.prolib.aquila.ib.event.IBEventTick;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

import com.ib.client.*;

/**
 * Диспетчер ответов от IB API.
 * <p>
 * Данный класс транслирует вызовы {@link com.ib.client.EWrapper EWrapper},
 * осуществляемые приемником ответов от Interactive Brokers API, в события.
 * Такой подход позволяет полностью абстрагироваться от библиотеки IB API в
 * части механизма обработки ответов на запросы.    
 * <p>
 * 2012-11-14<br>
 * $Id: IBWrapper.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBWrapper implements EWrapper,IBApiEventDispatcher {
	private static final int VERSION = 2;
	protected static volatile Logger logger;
	private final EventDispatcher dispatcher;
	private final EventType onConnectionClosed;
	private final EventType onError;
	private final EventType onNextValidId;
	private final EventType onContractDetails;
	private final EventType onManagedAccounts;
	private final EventType onUpdateAccount;
	private final EventType onUpdatePortfolio;
	private final EventType onOpenOrder;
	private final EventType onOrderStatus;
	private final EventType onTick;
	
	static {
		logger = LoggerFactory.getLogger(IBWrapper.class);
	}
	
	public IBWrapper(EventDispatcher dispatcher,
						EventType onConnectionClosed,
						EventType onError,
						EventType onNextValidId,
						EventType onContractDetails,
						EventType onManagedAccounts,
						EventType onUpdateAccount,
						EventType onUpdatePortfolio,
						EventType onOpenOrder,
						EventType onOrderStatus,
						EventType onTick)
	{
		super();
		this.dispatcher = dispatcher;
		this.onConnectionClosed = onConnectionClosed;
		this.onError = onError;
		this.onNextValidId = onNextValidId;
		this.onContractDetails = onContractDetails;
		this.onManagedAccounts = onManagedAccounts;
		this.onUpdateAccount = onUpdateAccount;
		this.onUpdatePortfolio = onUpdatePortfolio;
		this.onOpenOrder = onOpenOrder;
		this.onOrderStatus = onOrderStatus;
		this.onTick = onTick;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == IBWrapper.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		IBWrapper o = (IBWrapper) other;
		return new EqualsBuilder()
			.append(VERSION, 2)
			.append(dispatcher, o.dispatcher)
			.append(onConnectionClosed, o.onConnectionClosed)
			.append(onError, o.onError)
			.append(onNextValidId, o.onNextValidId)
			.append(onContractDetails, o.onContractDetails)
			.append(onManagedAccounts, o.onManagedAccounts)
			.append(onUpdateAccount, o.onUpdateAccount)
			.append(onUpdatePortfolio, o.onUpdatePortfolio)
			.append(onOpenOrder, o.onOpenOrder)
			.append(onOrderStatus, o.onOrderStatus)
			.append(onTick, o.onTick)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121207, 24519)
			.append(VERSION)
			.append(dispatcher)
			.append(onConnectionClosed)
			.append(onError)
			.append(onNextValidId)
			.append(onContractDetails)
			.append(onManagedAccounts)
			.append(onUpdateAccount)
			.append(onUpdatePortfolio)
			.append(onOpenOrder)
			.append(onOrderStatus)
			.append(onTick)
			.toHashCode();
	}
	
	/**
	 * Вывести в лог информацию о нереализованном методе.
	 */
	private void notimplemented() {
		//StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		//if ( stack.length > 2 ) {
		//	logger.debug(stack[2].getMethodName() + " not implemented");
		//} else {
		//	logger.error("direct call of debug method");
		//}
	}

	@Override
	public void connectionClosed() {
		dispatcher.dispatch(new IBEvent(onConnectionClosed));
	}

	@Override
	public void error(Exception e) {
		logger.error("unexpected call error(Exception)", e);
	}

	@Override
	public void error(String str) {
		logger.error("unexpected call error(String): {}", str);
	}

	@Override
	public void error(int id, int errorCode, String errorMsg) {
		if ( logger.isDebugEnabled() ) {
			Object a[] = { id, errorCode, errorMsg };
			logger.debug("error: reqid={}, code={}, msg={}", a);
		}
		dispatcher.dispatch(new IBEventError(onError, id, errorCode, errorMsg));
	}

	@Override
	public void accountDownloadEnd(String accountName) {
		notimplemented();
	}

	@Override
	public void commissionReport(CommissionReport commissionReport) {
		notimplemented();
	}

	@Override
	public void contractDetails(int reqId, ContractDetails details) {
		dispatcher.dispatch(new IBEventContract(onContractDetails, reqId,
				IBEventContract.SUBTYPE_NORM, details));
	}
	
	@Override
	public void bondContractDetails(int reqId, ContractDetails details) {
		dispatcher.dispatch(new IBEventContract(onContractDetails, reqId,
				IBEventContract.SUBTYPE_BOND, details));
	}

	@Override
	public void contractDetailsEnd(int reqId) {
		dispatcher.dispatch(new IBEventContract(onContractDetails, reqId,
				IBEventContract.SUBTYPE_END, null));
	}

	@Override
	public void currentTime(long time) {
		notimplemented();
	}

	@Override
	public void deltaNeutralValidation(int reqId, UnderComp underComp) {
		notimplemented();		
	}

	@Override
	public void execDetails(int reqId, Contract contract, Execution execution) {
		notimplemented();
	}

	@Override
	public void execDetailsEnd(int reqId) {
		notimplemented();
	}

	@Override
	public void fundamentalData(int reqId, String data) {
		notimplemented();
	}

	@Override
	public void historicalData(int reqId, String date, double open,
			double high, double low, double close, int volume, int count,
			double WAP, boolean hasGaps)
	{
		notimplemented();
	}

	@Override
	public void managedAccounts(String accounts) {
		dispatcher.dispatch(new IBEventAccounts(onManagedAccounts, accounts));
	}

	@Override
	public void marketDataType(int reqId, int marketDataType) {
		notimplemented();
	}

	@Override
	public void nextValidId(int orderId) {
		dispatcher.dispatch(new IBEventRequest(onNextValidId, orderId));
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order,
			OrderState orderState)
	{
		dispatcher.dispatch(new IBEventOpenOrder(onOpenOrder, orderId,
				contract, order, orderState));
	}

	@Override
	public void openOrderEnd() {
		notimplemented();
	}

	@Override
	public void orderStatus(int orderId, String status, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld)
	{
		dispatcher.dispatch(new IBEventOrderStatus(onOrderStatus, orderId,
				status, filled, remaining, avgFillPrice, permId, parentId,
				lastFillPrice, clientId, whyHeld));
	}

	@Override
	public void realtimeBar(int reqId, long time, double open, double high,
			double low, double close, long volume, double wap, int count)
	{
		notimplemented();
	}

	@Override
	public void receiveFA(int faDataType, String xml) {
		notimplemented();
	}

	@Override
	public void scannerData(int reqId, int rank,
			ContractDetails contractDetails, String distance, String benchmark,
			String projection, String legsStr)
	{
		notimplemented();
	}

	@Override
	public void scannerDataEnd(int reqId) {
		notimplemented();
	}

	@Override
	public void scannerParameters(String xml) {
		notimplemented();
	}

	@Override
	public void tickEFP(int tickerId, int tickType, double basisPoints,
			String formattedBasisPoints, double impliedFuture, int holdDays,
			String futureExpiry, double dividendImpact,
			double dividendsToExpiry)
	{
		notimplemented();
	}

	@Override
	public void tickGeneric(int tickerId, int tickType, double value) {
		dispatcher.dispatch(new IBEventTick(onTick, tickerId, tickType, value));
	}

	@Override
	public void tickOptionComputation(int tickerId, int field,
			double impliedVol, double delta, double optPrice,
			double pvDividend, double gamma, double vega, double theta,
			double undPrice)
	{
		notimplemented();
	}

	@Override
	public void tickPrice(int tickerId, int field, double price,
			int canAutoExecute)
	{
		dispatcher.dispatch(new IBEventTick(onTick, tickerId, field, price));
	}

	@Override
	public void tickSize(int tickerId, int field, int size) {
		dispatcher.dispatch(new IBEventTick(onTick, tickerId, field, size));
	}

	@Override
	public void tickSnapshotEnd(int reqId) {
		notimplemented();
	}

	@Override
	public void tickString(int tickerId, int tickType, String value) {
		notimplemented();
	}

	@Override
	public void updateAccountTime(String timeStamp) {
		notimplemented();
	}

	@Override
	public void updateAccountValue(String key, String value, String currency,
			String accountName)
	{
		dispatcher.dispatch(new IBEventUpdateAccount(onUpdateAccount,
				key, value, currency, accountName));
	}

	@Override
	public void updateMktDepth(int tickerId, int position, int operation,
			int side, double price, int size)
	{
		notimplemented();
	}

	@Override
	public void updateMktDepthL2(int tickerId, int position,
			String marketMaker, int operation, int side, double price, int size)
	{
		notimplemented();
	}

	@Override
	public void updateNewsBulletin(int msgId, int msgType, String message,
			String origExchange)
	{
		notimplemented();
	}

	@Override
	public void updatePortfolio(Contract contract, int position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName)
	{
		dispatcher.dispatch(new IBEventUpdatePortfolio(onUpdatePortfolio,
				contract, position, marketPrice, marketValue, averageCost,
				unrealizedPNL, realizedPNL, accountName));
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	@Override
	public EventType OnConnectionClosed() {
		return onConnectionClosed;
	}

	@Override
	public EventType OnError() {
		return onError;
	}

	@Override
	public EventType OnNextValidId() {
		return onNextValidId;
	}
	
	@Override
	public EventType OnContractDetails() {
		return onContractDetails;
	}

	@Override
	public EventType OnManagedAccounts() {
		return onManagedAccounts;
	}

	@Override
	public EventType OnUpdateAccount() {
		return onUpdateAccount;
	}

	@Override
	public EventType OnUpdatePortfolio() {
		return onUpdatePortfolio;
	}

	@Override
	public EventType OnOpenOrder() {
		return onOpenOrder;
	}

	@Override
	public EventType OnOrderStatus() {
		return onOrderStatus;
	}

	@Override
	public EventType OnTick() {
		return onTick;
	}

}
