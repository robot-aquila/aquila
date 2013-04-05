package ru.prolib.aquila.ChaosTheory;


/**
 * Интерфейс действия экстренного закрытия позиции.
 */
public interface PortfolioDriverEmergClosePosition {
	
	/**
	 * Выполнить попытку закрытия позиции.
	 * 
	 * @param priceShift сдвиг цены в шагах от текущей
	 * @param comment комментарий заявки
	 * @return  true нейтральная или противоположная поза, false без изменений
	 * @throws PortfolioDriverException
	 */
	public boolean tryClose(int priceShift, String comment)
		throws PortfolioException, InterruptedException;

}
