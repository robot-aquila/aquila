package ru.prolib.aquila.util;


public class LateNotify extends Thread {
	final Object monitor;
	final long pause;
	final int count;
	final LateNotifyAction action;
	
	/**
	 * Конструктор с дефолтными параметрами срабатывания.
	 * 
	 * Количество срабатываний устанавливается в единицу, пауза в 100 мс.
	 * @param monitor
	 */
	public LateNotify(Object monitor) {
		this(monitor, 100, 1, null);
	}
	
	public LateNotify(Object monitor, int count, long pause) {
		this(monitor, count, pause, null);
	}
	
	/**
	 * Конструктор.
	 * 
	 * @param monitor
	 * @param count количество срабатываний
	 * @param pause пауза между срабатываниями
	 * @param action финальное действие
	 */
	public LateNotify(Object monitor, int count, long pause,
			LateNotifyAction action)
	{
		super();
		this.monitor = monitor;
		this.pause = pause;
		this.count = count;
		this.action = action;
	}
	
	@Override
	public void run() {
		synchronized ( monitor ) { 
			try {
				for ( int i = 0; i < count; i ++ ) {
					Thread.sleep(pause);
					monitor.notifyAll();
				}
				if ( action != null ) {
					action.execute();
				}
			} catch ( Exception e ) {
				System.err.println("Unhandled exception");
				e.printStackTrace();
			}
		}
	}
	
}