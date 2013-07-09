package ru.prolib.aquila.ib.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.Order;

/**
 * Облегченный клиент IB API.
 */
public class IBClient {
	private final EClientSocket socket;
	private final IBWrapper wrapper;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param socket сокет
	 * @param wrapper обработчик
	 */
	IBClient(EClientSocket socket, IBWrapper wrapper) {
		super();
		this.socket = socket;
		this.wrapper = wrapper;
	}
	
	/**
	 * Служебный Конструктор.
	 * <p>
	 * @param wrapper обработчик
	 */
	private IBClient(IBWrapper wrapper) {
		this(new EClientSocket(wrapper), wrapper);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param requestId нумератор запросов
	 */
	public IBClient() {
		this(new IBWrapper());
	}
	
	public IBWrapper getWrapper() {
		return wrapper;
	}
	
	public EClientSocket getSocket() {
		return socket;
	}
	
	/**
	 * Установить базовый обработчик данных.
	 * <p>
	 * @param handler обработчик данных
	 */
	public void setMainHandler(MainHandler handler) {
		wrapper.setMainHandler(handler);
	}
	
	/**
	 * Получить базовый обработчик данных.
	 * <p>
	 * @return текущий экземпляр обработчика
	 */
	public MainHandler getMainHandler() {
		return wrapper.getMainHandler();
	}
	
	/**
	 * Установить обработчик данных контракта.
	 * <p>
	 * @param reqId номер запроса
	 * @param handler обработчик данных
	 */
	public void setContractHandler(int reqId, ContractHandler handler) {
		wrapper.setContractHandler(reqId, handler);
	}
	
	/**
	 * Установить обработчик данных заявки.
	 * <p>
	 * @param reqId номер запроса
	 * @param handler обработчик данных
	 */
	public void setOrderHandler(int reqId, OrderHandler handler) {
		wrapper.setOrderHandler(reqId, handler);
	}
	
	/**
	 * Удалить обработчик данных.
	 * <p>
	 * @param reqId номер запроса
	 */
	public void removeHandler(int reqId) {
		wrapper.removeHandler(reqId);
	}
	
	/**
	 * Установить соединение с IB API.
	 * <p>
	 * Если соединение уже установлено, то ничего не делает.
	 * <p>
	 * @param config конфигурация соединения
	 */
	public void connect(IBConfig config) {
		if ( ! socket.isConnected() ) {
			socket.eConnect(config.getHost(), config.getPort(),
					config.getClientId());
			if ( socket.isConnected() ) {
				wrapper.connectionOpened();
			}
		}
	}
	
	/**
	 * Разорвать соединение с IB API.
	 * <p>
	 * Если соединение не установлено, то ничего не делает.
	 */
	public void disconnect() {
		if ( socket.isConnected() ) {
			socket.eDisconnect();
			wrapper.connectionClosed();
		}
	}
	
	/**
	 * Проверить соединение.
	 * <p>
	 * @return true - соединение установлено, false - не установлено
	 */
	public boolean connected() {
		return socket.isConnected();
	}
	
	public void reqAutoOpenOrders(boolean autoBind) {
		socket.reqAutoOpenOrders(autoBind);
	}
	
	public void reqOpenOrders() {
		socket.reqOpenOrders();
	}
	
	public void reqAllOpenOrders() {
		socket.reqAllOpenOrders();
	}
	
	public void reqAccountUpdates(boolean subscribe, String accountName) {
		socket.reqAccountUpdates(subscribe, accountName);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != IBClient.class ) {
			return false;
		}
		IBClient o = (IBClient) other;
		return new EqualsBuilder()
			.append(o.wrapper, wrapper)
			.isEquals();
	}
	
	public void reqContractDetails(int reqId, Contract contract) {
		socket.reqContractDetails(reqId, contract);
	}
	
	public void reqMktData(int reqId, Contract contract,
			String genericTickList, boolean snapshot)
	{
		socket.reqMktData(reqId, contract, genericTickList, snapshot);
	}
	
	public void placeOrder(int reqId, Contract contract, Order order) {
		socket.placeOrder(reqId, contract, order);
	}
	
	public void cancelOrder(int reqId) {
		socket.cancelOrder(reqId);
	}
	
	public ContractHandler getContractHandler(int reqId) {
		return wrapper.getContractHandler(reqId);
	}
	
	public OrderHandler getOrderHandler(int reqId) {
		return wrapper.getOrderHandler(reqId);
	}

}
