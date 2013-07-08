package ru.prolib.aquila.core.BusinessEntities;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Абстрактная транзакция.
 * <p>
 * Транзакция инкапсулирует служебную информацию, связанную с исполнением
 * запросов в удаленной системе. Специфические реализации терминалов используют
 * транзакции для сохранения принципиально-важной информации, необходимой
 * для последующего обслуживания объектов, статистических данных и отладочной
 * информации. Данный класс определяет форму, но не содержание транзакции.
 * Специфические терминалы реализуют собственные классы для представления
 * объектов запроса и ответа. 
 */
public class Transaction {
	private Object request, response;
	private Date requestTime, responseTime;
	
	/**
	 * Конструктор.
	 */
	public Transaction() {
		super();
	}
	
	/**
	 * Получить информацию о запросе.
	 * <p>
	 * @return объект-информация о запросе
	 */
	public synchronized Object getRequest() {
		return request;
	}
	
	/**
	 * Установить информацию о запросе.
	 * <p>
	 * @param request объект-информация о запросе
	 */
	public synchronized void setRequest(Object request) {
		this.request = request;
	}
	
	/**
	 * Получить время отправки запроса.
	 * <p>
	 * @return время отправки запроса
	 */
	public synchronized Date getRequestTime() {
		return requestTime;
	}
	
	/**
	 * Установить время отправки запроса.
	 * <p>
	 * @param time время отправки запроса
	 */
	public synchronized void setRequestTime(Date time) {
		this.requestTime = time;
	}
	
	/**
	 * Установить текущее время в качестве времени отправки запроса.
	 */
	public synchronized void setRequestTime() {
		requestTime = new Date();
	}
	
	/**
	 * Получить информацию об ответе.
	 * <p>
	 * @return объект-информация об ответе
	 */
	public synchronized Object getResponse() {
		return response;
	}
	
	/**
	 * Установить информацию об ответе.
	 * <p>
	 * @param response объект-информация об ответе
	 */
	public synchronized void setResponse(Object response) {
		this.response = response;
	}
	
	/**
	 * Получить время получения ответа.
	 * <p>
	 * @return время получения ответа
	 */
	public synchronized Date getResponseTime() {
		return responseTime;
	}
	
	/**
	 * Установить время получения ответа.
	 * <p>
	 * @param time время получения ответа
	 */
	public synchronized void setResponseTime(Date time) {
		this.responseTime = time;
	}
	
	/**
	 * Установить текущее время в качестве времени получения ответа.
	 */
	public synchronized void setResponseTime() {
		responseTime = new Date();
	}
	
	/**
	 * Расчитать время транзакции в мс.
	 * <p>
	 * Используя установленные времена запроса и ответа расчитывает время,
	 * затраченное на выполнение транзакции. Если какое то из значений времени
	 * неопределено, то возвращает null.
	 * <p>
	 * @return время транзакции
	 */
	public synchronized Long getLatency() {
		if ( requestTime == null || responseTime == null ) {
			return null;
		} else {
			return responseTime.getTime() - requestTime.getTime();
		}
	}
	
	/**
	 * Признак начала транзакции.
	 * <p>
	 * Данный признак расчитывается по внутреннему состоянию транзакции.
	 * Определение объекта запроса рассматривается как начало транзакции.
	 * <p>
	 * @return true - если транзакция начата, false - если не начата
	 */
	public synchronized boolean isStarted() {
		return request == null ? false : true;
	}
	
	/**
	 * Признак завершения транзакции.
	 * <p>
	 * Данный признак расчитывается по внутреннему состоянию транзакции.
	 * Определение объекта ответа рассматривается как завершение транзакции.
	 * <p> 
	 * @return true - если транзакция завершена, false - если не завершена
	 */
	public synchronized boolean isExecuted() {
		return response == null ? false : true;
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Transaction.class ) {
			return false;
		}
		Transaction o = (Transaction) other;
		return new EqualsBuilder()
			.append(o.request, request)
			.append(o.response, response)
			.append(o.requestTime, requestTime)
			.append(o.responseTime, responseTime)
			.isEquals();
	}

}
