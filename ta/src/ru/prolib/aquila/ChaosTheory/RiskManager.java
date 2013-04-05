package ru.prolib.aquila.ChaosTheory;

public interface RiskManager {

	/**
	 * Рассчитать количество заявки на увеличение длиной позиции.
	 * @param price цена заявки на покупку.
	 * @return возвращает значение больше нуля, указывающее на количество заявки
	 * или ноль, если увеличивать позицию нельзя.
	 */
	abstract public int getLongSize(double price);
	
	/**
	 * Рассчитать количество заявки на увеличение короткой позиции.
	 * @param price цена заявки на продажу.
	 * @return значение больше нуля указывает на количество заявки, если ноль,
	 * то увеличивать позицию нельзя.
	 */
	abstract public int getShortSize(double price);

}
