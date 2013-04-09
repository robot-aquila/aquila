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
	protected final T2QService service;
	protected final EventTypeMap<Long> onTransReplyMap;
	private final EventType onConnStatus, onOrderStatus, onTradeStatus;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param service реализация сервиса транзакций
	 * @param onTransReplyMap карта типов событий для отслеживания транзакций
	 * @param onConnStatus тип события смены статуса подключения
	 * @param onOrderStatus тип события смены статуса заявки
	 * @param onTradeStatus тип события информация о сделке
	 */
	public ApiService(T2QService service,
			EventTypeMap<Long> onTransReplyMap, EventType onConnStatus,
			EventType onOrderStatus, EventType onTradeStatus)
	{
		super();
		this.service = service;
		this.onTransReplyMap = onTransReplyMap;
		this.onConnStatus = onConnStatus;
		this.onOrderStatus = onOrderStatus;
		this.onTradeStatus = onTradeStatus;
	}
	
	/**
	 * Получить тип события: ответ на транзакцию.
	 * <p>
	 * Данный тип события позволяет отслеживать результат обработки транзакции
	 * с указанным номером. Наблюдатели будут получать событие класса
	 * {@link TransEvent}.
	 * <p>
	 * @param transId номер транзакции
	 * @return тип события
	 */
	public EventType OnTransactionReply(long transId) {
		return onTransReplyMap.get(transId);
	}

	/**
	 * Получить тип события: изменение статуса подключения.
	 * <p>
	 * Наблюдатели будут получать событие класса {@link ConnEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnConnectionStatus() {
		return onConnStatus;
	}
	
	/**
	 * Получить тип события: изменение статуса заявки.
	 * <p>
	 * Наблюдатели будут получать событие класса {@link OrderEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnOrderStatus() {
		return onOrderStatus;
	}
	
	/**
	 * Получить тип события: информация по сделке.
	 * <p>
	 * Наблюдатели будут получать событие класса {@link TradeEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnTradeStatus() {
		return onTradeStatus;
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
				.append(onTransReplyMap, o.onTransReplyMap)
				.append(onConnStatus, o.onConnStatus)
				.append(onOrderStatus, o.onOrderStatus)
				.append(onTradeStatus, o.onTradeStatus)
				.isEquals();
		} else {
			return false;
		}
	}

}
