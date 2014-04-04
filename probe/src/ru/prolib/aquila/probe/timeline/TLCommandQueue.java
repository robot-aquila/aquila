package ru.prolib.aquila.probe.timeline;

import java.util.concurrent.*;

/**
 * Очередь команд управления симуляцией.
 * <p>
 * Симуляция выполняется в реальном времени. Команды позволяют управлять
 * симуляцией: приостанавливать выполняющуются симуляцию, выполнять до указанной
 * временной отсечки или принудительно завершать симуляцию.
 */
public class TLCommandQueue {
	private final BlockingQueue<TLCommand> queue;
	private TLCommand last;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param queue очередь команд
	 */
	public TLCommandQueue(BlockingQueue<TLCommand> queue) {
		super();
		this.queue = queue;
	}
	
	/**
	 * Конструктор.
	 */
	public TLCommandQueue() {
		this(new LinkedBlockingQueue<TLCommand>());
	}
	
	/**
	 * Добавить команду в конец очереди.
	 * <p>
	 * @param command команда
	 * @throws TLInterruptionsNotAllowedException
	 */
	public synchronized void put(TLCommand command) {
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
	public synchronized TLCommand tell() {
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
	public synchronized TLCommand tellb() {
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
	public synchronized TLCommand pull() {
		TLCommand result = last;
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
	public synchronized TLCommand pullb() {
		TLCommand result = last;
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
