package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого набора инструментов.
 * <p>
 * 2012-07-04<br>
 * $Id: EditableSecurities.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public interface EditableSecurities extends Securities {
	
	/**
	 * Получить модифицируемый экземпляр инструмента.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return инструмент
	 * @throws SecurityNotExistsException указанный инструмент не существует
	 */
	public EditableSecurity getEditableSecurity(SecurityDescriptor descr)
			throws SecurityNotExistsException;
	
	/**
	 * Генерировать событие о добавлении нового инструмента.
	 * <p>
	 * @param security новый инструмент
	 */
	public void fireSecurityAvailableEvent(Security security);
	
	/**
	 * Создать инструмент.
	 * <p>
	 * Создает инструмент и добавляет его в текущий набор.
	 * В результате вызова никаких событий не генерируется.
	 * <p>
	 * @param terminal терминал
	 * @param descr дескриптор инструмента
	 * @return экземпляр созданного инструмента
	 * @throws SecurityAlreadyExistsException такой инструмент уже есть в наборе
	 */
	public EditableSecurity createSecurity(EditableTerminal terminal,
			SecurityDescriptor descr) throws SecurityAlreadyExistsException;

}
