package ru.prolib.aquila.ib.subsys.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.IBException;
import ru.prolib.aquila.ib.event.IBEvent;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.Order;

/**
 * Обертка клиента.
 * <p>
 * 2012-11-19<br>
 * $Id: IBClientImpl.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class IBClientImpl implements IBClient, EventListener {
	@SuppressWarnings("unused")
	private static final Logger logger;
	private final EClientSocket socket;
	private final IBApiEventDispatcher wrapper;
	private final EventDispatcher dispatcher;
	private final EventType onConnectionClosed;
	private final EventType onConnectionOpened;
	
	static {
		logger = LoggerFactory.getLogger(IBClientImpl.class);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param socket клиентское подключение
	 * @param wrapper диспетчер ответов от IB API
	 * @param dispatcher диспетчер событий
	 * @param onConnectionOpened тип события при открытии соединения
	 * @param onConnectionClosed тип события при закрытии соединения
	 */
	public IBClientImpl(EClientSocket socket, IBApiEventDispatcher wrapper,
			EventDispatcher dispatcher, EventType onConnectionOpened,
			EventType onConnectionClosed) {
		super();
		this.socket = socket;
		this.wrapper = wrapper;
		this.dispatcher = dispatcher;
		this.onConnectionOpened = onConnectionOpened;
		this.onConnectionClosed = onConnectionClosed;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить библиотечный экземпляр подключения.
	 * <p>
	 * @return подключение
	 */
	public EClientSocket getSocket() {
		return socket;
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер
	 */
	public IBApiEventDispatcher getApiEventDispatcher() {
		return wrapper;
	}
	
	/**
	 * Проверить подключение.
	 */
	private void checkConnected() throws IBException {
		if ( ! socket.isConnected() ) {
			throw new IBException("Not connected");
		}
	}

	@Override
	public boolean isConnected() {
		return socket.isConnected();
	}

	@Override
	public void eConnect(String host, int port, int clientId)
			throws IBException
	{
		socket.eConnect(host, port, clientId);
		if ( ! socket.isConnected() ) {
			throw new IBException("Not connected, TODO: pop last error");
		}
		dispatcher.dispatch(new EventImpl(onConnectionOpened));
	}

	@Override
	public void eDisconnect() {
		socket.eDisconnect();
	}

	@Override
	public void reqContractDetails(int reqId, Contract contract)
			throws IBException
	{
		checkConnected();
		socket.reqContractDetails(reqId, contract);
	}
	
	@Override
	public void reqAccountUpdates(boolean subscribe, String account)
			throws IBException
	{
		if ( isConnected() ) {
			socket.reqAccountUpdates(subscribe, account);
		} else if ( subscribe ) {
			// Если не подключено и запрос на подписку -> исключение
			throw new IBException("Not connected");
		}
	}

	@Override
	public EventType OnConnectionClosed() {
		wrapper.OnConnectionClosed().addListener(this);
		wrapper.OnError().addListener(this);
		return onConnectionClosed;
	}

	@Override
	public EventType OnError() {
		return wrapper.OnError();
	}

	@Override
	public EventType OnNextValidId() {
		return wrapper.OnNextValidId();
	}

	@Override
	public EventType OnContractDetails() {
		return wrapper.OnContractDetails();
	}

	@Override
	public EventType OnManagedAccounts() {
		return wrapper.OnManagedAccounts();
	}

	@Override
	public EventType OnUpdateAccount() {
		return wrapper.OnUpdateAccount();
	}

	@Override
	public EventType OnUpdatePortfolio() {
		return wrapper.OnUpdatePortfolio();
	}

	@Override
	public void reqManagedAccts() throws IBException {
		checkConnected();
		socket.reqManagedAccts();
	}

	@Override
	public EventType OnOpenOrder() {
		return wrapper.OnOpenOrder();
	}

	@Override
	public EventType OnOrderStatus() {
		return wrapper.OnOrderStatus();
	}

	@Override
	public void placeOrder(int orderId, Contract contract, Order order)
			throws IBException
	{
		checkConnected();
		socket.placeOrder(orderId, contract, order);
	}

	@Override
	public void cancelOrder(int orderId)
			throws IBException
	{
		checkConnected();
		socket.cancelOrder(orderId);
	}

	@Override
	public void reqMktData(int tickerId, Contract contract,
			String genericTickList, boolean snapshot)
					throws IBException
	{
		checkConnected();
		socket.reqMktData(tickerId, contract, genericTickList, snapshot);
	}

	@Override
	public void cancelMktData(int tickerId) {
		if ( socket.isConnected() ) {
			socket.cancelMktData(tickerId);
		}
	}

	@Override
	public EventType OnTick() {
		return wrapper.OnTick();
	}

	@Override
	public void reqAutoOpenOrders(boolean bAutoBind) throws IBException {
		if ( isConnected() ) {
			socket.reqAutoOpenOrders(bAutoBind);
		} else if ( bAutoBind ) {
			throw new IBException("Not connected");
		}
	}

	@Override
	public EventType OnConnectionOpened() {
		return onConnectionOpened;
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(wrapper.OnConnectionClosed()) ||
		    (event.isType(wrapper.OnError()) && !socket.isConnected()) ) {
			dispatcher.dispatch(new IBEvent(onConnectionClosed));
		}
	}

}
