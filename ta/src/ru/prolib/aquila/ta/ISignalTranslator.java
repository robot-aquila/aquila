package ru.prolib.aquila.ta;

/**
 * Интерфейс транслятора сигналов.
 * Используется генератором сигналов для регистрации сигналов.
 */
public interface ISignalTranslator {
	
	/**
	 * Зарегистрировать сигнал на покупку по указанной цене.
	 * @param price
	 * @param comment
	 */
	public void signalToBuy(double price, String comment);
	
	public void signalToBuy(double price);
	
	/**
	 * Зарегистрировать сигнал на продажу по указанной цене.
	 * @param price
	 * @param comment
	 */
	public void signalToSell(double price, String comment);
	
	public void signalToSell(double price);

}
