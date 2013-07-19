package ru.prolib.aquila.quik.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.t2q.*;
import ru.prolib.aquila.t2q.jqt.JQTServiceFactory;

/**
 * Клиент QUIK API.
 */
public class QUIKClient {
	private final T2QService apiService;
	private final QUIKWrapper apiHandler;
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param apiService сервис
	 * @param handler обработчик
	 */
	QUIKClient(T2QService apiService, QUIKWrapper handler) {
		super();
		this.apiService = apiService;
		this.apiHandler = handler;
	}
	
	/**
	 * Служебный конструктор.
	 * <p>
	 * @param handler обработчик
	 */
	private QUIKClient(QUIKWrapper handler) {
		this(new JQTServiceFactory().createService(handler), handler);
	}
	
	/**
	 * Конструктор.
	 */
	public QUIKClient() {
		this(new QUIKWrapper());
	}
	
	T2QService getService() {
		return apiService;
	}
	
	QUIKWrapper getWrapper() {
		return apiHandler;
	}
	
	/**
	 * Установить обработчик транзакции.
	 * <p>
	 * @param transId номер транзакции 
	 * @param handler обработчик транзакции
	 */
	public void setHandler(int transId, QUIKTransactionHandler handler) {
		apiHandler.setHandler(transId, handler);
	}
	
	/**
	 * Удалить обработчик транзакции.
	 * <p>
	 * @param transId номер транзакции
	 */
	public void removeHandler(int transId) {
		apiHandler.removeHandler(transId);
	}
	
	/**
	 * Установить базовый обработчик данных.
	 * <p>
	 * @param handler обработчик
	 */
	public void setMainHandler(QUIKMainHandler handler) {
		apiHandler.setMainHandler(handler);
	}
	
	/**
	 * Инициировать подключение к QUIK API.
	 * <p>
	 * @param path путь к каталогу с QUIK
	 * @throws T2QException
	 */
	public void connect(String path) throws T2QException {
		apiService.connect(path);
	}
	
	/**
	 * Разорвать соединение с QUIK API.
	 */
	public void disconnect() {
		apiService.disconnect();
	}

	/**
	 * Отправить транзакцию в торговую систему.
	 * <p>
	 * @param str строка транзакции
	 * @throws T2QException
	 */
	public void send(String str) throws T2QException {
		apiService.send(str);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QUIKClient.class ) {
			return false;
		}
		QUIKClient o = (QUIKClient) other;
		return new EqualsBuilder()
			.append(o.apiHandler, apiHandler)
			.isEquals();
	}

}
