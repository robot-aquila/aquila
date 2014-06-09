package ru.prolib.aquila.probe.timeline;

/**
 * Класс потока эмулятора хронологии.
 * <p>
 * Данный класс реализует некоторые делегаты к функции эмуляции.
 */
public class TLSThread extends Thread {
	private final TLSThreadWorker worker;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param worker функция исполнения хронологии
	 */
	public TLSThread(TLSThreadWorker worker) {
		super(worker);
		this.worker = worker;
	}
	
	/**
	 * Включить отладочные сообщения.
	 * <p>
	 * @param enabled true - включить, false - отключить
	 */
	public void setDebug(boolean enabled) {
		worker.setDebug(enabled);
	}

}
