package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого набора инструментов.
 * <p>
 * 2012-07-04<br>
 * $Id: EditableSecurities.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public interface EditableSecurities extends Securities {
	
	/**
	 * Получить или добавить новый инструмент в набор.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return инструмент
	 */
	public EditableSecurity getEditableSecurity(SecurityDescriptor descr);
	
	/**
	 * Генерировать событие о добавлении нового инструмента.
	 * <p>
	 * @param security новый инструмент
	 */
	public void fireSecurityAvailableEvent(Security security);

}
