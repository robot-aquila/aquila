package ru.prolib.aquila.core.BusinessEntities.utils;

import java.util.List;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.utils.Counter;

@Deprecated
public class TerminalDecorator implements EditableTerminal {
	private final EditableTerminal terminal;
	
	public TerminalDecorator(EditableTerminal terminal) {
		this.terminal = terminal;
	}
	
	/**
	 * Get decorated terminal.
	 * <p>
	 * @return the decorated terminal
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}

	@Override
	public boolean started() {
		return terminal.started();
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
	public EventType OnConnected() {
		return terminal.OnConnected();
	}

	@Override
	public EventType OnDisconnected() {
		return terminal.OnDisconnected();
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
	public EventType OnReady() {
		return terminal.OnReady();
	}

	@Override
	public EventType OnUnready() {
		return terminal.OnUnready();
	}

	@Override
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, double price)
	{
		return terminal.createOrder(account, dir, security, qty, price);
	}

	@Override
	public Order createOrder(Account account, Direction dir, Security security,
			long qty)
	{
		return terminal.createOrder(account, dir, security, qty);
	}

	@Override
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, double price, OrderActivator activator)
	{
		return terminal.createOrder(account, dir, security, qty, price, activator);
	}

	@Override
	public Order createOrder(Account account, Direction dir, Security security,
			long qty, OrderActivator activator)
	{
		return terminal.createOrder(account, dir, security, qty, activator);
	}

	@Override
	public void requestSecurity(Symbol symbol) {
		terminal.requestSecurity(symbol);
	}

	@Override
	public EventType OnRequestSecurityError() {
		return terminal.OnRequestSecurityError();
	}

	@Override
	public boolean isOrderExists(int id) {
		return terminal.isOrderExists(id);
	}

	@Override
	public List<Order> getOrders() {
		return terminal.getOrders();
	}

	@Override
	public int getOrdersCount() {
		return terminal.getOrdersCount();
	}

	@Override
	public Order getOrder(int id) throws OrderException {
		return terminal.getOrder(id);
	}

	@Override
	public EventType OnOrderAvailable() {
		return terminal.OnOrderAvailable();
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
	public EventType OnOrderTrade() {
		return terminal.OnOrderTrade();
	}

	@Override
	public boolean isPortfolioAvailable(Account account) {
		return terminal.isPortfolioAvailable(account);
	}

	@Override
	public EventType OnPortfolioAvailable() {
		return terminal.OnPortfolioAvailable();
	}

	@Override
	public List<Portfolio> getPortfolios() {
		return terminal.getPortfolios();
	}

	@Override
	public Portfolio getPortfolio(Account account) throws PortfolioException {
		return terminal.getPortfolio(account);
	}

	@Override
	public Portfolio getDefaultPortfolio() throws PortfolioException {
		return terminal.getDefaultPortfolio();
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
	public int getPortfoliosCount() {
		return terminal.getPortfoliosCount();
	}

	@Override
	public List<Security> getSecurities() {
		return terminal.getSecurities();
	}

	@Override
	public Security getSecurity(Symbol symbol) throws SecurityException {
		return terminal.getSecurity(symbol);
	}

	@Override
	public boolean isSecurityExists(Symbol symbol) {
		return terminal.isSecurityExists(symbol);
	}

	@Override
	public EventType OnSecurityAvailable() {
		return terminal.OnSecurityAvailable();
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
	public int getSecuritiesCount() {
		return terminal.getSecuritiesCount();
	}

	@Override
	public void start() throws StarterException {
		terminal.start();
	}

	@Override
	public void stop() throws StarterException {
		terminal.stop();
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
	public DateTime getCurrentTime() {
		return terminal.getCurrentTime();
	}

	@Override
	public TaskHandler schedule(Runnable task, DateTime time) {
		return terminal.schedule(task, time);
	}

	@Override
	public TaskHandler schedule(Runnable task, DateTime firstTime,
			long period)
	{
		return terminal.schedule(task, firstTime, period);
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay) {
		return terminal.schedule(task, delay);
	}

	@Override
	public TaskHandler schedule(Runnable task, long delay, long period) {
		return terminal.schedule(task, delay, period);
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, DateTime firstTime,
			long period)
	{
		return terminal.scheduleAtFixedRate(task, firstTime, period);
	}

	@Override
	public TaskHandler scheduleAtFixedRate(Runnable task, long delay,
			long period)
	{
		return terminal.scheduleAtFixedRate(task, delay, period);
	}

	@Override
	public void cancel(Runnable task) {
		terminal.cancel(task);
	}

	@Override
	public boolean scheduled(Runnable task) {
		return terminal.scheduled(task);
	}

	@Override
	public TaskHandler getTaskHandler(Runnable task) {
		return terminal.getTaskHandler(task);
	}

	@Override
	public EventSystem getEventSystem() {
		return terminal.getEventSystem();
	}

	@Override
	public void markTerminalConnected() {
		terminal.markTerminalConnected();
	}

	@Override
	public void markTerminalDisconnected() {
		terminal.markTerminalDisconnected();
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
	public OrderProcessor getOrderProcessor() {
		return terminal.getOrderProcessor();
	}

	@Override
	public StarterQueue getStarter() {
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
	public void fireTerminalReady() {
		terminal.fireTerminalReady();
	}

	@Override
	public void fireTerminalUnready() {
		terminal.fireTerminalUnready();
	}

	@Override
	public void setTerminalState(TerminalState state) {
		terminal.setTerminalState(state);
	}

	@Override
	public EditableSecurity getEditableSecurity(Symbol symbol) {
		return terminal.getEditableSecurity(symbol);
	}

	@Override
	public EditablePortfolio getEditablePortfolio(Account account) {
		return terminal.getEditablePortfolio(account);
	}

	@Override
	public EditableOrder createOrder() {
		return terminal.createOrder();
	}

	@Override
	public void fireSecurityRequestError(Symbol symbol, int errorCode, String errorMsg) {
		terminal.fireSecurityRequestError(symbol, errorCode, errorMsg);
	}

	@Override
	public void fireEvents(EditableSecurity security) {
		terminal.fireEvents(security);
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
	public void fireEvents(EditableOrder order) {
		terminal.fireEvents(order);
	}

	@Override
	public EditableOrder getEditableOrder(int id)
			throws OrderNotExistsException
	{
		return terminal.getEditableOrder(id);
	}

	@Override
	public void purgeOrder(int id) {
		terminal.purgeOrder(id);
	}

	@Override
	public void fireEvents(EditablePortfolio portfolio) {
		terminal.fireEvents(portfolio);
	}

	@Override
	public void setDefaultPortfolio(EditablePortfolio portfolio) {
		terminal.setDefaultPortfolio(portfolio);
	}

	@Override
	public Counter getOrderIdSequence() {
		return terminal.getOrderIdSequence();
	}

}
