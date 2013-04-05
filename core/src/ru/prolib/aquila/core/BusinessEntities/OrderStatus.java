package ru.prolib.aquila.core.BusinessEntities;

/**
 * Статус заявки.
 * <p>
 * Класс определяет константы, использующиеся для спецификации текущего
 * состояния заявки. Подразумевается, что данный статус отражает именно
 * биржевое состояние заявки и не включает в себя системные состояния такие как
 * состояние ошибки, отклонения и т.п.  
 * <p>
 * 2012-05-30<br>
 * $Id: OrderStatus.java 459 2013-01-29 17:11:57Z whirlwind $
 */
public class OrderStatus {
	public static final int VERSION = 0x01;
	
	/**
	 * Заявка ожидает регистрации в терминале.
	 */
	public static final OrderStatus PENDING = new OrderStatus("Pending");
	
	/**
	 * Заявка активна.
	 */
	public static final OrderStatus ACTIVE = new OrderStatus("Active");
	
	/**
	 * Заявка исполнена.
	 */
	public static final OrderStatus FILLED = new OrderStatus("Filled");
	
	/**
	 * Заявка отменена.
	 */
	public static final OrderStatus CANCELLED = new OrderStatus("Cancelled");
	
	/**
	 * Заявка отклонена.
	 */
	public static final OrderStatus FAILED = new OrderStatus("Failed");
	
	private final String code;
	
	/**
	 * Создать статус.
	 * <p>
	 * @param code код статуса
	 */
	private OrderStatus(String code) {
		super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}

}
