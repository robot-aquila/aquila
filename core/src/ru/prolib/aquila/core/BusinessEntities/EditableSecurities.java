package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого набора инструментов.
 * <p>
 * 2012-07-04<br>
 * $Id: EditableSecurities.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public interface EditableSecurities extends Securities {
	
	/**
	 * Получить экземпляр инструмента.
	 * <p>
	 * Если инструмент не существует, создает инструмент и добавляет его в
	 * набор. В результате вызова никаких событий не генерируется.
	 * <p>
	 * @param terminal терминал
	 * @param descr дескриптор инструмента
	 * @return инструмент
	 */
	public EditableSecurity getEditableSecurity(EditableTerminal terminal,
			SecurityDescriptor descr);
	
	/**
	 * Генерировать события инструмента.
	 * <p>
	 * @param security инструмент
	 */
	public void fireEvents(EditableSecurity security);

}
