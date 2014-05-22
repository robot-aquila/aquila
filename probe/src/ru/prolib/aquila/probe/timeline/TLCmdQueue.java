package ru.prolib.aquila.probe.timeline;

import java.util.concurrent.*;

/**
 * Очередь команд управления эмуляцией.
 * <p>
 * Очередь команд используется для передачи управляющих инструкций исполняющему
 * потоку.
 */
public class TLCmdQueue {
	private final BlockingQueue<TLCmd> queue;
	private TLCmd last;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param queue очередь команд
	 */
	public TLCmdQueue(BlockingQueue<TLCmd> queue) {
		super();
		this.queue = queue;
	}
	
	/**
	 * Конструктор.
	 */
	public TLCmdQueue() {
		this(new LinkedBlockingQueue<TLCmd>());
	}
	
	/**
	 * Добавить команду в конец очереди.
	 * <p>
	 * @param command команда
	 * @throws TLInterruptionsNotAllowedException
	 */
	public synchronized void put(TLCmd command) {
		try {
			queue.put(command);
		} catch ( InterruptedException e ) {
			throw new TLInterruptionsNotAllowedException(e);
		}
	}
	
	/**
	 * Неблокирующий запрос команды.
	 * <p>
	 * Данный метод позволяет получить команду, не изымая ее из очереди. Если
	 * на момент вызова очередь не содержит команд, то вызов завершится
	 * возвратом null. При наличии команды, последующие вызовы этого метода
	 * и метода {@link #tellb()} будут завершаться возвратом этой же самой
	 * команды до тех пор, пока команда не будет изъята из очереди
	 * посредством метода {@link #pull()} или {@link #pullb()}.
	 * <p>
	 * @return очередная команда или null, если очередь пуста
	 */
	public synchronized TLCmd tell() {
		if ( last == null ) {
			last = queue.poll();
		}
		return last;
	}
	
	/**
	 * Блокирующий запрос команды.
	 * <p>
	 * Данный метод позволяет получить команду, не изымая ее из очереди. При
	 * отсутствии очередной команды, вызов приведет к блокировке потока до того
	 * момента, пока поток не будет прерван или в очереди не появится новая
	 * команда. При наличии команды, последующие вызовы этого метода и метода
	 * {@link #tell()} будут завершаться возвратом этой же самой команды до тех
	 * пор, пока команда не будет изъята из очереди посредством метода
	 * {@link #pull()} или {@link #pullb()}. 
	 * <p>
	 * @return очередная команда
	 * @throws TLInterruptionsNotAllowedException
	 */
	public synchronized TLCmd tellb() {
		if ( last == null ) {
			try {
				last = queue.take();
			} catch ( InterruptedException e ) {
				throw new TLInterruptionsNotAllowedException(e);
			}
		}
		return last;
	}
	
	/**
	 * Неблокирующее изъятие команды.
	 * <p>
	 * При наличии команды, вызов данного метода приводит к удалению команды из
	 * очереди. Если на момент вызова очередь не содержит команд, то вызов
	 * завершится возвратом null. Данный метод так же позволяет удалить из
	 * очереди команду, полученную в результате предшествующего вызова метода
	 * {@link #tell()} или {@link #tellb()}. 
	 * <p>
	 * @return команда или null, если очередь не содержит команд
	 */
	public synchronized TLCmd pull() {
		TLCmd result = last;
		if ( result == null ) {
			result = tell();
		}
		last = null;
		return result;
	}
	
	/**
	 * Блокирующее изъятие команды.
	 * <p>
	 * При наличии команды, вызов данного метода приводит к удалению команды из
	 * очереди. При отсутствии очередной команды, вызов приведет к блокировке
	 * потока до того момента, пока поток не будет прерван или в очереди не
	 * появится новая команда. Данный метод так же позволяет удалить из
	 * очереди команду, полученную в результате предшествующего вызова метода
	 * {@link #tell()} или {@link #tellb()}.
	 * <p>
	 * @return команда
	 * @throws TLInterruptionsNotAllowedException
	 */
	public synchronized TLCmd pullb() {
		TLCmd result = last;
		if ( result == null ) {
			result = tellb();
		}
		last = null;
		return result;
	}
	
	/**
	 * Очистить очередь команд.
	 */
	public synchronized void clear() {
		queue.clear();
	}
	
}
