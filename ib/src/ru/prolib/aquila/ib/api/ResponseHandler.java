package ru.prolib.aquila.ib.api;

/**
 * Общая для всех обработчиков часть интерфейса.
 */
public interface ResponseHandler {
	
	public void error(int reqId, int errorCode, String errorMsg);
	
	public void connectionOpened();
	
	public void connectionClosed();

}
