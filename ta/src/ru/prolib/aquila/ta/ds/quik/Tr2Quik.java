package ru.prolib.aquila.ta.ds.quik;

public interface Tr2Quik {

	/**
	 * Получить номер последней транзакции.
	 * @return
	 */
	public abstract long getLastTransactionId();

	/**
	 * Завершить работу.
	 */
	public abstract void close();
	
	/**
	 * Выполнить транзакцию.
	 * 
	 * @param command команда
	 * @param expectedStatus ожидаемый статус
	 * @return
	 * @throws InterruptedException
	 * @throws Tr2QuikTimeoutException
	 * @throws Tr2QuikRejectedException
	 * @throws Tr2QuikIoException
	 * @throws Tr2QuikException
	 */
	public Tr2QuikResult transaction(String command, int expectedStatus)
		throws InterruptedException, Tr2QuikTimeoutException,
			Tr2QuikRejectedException, Tr2QuikIoException,
			Tr2QuikException;

}