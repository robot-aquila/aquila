package ru.prolib.aquila.t2q;

/**
 * Интерфейс фабрики сервиса TRANS2QUIK.
 * <p>
 * Поставщик сервиса реализует фабрику для обеспечения корректного
 * инстанцирования экземпляров сервиса. Потребитель сервиса использует
 * фабрику для создания произвольного количества экземпляров сервиса.
 * <p>
 * 2013-01-29<br>
 * $Id: T2QServiceFactory.java 464 2013-01-31 09:53:57Z whirlwind $
 */
public interface T2QServiceFactory {
	
	/**
	 * Создать экземпляр сервиса.
	 * <p>
	 * @param handler обработчик событий
	 * @return экземпляр сервиса
	 */
	public T2QService createService(T2QHandler handler);

}
