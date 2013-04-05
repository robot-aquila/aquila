package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.List;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrders;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolios;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurities;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.FirePanicEvent;
import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderBuilder;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.OrderProcessor;
import ru.prolib.aquila.core.BusinessEntities.Portfolio;
import ru.prolib.aquila.core.BusinessEntities.PortfolioException;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.TerminalState;

/**
 * Декоратор терминала.
 * <p>
 * 2012-08-17<br>
 * $Id: TerminalDecorator.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public class TerminalDecorator implements EditableTerminal {
	private EditableTerminal terminal;

	/**
	 * Создать декоратор.
	 */
	public TerminalDecorator() {
		super();
	}
	
	/**
	 * Установить декорируемый терминал.
	 * <p>
	 * @param terminal
	 */
	public  void setTerminal(EditableTerminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * Получить декорированный терминал.
	 * <p>
	 * @return терминал или null, если терминал не задан
	 */
	public FirePanicEvent getTerminal() {
		return terminal;
	}

	@Override
	public  List<Security> getSecurities() {
		return terminal.getSecurities();
	}

	@Override
	public  Security getSecurity(String code, String classCode)
		throws SecurityException
	{
		return terminal.getSecurity(code, classCode);
	}

	@Override
	public  Security getSecurity(SecurityDescriptor descr)
		throws SecurityException
	{
		return terminal.getSecurity(descr);
	}

	@Override
	public  Security getSecurity(String code)
		throws SecurityException
	{
		return terminal.getSecurity(code);
	}

	@Override
	public boolean isSecurityExists(String code, String classCode) {
		return terminal.isSecurityExists(code, classCode);
	}

	@Override
	public  boolean isSecurityExists(String code) {
		return terminal.isSecurityExists(code);
	}

	@Override
	public  boolean isSecurityExists(SecurityDescriptor descr) {
		return terminal.isSecurityExists(descr);
	}

	@Override
	public  boolean isSecurityAmbiguous(String code) {
		return terminal.isSecurityAmbiguous(code);
	}

	@Override
	public  EventType OnSecurityAvailable() {
		return terminal.OnSecurityAvailable();
	}

	@Override
	public  boolean isPortfolioAvailable(Account account) {
		return terminal.isPortfolioAvailable(account);
	}

	@Override
	public  EventType OnPortfolioAvailable() {
		return terminal.OnPortfolioAvailable();
	}

	@Override
	public  List<Portfolio> getPortfolios() {
		return terminal.getPortfolios();
	}

	@Override
	public  Portfolio getPortfolio(Account account) throws PortfolioException {
		return terminal.getPortfolio(account);
	}

	@Override
	public void start() throws StarterException {
		terminal.start();
	}

	@Override
	public  boolean started() {
		return terminal.started();
	}

	@Override
	public  void stop() throws StarterException {
		terminal.stop();
	}

	@Override
	public  Portfolio getDefaultPortfolio() throws PortfolioException {
		return terminal.getDefaultPortfolio();
	}

	@Override
	public  boolean isOrderExists(long id) {
		return terminal.isOrderExists(id);
	}

	@Override
	public  List<Order> getOrders() {
		return terminal.getOrders();
	}

	@Override
	public  Order getOrder(long id) throws OrderException {
		return terminal.getOrder(id);
	}

	@Override
	public  EventType OnOrderAvailable() {
		return terminal.OnOrderAvailable();
	}

	@Override
	public  boolean isStopOrderExists(long id) {
		return terminal.isStopOrderExists(id);
	}

	@Override
	public  List<Order> getStopOrders() {
		return terminal.getStopOrders();
	}

	@Override
	public  Order getStopOrder(long id) throws OrderException {
		return terminal.getStopOrder(id);
	}

	@Override
	public  EventType OnStopOrderAvailable() {
		return terminal.OnStopOrderAvailable();
	}

	@Override
	public Order createMarketOrderB(Account account, Security sec, long qty)
		throws OrderException
	{
		return terminal.createMarketOrderB(account, sec, qty);
	}

	@Override
	public Order createMarketOrderS(Account account, Security sec, long qty)
		throws OrderException
	{
		return terminal.createMarketOrderS(account, sec, qty);
	}

	@Override
	public void placeOrder(Order order) throws OrderException {
		terminal.placeOrder(order);
	}

	@Override
	public void cancelOrder(Order order) throws OrderException {
		terminal.cancelOrder(order);
	}

	@Override
	public String getDefaultCurrency() {
		return terminal.getDefaultCurrency();
	}

	@Override
	public SecurityType getDefaultType() {
		return terminal.getDefaultType();
	}

	@Override
	public int getOrdersCount() {
		return terminal.getOrdersCount();
	}

	@Override
	public EventType OnOrderCancelFailed() {
		return terminal.OnOrderCancelFailed();
	}

	@Override
	public EventType OnOrderCancelled() {
		return terminal.OnOrderCancelled();
	}

	@Override
	public EventType OnOrderChanged() {
		return terminal.OnOrderChanged();
	}

	@Override
	public EventType OnOrderDone() {
		return terminal.OnOrderDone();
	}

	@Override
	public EventType OnOrderFailed() {
		return terminal.OnOrderFailed();
	}

	@Override
	public EventType OnOrderFilled() {
		return terminal.OnOrderFilled();
	}

	@Override
	public EventType OnOrderPartiallyFilled() {
		return terminal.OnOrderPartiallyFilled();
	}

	@Override
	public EventType OnOrderRegistered() {
		return terminal.OnOrderRegistered();
	}

	@Override
	public EventType OnOrderRegisterFailed() {
		return terminal.OnOrderRegisterFailed();
	}

	@Override
	public EventType OnSecurityChanged() {
		return terminal.OnSecurityChanged();
	}

	@Override
	public EventType OnSecurityTrade() {
		return terminal.OnSecurityTrade();
	}

	@Override
	public EventType OnPortfolioChanged() {
		return terminal.OnPortfolioChanged();
	}

	@Override
	public EventType OnPositionAvailable() {
		return terminal.OnPositionAvailable();
	}

	@Override
	public EventType OnPositionChanged() {
		return terminal.OnPositionChanged();
	}

	@Override
	public int getSecuritiesCount() {
		return terminal.getSecuritiesCount();
	}

	@Override
	public int getPortfoliosCount() {
		return terminal.getPortfoliosCount();
	}

	@Override
	public int getStopOrdersCount() {
		return terminal.getStopOrdersCount();
	}

	@Override
	public EventType OnStopOrderChanged() {
		return terminal.OnStopOrderChanged();
	}

	@Override
	public EventType OnStopOrderCancelFailed() {
		return terminal.OnStopOrderCancelFailed();
	}

	@Override
	public EventType OnStopOrderCancelled() {
		return terminal.OnStopOrderCancelled();
	}

	@Override
	public EventType OnStopOrderDone() {
		return terminal.OnStopOrderDone();
	}

	@Override
	public EventType OnStopOrderFailed() {
		return terminal.OnStopOrderFailed();
	}

	@Override
	public EventType OnStopOrderRegistered() {
		return terminal.OnStopOrderRegistered();
	}

	@Override
	public EventType OnStopOrderRegisterFailed() {
		return terminal.OnStopOrderRegisterFailed();
	}

	@Override
	public void fireOrderAvailableEvent(Order order) {
		terminal.fireOrderAvailableEvent(order);
	}

	@Override
	public EditableOrder getEditableOrder(long id) throws OrderException {
		return terminal.getEditableOrder(id);
	}

	@Override
	public void registerOrder(EditableOrder order) throws OrderException {
		terminal.registerOrder(order);
	}

	@Override
	public void purgeOrder(EditableOrder order) {
		terminal.purgeOrder(order);
	}

	@Override
	public void purgeOrder(long id) {
		terminal.purgeOrder(id);
	}

	@Override
	public boolean isPendingOrder(long transId) {
		return terminal.isPendingOrder(transId);
	}

	@Override
	public void registerPendingOrder(EditableOrder order)
		throws OrderException
	{
		terminal.registerPendingOrder(order);
	}

	@Override
	public void purgePendingOrder(EditableOrder order) {
		terminal.purgePendingOrder(order);
	}

	@Override
	public void purgePendingOrder(long transId) {
		terminal.purgePendingOrder(transId);
	}

	@Override
	public EditableOrder getPendingOrder(long transId) {
		return terminal.getPendingOrder(transId);
	}

	@Override
	public void fireStopOrderAvailableEvent(Order order) {
		terminal.fireStopOrderAvailableEvent(order);
	}

	@Override
	public EditableOrder getEditableStopOrder(long id)
		throws OrderException
	{
		return terminal.getEditableStopOrder(id);
	}

	@Override
	public void registerStopOrder(EditableOrder order)
		throws OrderException
	{
		terminal.registerStopOrder(order);
	}

	@Override
	public void purgeStopOrder(EditableOrder order) {
		terminal.purgeStopOrder(order);
	}

	@Override
	public void purgeStopOrder(long id) {
		terminal.purgeStopOrder(id);
	}

	@Override
	public boolean isPendingStopOrder(long transId) {
		return terminal.isPendingStopOrder(transId);
	}

	@Override
	public void registerPendingStopOrder(EditableOrder order)
		throws OrderException
	{
		terminal.registerPendingStopOrder(order);
	}

	@Override
	public void purgePendingStopOrder(EditableOrder order) {
		terminal.purgePendingStopOrder(order);
	}

	@Override
	public void purgePendingStopOrder(long transId) {
		terminal.purgePendingStopOrder(transId);
	}

	@Override
	public EditableOrder getPendingStopOrder(long transId) {
		return terminal.getPendingStopOrder(transId);
	}

	@Override
	public void firePortfolioAvailableEvent(Portfolio portfolio) {
		terminal.firePortfolioAvailableEvent(portfolio);
	}

	@Override
	public EditablePortfolio getEditablePortfolio(Account account)
		throws PortfolioException
	{
		return terminal.getEditablePortfolio(account);
	}

	@Override
	public void registerPortfolio(EditablePortfolio portfolio)
		throws PortfolioException
	{
		terminal.registerPortfolio(portfolio);
	}

	@Override
	public void setDefaultPortfolio(EditablePortfolio portfolio) {
		terminal.setDefaultPortfolio(portfolio);
	}

	@Override
	public EditableSecurity getEditableSecurity(SecurityDescriptor descr) {
		return terminal.getEditableSecurity(descr);
	}

	@Override
	public void fireSecurityAvailableEvent(Security security) {
		terminal.fireSecurityAvailableEvent(security);
	}

	@Override
	public EventType OnConnected() {
		return terminal.OnConnected();
	}

	@Override
	public EventType OnDisconnected() {
		return terminal.OnDisconnected();
	}

	@Override
	public void fireTerminalConnectedEvent() {
		terminal.fireTerminalConnectedEvent();
	}

	@Override
	public void fireTerminalDisconnectedEvent() {
		terminal.fireTerminalDisconnectedEvent();
	}

	@Override
	public EditableOrder
		makePendingOrderAsRegisteredIfExists(long transId, long orderId)
			throws OrderException
	{
		return terminal.makePendingOrderAsRegisteredIfExists(transId, orderId);
	}

	@Override
	public EditableOrder
		makePendingStopOrderAsRegisteredIfExists(long transId, long orderId)
			throws OrderException
	{
		return terminal
			.makePendingStopOrderAsRegisteredIfExists(transId, orderId);
	}

	@Override
	public EventType OnStarted() {
		return terminal.OnStarted();
	}

	@Override
	public EventType OnStopped() {
		return terminal.OnStopped();
	}

	@Override
	public EventType OnPanic() {
		return terminal.OnPanic();
	}

	@Override
	public void firePanicEvent(int code, String msgId) {
		terminal.firePanicEvent(code, msgId);
	}

	@Override
	public void firePanicEvent(int code, String msgId, Object[] args) {
		terminal.firePanicEvent(code, msgId, args);
	}

	@Override
	public OrderProcessor getOrderProcessorInstance() {
		return terminal.getOrderProcessorInstance();
	}

	@Override
	public OrderBuilder getOrderBuilderInstance() {
		return terminal.getOrderBuilderInstance();
	}

	@Override
	public EditableSecurities getSecuritiesInstance() {
		return terminal.getSecuritiesInstance();
	}

	@Override
	public EditablePortfolios getPortfoliosInstance() {
		return terminal.getPortfoliosInstance();
	}

	@Override
	public EditableOrders getOrdersInstance() {
		return terminal.getOrdersInstance();
	}

	@Override
	public EditableOrders getStopOrdersInstance() {
		return terminal.getStopOrdersInstance();
	}

	@Override
	public boolean stopped() {
		return terminal.stopped();
	}

	@Override
	public boolean connected() {
		return terminal.connected();
	}

	@Override
	public TerminalState getTerminalState() {
		return terminal.getTerminalState();
	}

	@Override
	public Starter getStarter() {
		return terminal.getStarter();
	}

	@Override
	public void fireTerminalStartedEvent() {
		terminal.fireTerminalStartedEvent();
	}

	@Override
	public void fireTerminalStoppedEvent() {
		terminal.fireTerminalStoppedEvent();
	}

	@Override
	public void setTerminalState(TerminalState state) {
		terminal.setTerminalState(state);
	}

	@Override
	public EventType OnStopOrderFilled() {
		return terminal.OnStopOrderFilled();
	}

	@Override
	public Order createLimitOrderB(Account account, Security sec, long qty,
			double price) throws OrderException
	{
		return terminal.createLimitOrderB(account, sec, qty, price);
	}

	@Override
	public Order createLimitOrderS(Account account, Security sec, long qty,
			double price) throws OrderException
	{
		return terminal.createLimitOrderS(account, sec, qty, price);
	}

	@Override
	public Order createStopLimitB(Account account, Security sec, long qty,
			double stopPrice, double price) throws OrderException
	{
		return terminal.createStopLimitB(account, sec, qty, stopPrice, price);
	}

	@Override
	public Order createStopLimitS(Account account, Security sec, long qty,
			double stopPrice, double price) throws OrderException
	{
		return terminal.createStopLimitS(account, sec, qty, stopPrice, price);
	}

}
