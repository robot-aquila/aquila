package ru.prolib.aquila.dde.utils;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс обозреваемого DDE-сервиса.
 * <p>
 * Обозреваемый DDE-сервис предполагает использование событийной модели для
 * обработки входящих транзакций. Это позволяет абстрагироваться от источника
 * данных и облегчает тестирование обработчиков DDE-транзакций. Кроме этого,
 * использование событийной модели решает проблему разрыва связи клиента с
 * DDE-сервером в случае превышения лимита времени обработки DDE-транзакции.
 * Данная проблема может возникать при обработке большого объема данных
 * непосредственно в приемнике DDE-данных.
 * <p>
 * 2012-07-27<br>
 * $Id: DDEObservableService.java 243 2012-07-29 15:16:56Z whirlwind $
 */
public interface DDEObservableService {
	
	/**
	 * Получить тип события: при подключении нового DDE-клиента.
	 * <p>
	 * Обработчик события данного типа будет получать экземпляр класса
	 * {@link DDETopicEvent} каждый раз при подключении нового DDE-клиента. 
	 * <p>
	 * @return тип события
	 */
	public EventType OnConnect();
	
	/**
	 * Получить тип события: при отключении DDE-клиента.
	 * <p>
	 * Обработчик события данного типа будет получать экземпляр класса
	 * {@link DDETopicEvent} каждый раз при отключении DDE-клиента.
	 * <p>
	 * @return тип события
	 */
	public EventType OnDisconnect();
	
	/**
	 * Получить тип события: при регистрации сервиса.
	 * <p>
	 * Обработчик события данного типа будет получать экземпляр класса
	 * {@link DDEEvent} после успешной регистрации сервиса.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRegister();
	
	/**
	 * Получить тип события: при удалении сервиса.
	 * <p>
	 * Обработчик события данного типа будет получать экземпляр класса
	 * {@link DDEEvent} перед удалением сервиса.
	 * <p>
	 * @return тип события
	 */
	public EventType OnUnregister();
	
	/**
	 * Получить тип события: при получении данных.
	 * <p>
	 * Обработчик события данного типа будет получать экземпляр класса
	 * {@link DDEDataEvent} каждый раз при получении данных.
	 * <p>
	 * @return тип события
	 */
	public EventType OnData();
	
	/**
	 * Получить тип события: при получении табличных данных.
	 * <p>
	 * Обработчик события данного типа будет получать экземпляр класса
	 * {@link DDETableEvent} каждый раз при получении новой XLT-таблицы.
	 * <p>
	 * @return тип события
	 */
	public EventType OnTable();

}
