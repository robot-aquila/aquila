package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс набора инструментов.
 * <p>
 * Набор инструментов обеспечивает доступ к конкретным инструментам типа
 * {@link Security}. Данный интерфейс предназначен для использования
 * пользователями сервиса и не содержит методов управления набором.
 * <p>
 * 2012-06-09<br>
 * $Id: Securities.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface Securities {

	/**
	 * Получить список доступных инструментов
	 * <p>
	 * @return список инструментов
	 */
	public List<Security> getSecurities();
	
	/**
	 * Получить инструмент по дескриптору
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return инструмент
	 * @throws SecurityNotExistsException
	 */
	public Security getSecurity(SecurityDescriptor descr)
			throws SecurityException;
	
	/**
	 * Проверить наличие инструмента по дескриптору.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return наличие инструмента
	 */
	public boolean isSecurityExists(SecurityDescriptor descr);

	/**
	 * Получить тип события: при появлении информации о новом инструменте.
	 * <p>
	 * Генерируется событие {@link SecurityEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnSecurityAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех инструментов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnSecurityChanged();
	
	/**
	 * Перехватчик событий соответствующего типа от всех инструментов.
	 * <p>
	 * @return тип события
	 */
	public EventType OnSecurityTrade();
	
	/**
	 * Получить количество доступных инструментов.
	 * <p>
	 * @return количество инструментов
	 */
	public int getSecuritiesCount();

}
