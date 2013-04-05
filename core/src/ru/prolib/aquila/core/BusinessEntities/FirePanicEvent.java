package ru.prolib.aquila.core.BusinessEntities;

public interface FirePanicEvent {

	/**
	 * Генерировать событие о паническом состоянии.
	 * <p>
	 * @param code код ситуации
	 * @param msgId идентификатор сообщения
	 */
	public void firePanicEvent(int code, String msgId);

	/**
	 * Генерировать событие о паническом состоянии.
	 * <p>
	 * Данный метод используется для описания состояний, характеризующемся
	 * дополнительными аргументами. Как правило, идентификатор сообщения
	 * указывает на строку с плейсхолдерами, а массив аргументов содержит
	 * значения для подстановки. 
	 * <p>
	 * @param code код ситуации
	 * @param msgId идентификатор сообщения
	 * @param args аргументы, описывающие ситуацию
	 */
	public void firePanicEvent(int code, String msgId, Object[] args);

}