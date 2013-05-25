package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.Date;
import java.util.List;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;

/**
 * Декоратор терминала.
 * <p>
 * 2012-08-17<br>
 * $Id: TerminalDecorator.java 513 2013-02-11 01:17:18Z whirlwind $
 */
@Deprecated
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
	public final void setTerminal(EditableTerminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * Получить декорированный терминал.
	 * <p>
	 * @return терминал или null, если терминал не задан
	 */
	public final EditableTerminal getTerminal() {
		return terminal;
	}

	@Override
	public  final List<Security> getSecurities() {
		return terminal.getSecurities();
	}

	@Override
	public final Security getSecurity(SecurityDescriptor descr)
		throws SecurityException
	{
		return terminal.getSecurity(descr);
	}

	@Override
	public final boolean isSecurityExists(SecurityDescriptor descr) {
		return terminal.isSecurityExists(descr);
	}

	@Override
	public  EventType OnSecurityAvailable() {
		return terminal.OnSecurityAvailable();
	}

	@Override
	public final boolean isPortfolioAvailable(Account account) {
		return terminal.isPortfolioAvailable(account);
	}

	@Override
	public  EventType OnPortfolioAvailable() {
		return terminal.OnPortfolioAvailable();
	}

	@Override
	public final List<Portfolio> getPortfolios() {
		return terminal.getPortfolios();
	}

	@Override
	public final Portfolio getPortfolio(Account account)
			throws PortfolioException
	{
		return terminal.getPortfolio(account);
	}

	@Override
	public final void start() throws StarterException {
		terminal.start();
	}

	@Override
	public final boolean started() {
		return terminal.started();
	}

	@Override
	public final void stop() throws StarterException {
		terminal.stop();
	}

	@Override
	public final Portfolio getDefaultPortfolio() throws PortfolioException {
		return terminal.getDefaultPortfolio();
	}

	@Override
	public final boolean isOrderExists(long id) {
		return terminal.isOrderExists(id);
	}

	@Override
	public final List<Order> getOrders() {
		return terminal.getOrders();
	}

	@Override
	public final Order getOrder(long id) throws OrderException {
		return terminal.getOrder(id);
	}

	@Override
	public final EventType OnOrderAvailable() {
		return terminal.OnOrderAvailable();
	}

	@Override
	public final boolean isStopOrderExists(long id) {
		return terminal.isStopOrderExists(id);
	}

	@Override
	public final List<Order> getStopOrders() {
		return terminal.getStopOrders();
	}

	@Override
	public final Order getStopOrder(long id) throws OrderException {
		return terminal.getStopOrder(id);
	}

	@Override
	public final EventType OnStopOrderAvailable() {
		return terminal.OnStopOrderAvailable();
	}

	@Override
	public final Order
		createMarketOrderB(Account account, Security sec, long qty)
			throws OrderException
	{
		return terminal.createMarketOrderB(account, sec, qty);
	}

	@Override
	public final Order
		createMarketOrderS(Account account, Security sec, long qty)
			throws OrderException
	{
		return terminal.createMarketOrderS(account, sec, qty);
	}

	@Override
	public final void placeOrder(Order order) throws OrderException {
		terminal.placeOrder(order);
	}

	@Override
	public final void cancelOrder(Order order) throws OrderException {
		terminal.cancelOrder(order);
	}

	@Override
	public final int getOrdersCount() {
		return terminal.getOrdersCount();
	}

	@Override
	public final EventType OnOrderCancelFailed() {
		return terminal.OnOrderCancelFailed();
	}

	@Override
	public final EventType OnOrderCancelled() {
		return terminal.OnOrderCancelled();
	}

	@Override
	public final EventType OnOrderChanged() {
		return terminal.OnOrderChanged();
	}

	@Override
	public final EventType OnOrderDone() {
		return terminal.OnOrderDone();
	}

	@Override
	public final EventType OnOrderFailed() {
		return terminal.OnOrderFailed();
	}

	@Override
	public final EventType OnOrderFilled() {
		return terminal.OnOrderFilled();
	}

	@Override
	public final EventType OnOrderPartiallyFilled() {
		return terminal.OnOrderPartiallyFilled();
	}

	@Override
	public final EventType OnOrderRegistered() {
		return terminal.OnOrderRegistered();
	}

	@Override
	public final EventType OnOrderRegisterFailed() {
		return terminal.OnOrderRegisterFailed();
	}

	@Override
	public final EventType OnSecurityChanged() {
		return terminal.OnSecurityChanged();
	}

	@Override
	public final EventType OnSecurityTrade() {
		return terminal.OnSecurityTrade();
	}

	@Override
	public final EventType OnPortfolioChanged() {
		return terminal.OnPortfolioChanged();
	}

	@Override
	public final EventType OnPositionAvailable() {
		return terminal.OnPositionAvailable();
	}

	@Override
	public final EventType OnPositionChanged() {
		return terminal.OnPositionChanged();
	}

	@Override
	public final int getSecuritiesCount() {
		return terminal.getSecuritiesCount();
	}

	@Override
	public final int getPortfoliosCount() {
		return terminal.getPortfoliosCount();
	}

	@Override
	public final int getStopOrdersCount() {
		return terminal.getStopOrdersCount();
	}

	@Override
	public final EventType OnStopOrderChanged() {
		return terminal.OnStopOrderChanged();
	}

	@Override
	public final EventType OnStopOrderCancelFailed() {
		return terminal.OnStopOrderCancelFailed();
	}

	@Override
	public final EventType OnStopOrderCancelled() {
		return terminal.OnStopOrderCancelled();
	}

	@Override
	public final EventType OnStopOrderDone() {
		return terminal.OnStopOrderDone();
	}

	@Override
	public final EventType OnStopOrderFailed() {
		return terminal.OnStopOrderFailed();
	}

	@Override
	public final EventType OnStopOrderRegistered() {
		return terminal.OnStopOrderRegistered();
	}

	@Override
	public final EventType OnStopOrderRegisterFailed() {
		return terminal.OnStopOrderRegisterFailed();
	}

	@Override
	public final void fireOrderAvailableEvent(Order order) {
		terminal.fireOrderAvailableEvent(order);
	}

	@Override
	public final EditableOrder getEditableOrder(long id)
		throws OrderNotExistsException
	{
		return terminal.getEditableOrder(id);
	}

	@Override
	public final void registerOrder(long orderId, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		terminal.registerOrder(orderId, order);
	}

	@Override
	public final void purgeOrder(long id) {
		terminal.purgeOrder(id);
	}

	@Override
	public final boolean isPendingOrder(long transId) {
		return terminal.isPendingOrder(transId);
	}

	@Override
	public final void registerPendingOrder(long orderId, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		terminal.registerPendingOrder(orderId, order);
	}

	@Override
	public final void purgePendingOrder(long transId) {
		terminal.purgePendingOrder(transId);
	}

	@Override
	public final EditableOrder getPendingOrder(long transId)
		throws OrderNotExistsException
	{
		return terminal.getPendingOrder(transId);
	}

	@Override
	public final void fireStopOrderAvailableEvent(Order order) {
		terminal.fireStopOrderAvailableEvent(order);
	}

	@Override
	public final EditableOrder getEditableStopOrder(long id)
		throws OrderNotExistsException
	{
		return terminal.getEditableStopOrder(id);
	}

	@Override
	public final void registerStopOrder(long orderId, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		terminal.registerStopOrder(orderId, order);
	}

	@Override
	public final void purgeStopOrder(long id) {
		terminal.purgeStopOrder(id);
	}

	@Override
	public final boolean isPendingStopOrder(long transId) {
		return terminal.isPendingStopOrder(transId);
	}

	@Override
	public final void registerPendingStopOrder(long transId, EditableOrder order)
		throws OrderAlreadyExistsException
	{
		terminal.registerPendingStopOrder(transId, order);
	}

	@Override
	public final void purgePendingStopOrder(long transId) {
		terminal.purgePendingStopOrder(transId);
	}

	@Override
	public final EditableOrder getPendingStopOrder(long transId)
		throws OrderNotExistsException
	{
		return terminal.getPendingStopOrder(transId);
	}

	@Override
	public final void firePortfolioAvailableEvent(Portfolio portfolio) {
		terminal.firePortfolioAvailableEvent(portfolio);
	}

	@Override
	public final EditablePortfolio getEditablePortfolio(Account account)
		throws PortfolioException
	{
		return terminal.getEditablePortfolio(account);
	}

	@Override
	public final EditablePortfolio createPortfolio(Account account)
		throws PortfolioException
	{
		return terminal.createPortfolio(this, account);
	}
	
	@Override
	public final EditablePortfolio
		createPortfolio(EditableTerminal terminal, Account account)
			throws PortfolioException
	{
		return this.terminal.createPortfolio(terminal, account);
	}

	@Override
	public final void setDefaultPortfolio(EditablePortfolio portfolio) {
		terminal.setDefaultPortfolio(portfolio);
	}

	@Override
	public final EditableSecurity
		getEditableSecurity(SecurityDescriptor descr) throws SecurityNotExistsException
	{
		return terminal.getEditableSecurity(descr);
	}

	@Override
	public void fireSecurityAvailableEvent(Security security) {
		terminal.fireSecurityAvailableEvent(security);
	}

	@Override
	public final EventType OnConnected() {
		return terminal.OnConnected();
	}

	@Override
	public final EventType OnDisconnected() {
		return terminal.OnDisconnected();
	}

	@Override
	public final void fireTerminalConnectedEvent() {
		terminal.fireTerminalConnectedEvent();
	}

	@Override
	public final void fireTerminalDisconnectedEvent() {
		terminal.fireTerminalDisconnectedEvent();
	}

	@Override
	public final EditableOrder movePendingOrder(long transId, long orderId)
			throws OrderException
	{
		return terminal.movePendingOrder(transId, orderId);
	}

	@Override
	public final EditableOrder movePendingStopOrder(long transId, long orderId)
			throws OrderException
	{
		return terminal.movePendingStopOrder(transId, orderId);
	}

	@Override
	public final EventType OnStarted() {
		return terminal.OnStarted();
	}

	@Override
	public final EventType OnStopped() {
		return terminal.OnStopped();
	}

	@Override
	public final EventType OnPanic() {
		return terminal.OnPanic();
	}

	@Override
	public final void firePanicEvent(int code, String msgId) {
		terminal.firePanicEvent(code, msgId);
	}

	@Override
	public final void firePanicEvent(int code, String msgId, Object[] args) {
		terminal.firePanicEvent(code, msgId, args);
	}

	@Override
	public final OrderProcessor getOrderProcessorInstance() {
		return terminal.getOrderProcessorInstance();
	}

	@Override
	public final EditableSecurities getSecuritiesInstance() {
		return terminal.getSecuritiesInstance();
	}

	@Override
	public final EditablePortfolios getPortfoliosInstance() {
		return terminal.getPortfoliosInstance();
	}

	@Override
	public final EditableOrders getOrdersInstance() {
		return terminal.getOrdersInstance();
	}

	@Override
	public final EditableOrders getStopOrdersInstance() {
		return terminal.getStopOrdersInstance();
	}

	@Override
	public final boolean stopped() {
		return terminal.stopped();
	}

	@Override
	public final boolean connected() {
		return terminal.connected();
	}

	@Override
	public final TerminalState getTerminalState() {
		return terminal.getTerminalState();
	}

	@Override
	public final Starter getStarter() {
		return terminal.getStarter();
	}

	@Override
	public final void fireTerminalStartedEvent() {
		terminal.fireTerminalStartedEvent();
	}

	@Override
	public final void fireTerminalStoppedEvent() {
		terminal.fireTerminalStoppedEvent();
	}

	@Override
	public final void setTerminalState(TerminalState state) {
		terminal.setTerminalState(state);
	}

	@Override
	public final EventType OnStopOrderFilled() {
		return terminal.OnStopOrderFilled();
	}

	@Override
	public final Order createLimitOrderB(Account account, Security sec,
			long qty, double price) throws OrderException
	{
		return terminal.createLimitOrderB(account, sec, qty, price);
	}

	@Override
	public final Order createLimitOrderS(Account account, Security sec,
			long qty, double price) throws OrderException
	{
		return terminal.createLimitOrderS(account, sec, qty, price);
	}

	@Override
	public final Order createStopLimitB(Account account, Security sec,
			long qty, double stopPrice, double price) throws OrderException
	{
		return terminal.createStopLimitB(account, sec, qty, stopPrice, price);
	}

	@Override
	public final Order createStopLimitS(Account account, Security sec,
			long qty, double stopPrice, double price) throws OrderException
	{
		return terminal.createStopLimitS(account, sec, qty, stopPrice, price);
	}

	@Override
	public final Date getCurrentTime() {
		return terminal.getCurrentTime();
	}

	@Override
	public final EventSystem getEventSystem() {
		return terminal.getEventSystem();
	}

	@Override
	public final EditableSecurity
		createSecurity(EditableTerminal terminal, SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		return terminal.createSecurity(terminal, descr);
	}

	@Override
	public final EditableSecurity createSecurity(SecurityDescriptor descr)
			throws SecurityAlreadyExistsException
	{
		return terminal.createSecurity(this, descr);
	}

	@Override
	public final void setOrderProcessorInstance(OrderProcessor processor) {
		terminal.setOrderProcessorInstance(processor);
	}

	@Override
	public final boolean hasPendingOrders() {
		return terminal.hasPendingOrders();
	}

	@Override
	public final EditableOrder createOrder(EditableTerminal terminal) {
		return this.terminal.createOrder(terminal);
	}

	@Override
	public final boolean hasPendingStopOrders() {
		return terminal.hasPendingStopOrders();
	}

	@Override
	public final EditableOrder createStopOrder(EditableTerminal terminal) {
		return this.terminal.createStopOrder(terminal);
	}

	@Override
	public final EditableOrder createOrder() {
		return terminal.createOrder(this);
	}

	@Override
	public final EditableOrder createStopOrder() {
		return terminal.createStopOrder(this);
	}

	@Override
	public final EventType OnOrderTrade() {
		return terminal.OnOrderTrade();
	}

}
