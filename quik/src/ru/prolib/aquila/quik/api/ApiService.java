package ru.prolib.aquila.quik.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.t2q.*;

/**
 * Фасад QUIK API.
 * <p>
 * Скрывает низкоуровневые детали реализации подключения к QUIK.
 * Представляет публичный интерфейс к функциям QUIK2TRANS API
 * и обеспечивает доступ наблюдателей к соответствующим событиям.
 */
public class ApiService {
	private final T2QService service;
	private final ApiServiceHandler handler;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param service реализация сервиса транзакций
	 * @param handler транслятор событий QUIK API
	 */
	public ApiService(T2QService service, ApiServiceHandler handler) {
		super();
		this.service = service;
		this.handler = handler;
	}
	
	/**
	 * Получить реализацию сервиса QUIK API.
	 * <p>
	 * @return сервис
	 */
	public T2QService getService() {
		return service;
	}
	
	/**
	 * Получить транслятор событий QUIK API.
	 * <p>
	 * @return транслятор событий
	 */
	public ApiServiceHandler getHandler() {
		return handler;
	}
	
	/**
	 * Получить тип события: ответ на транзакцию.
	 * <p>
	 * Данный тип события позволяет отслеживать результат обработки транзакции
	 * с указанным номером. Наблюдатели будут получать событие класса
	 * {@link TransEvent}. Отписка всех наблюдателей выполняется автоматически
	 * после получения одного из финальных статусов.
	 * <p>
	 * @param transId номер транзакции
	 * @return тип события
	 */
	public EventType OnTransReply(long transId) {
		return handler.OnTransReply(transId);
	}

	/**
	 * Получить тип события: изменение статуса подключения.
	 * <p>
	 * Наблюдатели будут получать событие класса {@link ConnEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnConnStatus() {
		return handler.OnConnStatus();
	}
	
	/**
	 * Получить тип события: изменение статуса заявки.
	 * <p>
	 * Наблюдатели будут получать событие класса {@link OrderEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderStatus() {
		return handler.OnOrderStatus();
	}
	
	/**
	 * Получить тип события: информация по сделке.
	 * <p>
	 * Наблюдатели будут получать событие класса {@link TradeEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnTradeStatus() {
		return handler.OnTradeStatus();
	}

	/**
	 * Подключиться к терминалу.
	 * <p>
	 * @param path путь к каталогу с QUIK
	 * @throws ApiServiceException
	 */
	public void connect(String path) throws ApiServiceException {
		try {
			service.connect(path);
		} catch ( T2QException e ) {
			throw new ApiServiceException(e);
		}
	}

	/**
	 * Отключиться от терминала.
	 */
	public void disconnect() {
		service.disconnect();
	}

	/**
	 * Отправить транзакцию на выполнение.
	 * <p>
	 * @param str строка транзакции
	 * @throws ApiServiceException
	 */
	public void send(String str) throws ApiServiceException {
		try {
			service.send(str);
		} catch ( T2QException e ) {
			throw new ApiServiceException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == ApiService.class ) {
			ApiService o = (ApiService) other;
			return new EqualsBuilder()
				.append(service, o.service)
				.append(handler, o.handler)
				.isEquals();
		} else {
			return false;
		}
	}
	
	/**
	 * Делегат к {@link ApiServiceHandler#getPendingTransactionCount()}.
	 * <p>
	 * @return количество ожидающих транзакций
	 */
	public int getPendingTransactionCount() {
		return handler.getPendingTransactionCount();
	}

}
