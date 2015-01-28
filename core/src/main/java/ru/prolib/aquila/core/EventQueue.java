package ru.prolib.aquila.core;

import java.util.List;

/**
 * Интерфейс очереди событий.
 * <p>
 * Очередь событий гарантирует диспетчеризацию событий в порядке их поступления.
 * Однако важно помнить, что при подаче на диспетчеризацию из различных потоков
 * порядок поступления событий не может быть определен. 
 * <p>
 * 2012-04-09<br>
 * $Id: EventQueue.java 327 2012-12-05 19:58:26Z whirlwind $
 */
public interface EventQueue extends Starter {
	
	/**
	 * Поместить событие в очередь на обработку.
	 * <p>
	 * @param event событие
	 * @param listeners список получателей
	 */
	public void enqueue(Event event, List<EventListener> listeners);
	
	/**
	 * Поместить событие в очередь на обработку.
	 * <p>
	 * @param event событие
	 * @param dispatcher диспетчер событий
	 */
	public void enqueue(Event event, EventDispatcher dispatcher);
	
	/**
	 * Проверить запущен-ли поток диспетчеризации событий.
	 * <p>
	 * @return true если запущен, иначе - false
	 */
	public boolean started();
	
	/**
	 * Проверить является-ли текущий поток является потоком очереди событий.
	 * <p>  
	 * @return true - текущий поток обрабатывает очередь событий, false - иначе 
	 */
	public boolean isDispatchThread();
	
	/**
	 * Ожидать завершения потока обработки очереди событий.
	 * <p>
	 * Если поток обработки очереди событий выполняется, то блокирует текущий
	 * поток до момента завершения потока очереди. Если вызов осуществлен из
	 * потока очереди, то осуществляет немедленный возврат. 
	 * <p>
	 * @param timeout таймаут в миллисекундах значение > 0
	 * @return true, если дождалить завершения, иначе false
	 * @throws InterruptedException
	 */
	public boolean join(long timeout) throws InterruptedException;
	
	/**
	 * Ожидать завершения потока обработки очереди событий.
	 * <p>
	 * Если поток обработки очереди событий выполняется, то блокирует текущий
	 * поток до момента завершения потока очереди. Если вызов осуществлен из
	 * потока очереди, то осуществляет немедленный возврат. 
	 */
	public void join() throws InterruptedException;
	
	/**
	 * Получить идентификатор очереди.
	 * <p>
	 * @return идентификатор очереди
	 */
	public String getId();

}
