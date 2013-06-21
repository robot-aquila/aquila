package ru.prolib.aquila.ib.api;


import ru.prolib.aquila.core.utils.Counter;

import com.ib.client.EClientSocket;

/**
 * Облегченный клиент IB API.
 */
public class IBClient {
	private final EClientSocket socket;
	private final IBWrapper wrapper;
	private final Counter requestId;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param socket сокет
	 * @param wrapper обработчик
	 * @param requestId нумератор запросов
	 */
	IBClient(EClientSocket socket, IBWrapper wrapper, Counter requestId) {
		super();
		this.socket = socket;
		this.wrapper = wrapper;
		this.requestId = requestId;
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
	
	/**
	 * Получить очередной номер запроса.
	 * <p>
	 * Каждый вызов приводит к инкременту нумератора запросов.
	 * <p>
	 * @return номер запроса
	 */
	public int nextReqId() {
		return requestId.getAndIncrement();
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

}
