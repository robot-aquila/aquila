package ru.prolib.aquila.quik.api;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * Ответ программы QUIK на транзакцию. 
 */
public class QUIKResponse {
	private final T2QTransStatus status;
	private final int id;
	private final Long orderId;
	private final String msg;
	
	public QUIKResponse(T2QTransStatus status, int id, Long orderId, String msg)
	{
		super();
		this.status = status;
		this.id = id;
		this.orderId = orderId;
		this.msg = msg;
	}
	
	/**
	 * Получить статус транзакции.
	 * <p>
	 * @return статус транзакции
	 */
	public T2QTransStatus getStatus() {
		return status;
	}
	
	/**
	 * Получить номер транзакции.
	 * <p>
	 * @return номер транзакции
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Получить номер заявки.
	 * <p>
	 * @return номер заявки или null, если транзакция не подразумевает создание
	 * новой заявки
	 */
	public Long getOrderId() {
		return orderId;
	}
	
	/**
	 * Получить текстовую расшифровку результата.
	 * <p>
	 * @return текстовое сообщение
	 */
	public String getMessage() {
		return msg;
	}
	
	/**
	 * Является ли транзакция успешной?
	 * <p>
	 * @return true - транзакция выполнена, false - не выполнена или ошибка
	 */
	public boolean isSuccess() {
		return status == T2QTransStatus.DONE;
	}
	
	/**
	 * Завершилась ли транзакция ошибкой?
	 * <p>
	 * @return true - ошибка, false - не ошибка или не завершена
	 */
	public boolean isError() {
		return status != T2QTransStatus.DONE
			&& status != T2QTransStatus.RECV
			&& status != T2QTransStatus.SENT;
	}
	
	/**
	 * Финальное состояние транзакции?
	 * <p>
	 * @return true - финальное, false - промежуточное
	 */
	public boolean isFinal() {
		return isSuccess() || isError();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QUIKResponse.class ) {
			return false;
		}
		QUIKResponse o = (QUIKResponse) other;
		return new EqualsBuilder()
			.append(o.id, id)
			.append(o.msg, msg)
			.append(o.orderId, orderId)
			.append(o.status, status)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "["
			+ "id=" + id + ", status=" + status + ", "
			+ (orderId == null ? "" : "order=" + orderId + ", ")
			+ "msg=" + msg + "]";
	}

}
