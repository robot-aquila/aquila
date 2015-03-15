package ru.prolib.aquila.t2q;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Детали заявки.
 * <p>
 * 2013-02-19<br>
 * $Id$
 */
public class T2QOrder {
	private final int mode;
	private final long transId;
	private final long orderId;
	private final String classCode;
	private final String secCode;
	private final double price;
	private final long balance;
	private final double value;
	private final boolean isSell;
	private final int status;
	private final String account;
	private final String firmId;
	private final String clientCode;
	
	private final long qty;
	private final long date;
	private final long time;
	private final long activationTime;
	private final long withdrawTime;
	private final long expiry;
	private final double accruedInt;
	private final double yield;
	private final long uid;
	private final String userId;
	private final String brokerRef;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param mode 0 - новая заявка, 1 - начальное получение, 2 - последняя
	 * заявка из начальной рассылки 
	 * @param transId идентификатор транзакции или 0, если не задан
	 * @param orderId номер заявки
	 * @param classCode код класса инструмента
	 * @param secCode код инструмента
	 * @param price цена заявки
	 * @param balance неисполненый остаток заявки
	 * @param value объем заявки
	 * @param isSell направление заявки: true - продажа, false - покупка
	 * @param status 1 - активна, 2 - снята, иное - исполнена
	 * @param firmId идентификатор фирмы
	 * @param clientCode код клиента
	 * @param account код счета
	 * @param qty количество заявки 
	 * @param date дата заявки
	 * @param time время заявки 
	 * @param activationTime время активации заявки
	 * @param withdrawTime время снятия заявки
	 * @param expiry дата окончания срока действия заявки
	 * @param accruedInt НКД
	 * @param yield доходность
	 * @param uid идентификатор пользователя
	 * @param userId строковый идентификатор трейдера, от имени которого
	 * отправлена заявка
	 * @param brokerRef комментарий
	 */
	public T2QOrder(int mode, long transId, long orderId,
			String classCode, String secCode, double price, long balance,
			double value, boolean isSell, int status,
			String firmId, String clientCode, String account,
			long qty, long date, long time, long activationTime,
			long withdrawTime, long expiry, double accruedInt, double yield,
			long uid, String userId, String brokerRef)
	{
		super();
		this.mode = mode;
		this.transId = transId;
		this.orderId = orderId;
		this.classCode = classCode;
		this.secCode = secCode;
		this.price = price;
		this.balance = balance;
		this.value = value;
		this.isSell = isSell;
		this.status = status;
		this.firmId = firmId;
		this.clientCode = clientCode;
		this.account = account;
		this.qty = qty;
		this.date = date;
		this.time = time;
		this.activationTime = activationTime;
		this.withdrawTime = withdrawTime;
		this.expiry = expiry;
		this.accruedInt = accruedInt;
		this.yield = yield;
		this.uid = uid;
		this.userId = userId;
		this.brokerRef = brokerRef;
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает экземпляр на основе существующего экземпляра с форсированием
	 * идентификатора транзакции. 
	 * <p>
	 * @param order экземпляр-основание
	 * @param forceTransId новый идентификатор транзакции
	 */
	public T2QOrder(T2QOrder order, long forceTransId) {
		this(order.mode, forceTransId, order.orderId,
				order.classCode, order.secCode, order.price, order.balance,
				order.value, order.isSell, order.status,
				order.firmId, order.clientCode, order.account,
				order.qty, order.date, order.time, order.activationTime,
				order.withdrawTime, order.expiry, order.accruedInt, order.yield,
				order.uid, order.userId, order.brokerRef);
	}
	
	public long getQty() {
		return qty;
	}
	
	public long getDate() {
		return date;
	}
	
	public long getTime() {
		return time;
	}
	
	public long getActivationTime() {
		return activationTime;
	}
	
	public long getWithdrawTime() {
		return withdrawTime;
	}
	
	public long getExpiry() {
		return expiry;
	}
	
	public double getAccruedInt() {
		return accruedInt;
	}
	
	public double getYield() {
		return yield;
	}
	
	public long getUid() {
		return uid;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getBrokerRef() {
		return brokerRef;
	}
	
	/**
	 * Получить режим обновления.
	 * <p>
	 * @return 0 - новая заявка, 1 - начальное получение, 2 - последняя
	 * заявка из начальной рассылки
	 */
	public int getMode() {
		return mode;
	}
	
	/**
	 * Получить идентификатор транзакции.
	 * <p>
	 * @return идентификатор транзакции или 0, если не задан
	 */
	public long getTransId() {
		return transId;
	}
	
	/**
	 * Получить номер заявки.
	 * <p>
	 * @return номер заявки
	 */
	public long getOrderId() {
		return orderId;
	}
	
	/**
	 * Получить код класса инструмента.
	 * <p>
	 * @return код класса
	 */
	public String getClassCode() {
		return classCode;
	}
	
	/**
	 * Получить код инструмента.
	 * <p>
	 * @return код инструмента
	 */
	public String getSecCode() {
		return secCode;
	}
	
	/**
	 * Получить цену заявки.
	 * <p>
	 * @return цена заявки
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * Получить неисполненый остаток заявки.
	 * <p>
	 * @return остаток
	 */
	public long getBalance() {
		return balance;
	}
	
	/**
	 * Получить объем заявки.
	 * <p>
	 * @return объем заявки
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * Получить направление заявки.
	 * <p>
	 * @return true - заявка на продажу, false - на покупку
	 */
	public boolean isSell() {
		return isSell;
	}
	
	/**
	 * Получить статус заявки.
	 * <p>
	 * @return 1 - активна, 2 - снята, иное - исполнена
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * Получить идентификатор фирмы.
	 * <p>
	 * @return идентификатор фирмы
	 */
	public String getFirmId() {
		return firmId;
	}
	
	/**
	 * Получить код клиента.
	 * <p>
	 * @return код клиента
	 */
	public String getClientCode() {
		return clientCode;
	}
	
	/**
	 * Получить код счета.
	 * <p>
	 * @return код счета
	 */
	public String getAccount() {
		return account;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == T2QOrder.class ) {
			T2QOrder o = (T2QOrder) other;
			return new EqualsBuilder()
				.append(account, o.account)
				.append(balance, o.balance)
				.append(classCode, o.classCode)
				.append(clientCode, o.clientCode)
				.append(firmId, o.firmId)
				.append(isSell, o.isSell)
				.append(mode, o.mode)
				.append(orderId, o.orderId)
				.append(price, o.price)
				.append(secCode, o.secCode)
				.append(status, o.status)
				.append(transId, o.transId)
				.append(value, o.value)
				.append(qty, o.qty)
				.append(date, o.date)
				.append(time, o.time)
				.append(activationTime, o.activationTime)
				.append(withdrawTime, o.withdrawTime)
				.append(expiry, o.expiry)
				.append(accruedInt, o.accruedInt)
				.append(yield, o.yield)
				.append(uid, o.uid)
				.append(userId, o.userId)
				.append(brokerRef, o.brokerRef)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		String strStatus;
		if ( status == 1 ) strStatus = "ACTIVE";
		else if ( status == 2 ) strStatus = "KILLED";
		else strStatus = "FILLED";
		return getClass().getSimpleName() + "[trn=" + transId + ", "
			+ "id=" + orderId + ", "
			+ (isSell ? "Sell" : "Buy")
			+ ", sec=" + secCode + "@" + classCode + ", "
			+ "price=" + price + ", "
			+ "balance=" + balance + "/" + qty + ", "
			+ "value=" + value + ", "
			+ "status=" + strStatus + ", "
			+ "date/time=" + date + "/" + time + ", "
			+ "atime=" + activationTime + ", "
			+ "wtime=" + withdrawTime + ", "
			+ "expiry=" + expiry + ", ...]";
	}
	
}
