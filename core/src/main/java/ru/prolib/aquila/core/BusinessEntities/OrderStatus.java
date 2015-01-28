package ru.prolib.aquila.core.BusinessEntities;

/**
 * Статус заявки.
 * <p>
 * Класс определяет константы, использующиеся для спецификации текущего
 * состояния заявки.  
 * <p>
 * 2012-05-30<br>
 * $Id: OrderStatus.java 459 2013-01-29 17:11:57Z whirlwind $
 */
public class OrderStatus {
	public static final int VERSION = 0x02;
	
	/**
	 * Заявка ожидает регистрации в терминале.
	 */
	public static final OrderStatus PENDING;
	
	/**
	 * Алгоритмическая заявка в ожидании исполнения условия.
	 */
	public static final OrderStatus CONDITION;
	
	/**
	 * Заявка отправлена на регистрацию в торговую систему.
	 * Подтверждение еще не получено.
	 */
	public static final OrderStatus SENT;
	
	/**
	 * Заявка активна.
	 */
	public static final OrderStatus ACTIVE;
	
	/**
	 * В торговую систему отправлен запрос на снятие заявки.
	 * Подтверждение еще не получено. 
	 */
	public static final OrderStatus CANCEL_SENT;
	
	/**
	 * Заявка исполнена.
	 */
	public static final OrderStatus FILLED;
	
	/**
	 * Заявка отменена.
	 */
	public static final OrderStatus CANCELLED;
	
	/**
	 * Заявка отклонена торговой системой на этапе регистрации.
	 */
	public static final OrderStatus REJECTED;
	
	/**
	 * Запрос на снятие заявки отклонен торговой системой.
	 */
	public static final OrderStatus CANCEL_FAILED;
	
	static {
		PENDING = new OrderStatus("Pending", false, false, false, true);
		ACTIVE = new OrderStatus("Active", false, false, true, true);
		FILLED = new OrderStatus("Filled", false, true, false, false);
		CANCELLED = new OrderStatus("Cancelled", false, true, false, false);
		REJECTED = new OrderStatus("Rejected", true, true, false, false);
		CANCEL_SENT = new OrderStatus("Cancelling", false, false, false, true);
		CONDITION = new OrderStatus("Condition", false, false, true, true);
		SENT = new OrderStatus("Sent", false, false, false, true);
		CANCEL_FAILED = new OrderStatus("Cancel failed", true,true,false,false);
	}
	
	private final String code;
	private final boolean isError;
	private final boolean isFinal;
	private final boolean isActive;
	private final boolean canBeUpdated;
	
	/**
	 * Создать статус.
	 * <p>
	 * @param code код статуса
	 * @param isError признак ошибки по заявке
	 * @param isFinal признак финального статуса
	 * @param isActive признак активности заявки
	 * @param canBeUpdated признак модифицируемости заявки
	 */
	private OrderStatus(String code, boolean isError, boolean isFinal,
			boolean isActive, boolean canBeUpdated)
	{
		super();
		this.code = code;
		this.isError = isError;
		this.isFinal = isFinal;
		this.isActive = isActive;
		this.canBeUpdated = canBeUpdated;
	}
	
	@Override
	public String toString() {
		return code;
	}
	
	/**
	 * Является ли статус признаком ошибки?
	 * <p>
	 * @return признак статуса-ошибки 
	 */
	public boolean isError() {
		return isError;
	}
	
	/**
	 * Является ли статус финальным?
	 * <p>
	 * Финальный статус означает, что с заявкой больше не произойдет никаких
	 * изменений: ее атрибуты не будут изменены, новые сделки не появятся,
	 * заявку нельзя отменить.
	 * <p>
	 * @return true - финальный статус, false - промежуточный
	 */
	public boolean isFinal() {
		return isFinal;
	}
	
	/**
	 * Является ли заявка активной?
	 * <p>
	 * Данный признак определяет является ли заявка в данном статусе активной.
	 * Активная заявка может быть отменена или исполнена, состояние ее
	 * атрибутов может измениться, могут быть совершены сделки и т.д.
	 * <p>
	 * @return признак активности заявки
	 */
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * Может ли измениться внутреннее состояние заявки?
	 * <p>
	 * Этот признак свидетельствует о том, что внутреннее состояние заявки может
	 * измениться с течением времени.
	 * <p>
	 * @return признак возможности изменения состояния заявки
	 */
	public boolean canBeUpdated() {
		return canBeUpdated;
	}

}
