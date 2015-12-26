package ru.prolib.aquila.core;

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
	 * Enqueue event.
	 * <p>
	 * Do not use this method. Use {@link #enqueue(EventType, EventFactory)}
	 * instead. This method will be marked as private in the future.
	 * <p>
	 * @param event - event to dispatch
	 */
	@Deprecated
	public void enqueue(Event event);
	
	/**
	 * Enqueue event created by factory.
	 * <p>
	 * This method is used to enqueue events created by factory for the
	 * specified event type and all its alternate types.
	 * <p>
	 * @param type - starting event type
	 * @param factory - event factory
	 */
	public void enqueue(EventType type, EventFactory factory);
	
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
	 * @param timeout таймаут в миллисекундах значение &gt; 0
	 * @return true, если дождалить завершения, иначе false
	 * @throws InterruptedException - The waiting thread have interrupted
	 */
	public boolean join(long timeout) throws InterruptedException;
	
	/**
	 * Ожидать завершения потока обработки очереди событий.
	 * <p>
	 * Если поток обработки очереди событий выполняется, то блокирует текущий
	 * поток до момента завершения потока очереди. Если вызов осуществлен из
	 * потока очереди, то осуществляет немедленный возврат. 
	 * <p>
	 * @throws InterruptedException - The waiting thread have interrupted
	 */
	public void join() throws InterruptedException;
	
	/**
	 * Получить идентификатор очереди.
	 * <p>
	 * @return идентификатор очереди
	 */
	public String getId();

}
