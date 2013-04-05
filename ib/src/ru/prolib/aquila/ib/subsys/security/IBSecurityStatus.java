package ru.prolib.aquila.ib.subsys.security;

/**
 * Статус инструмента.
 * <p>
 * 2012-11-20<br>
 * $Id: IBSecurityStatus.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBSecurityStatus {
	/**
	 * Нет информации об инструменте.
	 */
	public static final IBSecurityStatus NONE = new IBSecurityStatus("None");
	
	/**
	 * Запрос информации об инструменте был отправлен на сервер, но ответ
	 * еще не получен. Запрашиваемый код должен подождать изменения статуса
	 * инструмента. 
	 */
	public static final IBSecurityStatus SENT = new IBSecurityStatus("Sent");
	
	/**
	 * Инструмент с указанными параметрами не существует или недоступен. При
	 * этом статусе никакие другие запросы на сервер отправляться не должны.
	 * Запрос существования инструмента с указанными параметрами всегда
	 * возвращает false, а запрос экземпляра инструмента завершается выбросом
	 * исключения о несуществующем инструменте. 
	 */
	public static final IBSecurityStatus NFND = new IBSecurityStatus("NotFnd");
	
	/**
	 * Ответ на запрос информации об инструменте был получен ранее.
	 * Данный статус означает что экземпляр инструмента инстанцирован и
	 * сохранен в наборе инструментов.
	 */
	public static final IBSecurityStatus DONE = new IBSecurityStatus("Done");
	
	private final String code;
	
	private IBSecurityStatus(String code) {
		super();
		this.code = code;
	}
	
	/**
	 * Получить код статуса.
	 * <p>
	 * @return код
	 */
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return getCode();
	}

}
