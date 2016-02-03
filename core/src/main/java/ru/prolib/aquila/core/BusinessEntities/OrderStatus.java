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
public enum OrderStatus {
	
	/**
	 * Заявка ожидает регистрации в терминале.
	 */
	PENDING(false, false, false, true),
	
	/**
	 * Алгоритмическая заявка в ожидании исполнения условия.
	 */
	CONDITION(false, false, true, true),
	
	/**
	 * Заявка отправлена на регистрацию в торговую систему.
	 * Подтверждение еще не получено.
	 */
	SENT(false, false, false, true),
	
	/**
	 * Заявка активна.
	 */
	ACTIVE(false, false, true, true),
	
	/**
	 * В торговую систему отправлен запрос на снятие заявки.
	 * Подтверждение еще не получено. 
	 */
	CANCEL_SENT(false, false, false, true),
	
	/**
	 * Заявка исполнена.
	 */
	FILLED(false, true, false, false),
	
	/**
	 * Заявка отменена.
	 */
	CANCELLED(false, true, false, false),
	
	/**
	 * Заявка отклонена торговой системой на этапе регистрации.
	 */
	REJECTED(true, true, false, false),

	/**
	 * Запрос на снятие заявки отклонен торговой системой.
	 */
	CANCEL_FAILED(true, true, false, false);
	
	private final boolean isError;
	private final boolean isFinal;
	private final boolean isActive;
	private final boolean canBeUpdated;
	
	OrderStatus(boolean isError, boolean isFinal,
			boolean isActive, boolean canBeUpdated)
	{
		this.isError = isError;
		this.isFinal = isFinal;
		this.isActive = isActive;
		this.canBeUpdated = canBeUpdated;
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
