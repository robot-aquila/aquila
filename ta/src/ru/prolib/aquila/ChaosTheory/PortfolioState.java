package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.util.Observable;

/**
 * Интерфейс состояния портфеля.
 * Наблюдаемый объект. Генерирует уведомления в случае изменения баланса или
 * текущей позиции.
 */
public interface PortfolioState extends Observable {
	
	/**
	 * Получить суммарную стоимость активов, выраженную в деньгах.
	 * @return
	 * @throws PortfolioException
	 */
	public double getTotalMoney() throws PortfolioException;

	/**
	 * Получить объем доступных денежных средств.
	 * @return
	 */
	public double getMoney() throws PortfolioException;

	/**
	 * Получить текущую позицию.
	 * @return 0 - нейтральная позиция, >0 - лонг, <0 - шорт
	 */
	public int getPosition() throws PortfolioException;
	
	/**
	 * Получить накопленную вариационную маржу.
	 * 
	 * @return
	 */
	public double getVariationMargin() throws PortfolioException;
	
	/**
	 * Получить блокированное ГО.
	 * 
	 * @return
	 */
	public double getInitialMargin() throws PortfolioException;

	/**
	 * Ожидать нейтральную позицию не дольше указанного времени.
	 * 
	 * @param timeout время ожидания в миллисекундах
	 * @throws PortfolioTimeoutException
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public void waitForNeutralPosition(long timeout)
			throws PortfolioTimeoutException, PortfolioException,
			InterruptedException;

}