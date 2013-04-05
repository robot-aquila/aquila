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
	 * Получить инструмент по коду инструмента и коду класса
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return инструмент
	 * @throws SecurityNotExistsException
	 */
	public Security getSecurity(String code, String classCode)
			throws SecurityException;
	
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
	 * Получить инструмент по коду инструмента
	 * <p>
	 * @param code код инструмента
	 * @return инструмент
	 * @throws SecurityAmbiguousException
	 * @throws SecurityNotExistsException
	 */
	public Security getSecurity(String code)
			throws SecurityException;
	
	/**
	 * Проверить наличие инструмента.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @return наличие инструмента
	 */
	public boolean isSecurityExists(String code, String classCode);
	
	/**
	 * Проверить наличие инструмента.
	 * <p>
	 * @param code код инструмента
	 * @return наличие инструмента
	 */
	public boolean isSecurityExists(String code);
	
	/**
	 * Проверить наличие инструмента по дескриптору.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @return наличие инструмента
	 */
	public boolean isSecurityExists(SecurityDescriptor descr);
	
	/**
	 * Проверить неоднозначность кода инструмента.
	 * <p>
	 * @param code код инструмента
	 * @return true - имеются несколько инструментов с таким кодом,
	 * false - инструмент с таким кодом только один, то есть код однозначно
	 * идентифицирует инструмент
	 */
	public boolean isSecurityAmbiguous(String code);

	/**
	 * Получить тип события: при появлении информации о новом инструменте.
	 * <p>
	 * Генерируется событие {@link SecurityEvent}.
	 * <p>
	 * @return тип события
	 */
	public EventType OnSecurityAvailable();
	
	/**
	 * Получить код валюты по-умолчанию.
	 * <p>
	 * @return код валюты
	 */
	public String getDefaultCurrency();
	
	/**
	 * Получить тип инструмента по-умолчанию.
	 * <p>
	 * @return тип инструмента
	 */
	public SecurityType getDefaultType();
	
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
