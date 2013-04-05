package ru.prolib.aquila.test;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.PortfolioException;

public interface TestPortfolioStateController {
	
	/**
	 * Установить баланс кошелька.
	 * 
	 * Изменять баланс кошелька можно только до открытия периода.
	 * 
	 * @param money
	 * @throws TestPortfolioStatePeriodOpenedException
	 */
	public void setMoney(double money)
		throws TestPortfolioStatePeriodOpenedException;
	
	/**
	 * Установить текущую позицию.
	 * 
	 * Установить позицию напрямую можно только до открытия периода.
	 * 
	 * @param position
	 * @throws TestPortfolioStatePeriodOpenedException
	 */
	public void setPosition(int position)
		throws TestPortfolioStatePeriodOpenedException;
	
	/**
	 * Закрыть период.
	 * 
	 * Даный метод выполняет перевод накопленной вариационной маржи на баланс
	 * кошелька. Если период не открыт, то никаких действий не выполняется.
	 */
	public void closePeriod() throws PortfolioException;
	
	/**
	 * Открыть период.
	 * 
	 * Устанавливает все необходимые для расчета вариационной маржи и ГО
	 * атрибуты. При наличии открытой позиции, блокирует ГО и расчитывает
	 * первоначальную стоимость позиции по расчетной цене актива. Если на момент
	 * вызова период уже открыт, то вызов завершится выбросом исключения.
	 * 
	 * @param asset
	 * @throws TestPortfolioStatePeriodOpenedException
	 */
	public void openPeriod(Asset asset)
		throws TestPortfolioStatePeriodOpenedException,
		 	   PortfolioException;
	
	/**
	 * Получить баланс кошелька.
	 * 
	 * @return
	 */
	public double getMoney();
	
	/**
	 * Получить размер текущей позиции.
	 * @return
	 */
	public int getPosition();
	
	/**
	 * Получить размер накопленной вариационной маржи в деньгах.
	 * 
	 * Возвращает размер накопленной вариационной маржи в соответствии с текущей
	 * ценой актива. Накопленная вариационная маржа рассчитывается по формуле:
	 * 
	 *  margin - (pos * close) * price step money / price step
	 *  
	 * где:
	 * margin - баланс маржи в пунктах с учетом открытой позиции
	 * pos - открытая позиция
	 * close - последняя цена актива
	 * price step money - стоимость шага цены
	 * price step - шаг цены в пунктах 
	 *
	 * Если период не открыт, всегда возвращает ноль.
	 * @return
	 */
	public double getVariationMargin() throws PortfolioException;
	
	/**
	 * Получить размер ГО по открытой позиции в деньгах.
	 * 
	 * ГО расчитывается по формуле:
	 * 
	 * 	abs(pos) * initial margin money
	 * 
	 * где:
	 * abs(pos) - позиция в абсолютном выражении
	 * initial margin money - ГО единицы позиции в деньгах
	 * 
	 * Если период не открыт, всегда возвращает ноль.
	 * @return
	 */
	public double getInitialMargin() throws PortfolioException;
	
	/**
	 * Изменить позицию.
	 * 
	 * Вызов метода возможен только после открытия периода.
	 * 
	 * @param delta величина изменения позиции
	 * @param price цена за единицу позиции в пунктах 
	 * @throws TestPortfolioStatePeriodNotOpenedException
	 */
	public void changePosition(int delta, double price)
		throws TestPortfolioStatePeriodNotOpenedException,
			   PortfolioException;
	
	/**
	 * Изменить позицию.
	 * 
	 * Изменяет позицию аналогично одноименному методу с двумя аргументами,
	 * но в качестве цены используется последняя цена актива (т.е. рыночная).
	 * 
	 * @param delta величина изменения позиции
	 * @throws TestPortfolioStatePeriodNotOpenedException
	 */
	public void changePosition(int delta)
		throws TestPortfolioStatePeriodNotOpenedException,
			   PortfolioException;

}
