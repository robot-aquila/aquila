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
public interface EventQueue {
	
	/**
	 * Enqueue event created by factory.
	 * <p>
	 * This method is used to enqueue events created by factory for the
	 * specified event type and all its alternate types. This method is
	 * thread safe and never causes deadlocks.
	 * <p>
	 * @param type - starting event type
	 * @param factory - event factory
	 */
	public void enqueue(EventType type, EventFactory factory);
		
	/**
	 * Получить идентификатор очереди.
	 * <p>
	 * @return идентификатор очереди
	 */
	public String getId();
	
	FlushIndicator newFlushIndicator();
	
	void shutdown();
	EventQueueStats getStats();

}
