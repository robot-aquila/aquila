package ru.prolib.aquila.ib.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ib.client.*;

/**
 * Обработчик данных IB API.
 * <p>
 * Этот служебный класс представляет собой реализацию обработчика данных в
 * соответствии с требованиями IB API. Участвует в процессе упрощения
 * IB API, игнорируя те вызовы IB API, которые не представляют интереса для
 * текущей реализации терминала. Осуществляет трансляцию вызовов соответствующим
 * узкоспециализированным обработчикам.
 */
public class IBWrapper implements EWrapper {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(IBWrapper.class);
	}
	
	private final Map<Integer, ContractHandler> hContracts;
	private final Map<Integer, OrderHandler> hOrders;
	private MainHandler hMain;
	
	IBWrapper() {
		super();
		hContracts = new LinkedHashMap<Integer, ContractHandler>();
		hOrders = new LinkedHashMap<Integer, OrderHandler>();
	}
	
	/**
	 * Установить базовый обработчик данных.
	 * <p>
	 * Базовый обработчик данных получает вызов
	 * {@link ResponseHandler#connectionOpened()} в первую очередь перед
	 * аналогичными вызовами для всех других зарегистрированных обработчиков.
	 * Метод {@link ResponseHandler#connectionClosed()} вызывается для базового
	 * обработчика после того, как аналогичный метод был вызван для всех
	 * других зарегистрированных обработчиков. Методы узкоспециализированных
	 * обработчиков вызываются когда не задан соответствующий номеру запроса
	 * экземпляр узкоспециализированного обработчика.
	 * <p>
	 * @param handler обработчик
	 */
	public synchronized void setMainHandler(MainHandler handler) {
		hMain = handler;
	}
	
	/**
	 * Получить базовый обработчик данных.
	 * <p>
	 * Возвращает обработчик, установленный посредством вызова метода
	 * {@link #setMainHandler(MainHandler)}.
	 * <p>
	 * @return текущий базовый обработчик
	 */
	public synchronized MainHandler getMainHandler() {
		return hMain;
	}
	
	/**
	 * Установить обработчик запроса данных контракта.
	 * <p>
	 * Обработчик контракта получает вызовы
	 * {@link ResponseHandler#connectionOpened()} и
	 * {@link ResponseHandler#connectionClosed()} каждый раз при установлении
	 * и разрыве соединения. Но следует учитывать, что данные методы не
	 * вызываются в момент регистрации.
	 * <p>
	 * @param reqId номер запроса
	 * @param handler обработчик данных
	 */
	public synchronized
		void setContractHandler(int reqId, ContractHandler handler)
	{
		hContracts.put(reqId, handler);
	}
	
	/**
	 * Получить обработчик данных контракта.
	 * <p>
	 * @param reqId номер запроса
	 * @return обработчик или null, если обработчик не задан
	 */
	public synchronized ContractHandler getContractHandler(int reqId) {
		return hContracts.get(reqId);
	}
	
	/**
	 * Установить обработчик запроса данных заявки.
	 * <p>
	 * Обработчик заявки получает вызовы
	 * {@link ResponseHandler#connectionOpened()} и
	 * {@link ResponseHandler#connectionClosed()} каждый раз при установлении
	 * и разрыве соединения. Но следует учитывать, что данные методы не
	 * вызываются в момент регистрации.
	 * <p>
	 * @param reqId номер запроса (номер заявки)
	 * @param handler обработчик данных
	 */
	public synchronized
		void setOrderHandler(int reqId, OrderHandler handler)
	{
		hOrders.put(reqId, handler);
	}
	
	/**
	 * Получить обработчик данных заявки.
	 * <p>
	 * @param reqId номер запроса
	 * @return обработчик или null, если обработчик не задан
	 */
	public synchronized OrderHandler getOrderHandler(int reqId) {
		return hOrders.get(reqId);
	}

	
	/**
	 * Удалить обработчик данных.
	 * <p>
	 * @param reqId номер запроса
	 */
	public synchronized void removeHandler(int reqId) {
		hContracts.remove(reqId);
		hOrders.remove(reqId);
	}
	
	/**
	 * Проверить наличие обработчика данных контракта.
	 * <p>
	 * @param reqId номер запроса
	 * @return наличие обработчика
	 */
	public synchronized boolean hasContractHandler(int reqId) {
		return hContracts.containsKey(reqId);
	}
	
	/**
	 * Проверить наличие обработчика данных заявки.
	 * <p>
	 * @param reqId номер запроса
	 * @return наличие обработчика
	 */
	public synchronized boolean hasOrderHandler(int reqId) {
		return hOrders.containsKey(reqId);
	}
	
	@Override
	public synchronized void managedAccounts(String accounts) {
		hMain.managedAccounts(accounts);
	}

	@Override
	public synchronized void nextValidId(int nextId) {
		hMain.nextValidId(nextId);
	}
	
	/**
	 * Выполнить обработку подключения.
	 * <p>
	 * Данный метод введен в дополнение к {@link #connectionClosed()}, что бы
	 * обеспечить логику обработки подключения узкоспециализированных
	 * обработчиков через единую точку доступа. 
	 */
	public synchronized void connectionOpened() {
		logger.debug("Connection opened");
		hMain.connectionOpened();
		for ( ContractHandler handler : hContracts.values() ) {
			handler.connectionOpened();
		}
		for ( OrderHandler handler : hOrders.values() ) {
			handler.connectionOpened();
		}
	}
	
	@Override
	public synchronized void connectionClosed() {
		logger.debug("Connection closed");
		for ( ContractHandler handler : hContracts.values() ) {
			handler.connectionClosed();
		}
		for ( OrderHandler handler : hOrders.values() ) {
			handler.connectionClosed();
		}
		hMain.connectionClosed();
	}
	
	@Override
	public synchronized
		void contractDetails(int reqId, ContractDetails details)
	{
		ContractHandler handler = hContracts.get(reqId);
		if ( handler == null ) {
			hMain.contractDetails(reqId, details);
		} else {
			handler.contractDetails(reqId, details);
		}
	}
	
	@Override
	public synchronized
		void execDetails(int reqId, Contract contract, Execution exec)
	{

	}
	
	@Override
	public synchronized void openOrder(int reqId, Contract contract,
			Order order, OrderState orderState)
	{
		getSuitableOrderHandler(reqId)
			.openOrder(reqId, contract, order, orderState);
	}

	@Override
	public synchronized void orderStatus(int reqId, String status, int filled,
			int remaining, double avgFillPrice, int permId, int parentId,
			double lastFillPrice, int clientId, String whyHeld)
	{
		getSuitableOrderHandler(reqId)
			.orderStatus(reqId, status, filled, remaining, avgFillPrice,
					permId, parentId, lastFillPrice, clientId, whyHeld);
	}
	
	@Override
	public synchronized void tickGeneric(int reqId, int field, double price) {
		getSuitableContractHandler(reqId).tickPrice(reqId, field, price);
	}
	
	@Override
	public synchronized void tickPrice(int reqId, int field, double price,
			int unused)
	{
		getSuitableContractHandler(reqId).tickPrice(reqId, field, price);
	}

	@Override
	public synchronized void tickSize(int reqId, int field, int size) {
		getSuitableContractHandler(reqId).tickSize(reqId, field, size);
	}
	
	@Override
	public synchronized void updateAccountValue(String key, String value,
			String currency, String accountName)
	{
		hMain.updateAccount(key, value, currency, accountName);
	}

	@Override
	public synchronized void updatePortfolio(Contract contract, int position,
			double marketPrice, double marketValue, double averageCost,
			double unrealizedPNL, double realizedPNL, String accountName)
	{
		hMain.updatePortfolio(contract, position, marketPrice, marketValue,
				averageCost, unrealizedPNL, realizedPNL, accountName);
	}

	@Override
	public synchronized void error(Exception e) {
		logger.error("Unexpected exception", e);
	}

	@Override
	public synchronized void error(String msg) {
		logger.error("Unexpected error: {}", msg);
	}

	@Override
	public synchronized void error(int reqId, int errorCode, String errorMsg) {
		if ( hContracts.containsKey(reqId) ) {
			getSuitableContractHandler(reqId).error(reqId, errorCode, errorMsg);
		} else {
			getSuitableOrderHandler(reqId).error(reqId, errorCode, errorMsg);
		}
	}

	@Override
	public synchronized void accountDownloadEnd(String accountName) {
		hMain.accountDownloadEnd(accountName);
	}

	@Override
	public synchronized
		void bondContractDetails(int reqId, ContractDetails details)
	{
		getSuitableContractHandler(reqId).bondContractDetails(reqId, details);
	}

	@Override
	public synchronized void commissionReport(CommissionReport report) {
		hMain.commissionReport(report);
	}

	@Override
	public synchronized void contractDetailsEnd(int reqId) {
		getSuitableContractHandler(reqId).contractDetailsEnd(reqId);
	}

	@Override
	public synchronized void currentTime(long time) {
		hMain.currentTime(time);
	}

	@Override
	public synchronized
		void deltaNeutralValidation(int reqId, UnderComp underComp) {

	}

	@Override
	public synchronized void execDetailsEnd(int reqId) {

	}

	@Override
	public synchronized void fundamentalData(int reqId, String data) {

	}

	@Override
	public synchronized void historicalData(int reqId, String date, double open,
			double high, double low, double close, int volume, int count,
			double WAP, boolean hasGaps)
	{

	}

	@Override
	public synchronized void marketDataType(int reqId, int marketDataType) {

	}

	@Override
	public synchronized void openOrderEnd() {

	}

	@Override
	public synchronized void realtimeBar(int reqId, long time, double open,
			double high, double low, double close, long volume, double wap,
			int count)
	{

	}

	@Override
	public synchronized void receiveFA(int faDataType, String xml) {

	}

	@Override
	public synchronized void scannerData(int reqId, int rank,
			ContractDetails contractDetails, String distance, String benchmark,
			String projection, String legsStr)
	{

	}

	@Override
	public synchronized void scannerDataEnd(int reqId) {

	}

	@Override
	public synchronized void scannerParameters(String xml) {

	}

	@Override
	public synchronized void tickEFP(int reqId, int tickType,
			double basisPoints, String formattedBasisPoints,
			double impliedFuture, int holdDays, String futureExpiry,
			double dividendImpact, double dividendsToExpiry)
	{

	}

	@Override
	public synchronized void tickOptionComputation(int reqId, int field,
			double impliedVol, double delta, double optPrice, double pvDividend,
			double gamma, double vega, double theta, double undPrice)
	{

	}

	@Override
	public synchronized void tickSnapshotEnd(int reqId) {

	}

	@Override
	public synchronized void tickString(int reqId, int tickType, String value) {

	}

	@Override
	public synchronized void updateAccountTime(String timeStamp) {

	}

	@Override
	public synchronized void updateMktDepth(int reqId, int position,
			int operation, int side, double price, int size)
	{

	}

	@Override
	public synchronized void updateMktDepthL2(int reqId, int position,
			String marketMaker, int operation, int side, double price, int size)
	{

	}

	@Override
	public synchronized void updateNewsBulletin(int msgId, int msgType,
			String message, String origExchange)
	{

	}
	
	/**
	 * Ярлык для определения подходящего обработчика заявки.
	 * <p>
	 * @param reqId номер заявки
	 * @return обработчик
	 */
	private OrderHandler getSuitableOrderHandler(int reqId) {
		OrderHandler handler = hOrders.get(reqId);
		return handler == null ? hMain : handler;
	}
	
	/**
	 * Ярлык для определения подходящего обработчика контракта.
	 * <p>
	 * @param reqId номер запроса
	 * @return обработчик
	 */
	private ContractHandler getSuitableContractHandler(int reqId) {
		ContractHandler handler = hContracts.get(reqId);
		return handler == null ? hMain : handler;	
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != IBWrapper.class ) {
			return false;
		}
		IBWrapper o = (IBWrapper) other;
		return new EqualsBuilder()
			.append(o.hContracts, hContracts)
			.append(o.hMain, hMain)
			.append(o.hOrders, hOrders)
			.isEquals();
	}

}
