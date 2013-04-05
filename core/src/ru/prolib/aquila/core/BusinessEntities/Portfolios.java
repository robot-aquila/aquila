package ru.prolib.aquila.core.BusinessEntities;

import java.util.List;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс набора портфелей.
 * <p>
 * 2012-08-03<br>
 * $Id: Portfolios.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface Portfolios {
	
	/**
	 * Проверить доступность информации о портфеле.
	 * <p>
	 * @param account идентификатор портфеля
	 * @return true если информация доступна, иначе - false
	 */
	public boolean isPortfolioAvailable(Account account);
	
	/**
	 * Получить тип события: при доступности информации по портфелю.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPortfolioAvailable();
	
	/**
	 * Получить список доступных портфелей.
	 * <p>
	 * @return список портфелей
	 */
	public List<Portfolio> getPortfolios();
	
	/**
	 * Получить портфель по идентификатору.
	 * <p>
	 * @param account счет портфеля
	 * @return экземпляр портфеля
	 * @throws PortfolioNotExistsException
	 */
	public Portfolio getPortfolio(Account account) throws PortfolioException;
	
	/**
	 * Получить портфель по-умолчанию.
	 * <p>
	 * Метод возвращает портфель в зависимости от реализации терминала. Это
	 * может быть единственный доступный портфель или первый попавшийся портфель
	 * из набора доступных.
	 * <p>
	 * @return портфель по-умолчанию
	 */
	public Portfolio getDefaultPortfolio() throws PortfolioException;
	
	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPortfolioChanged();
	
	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionAvailable();
	
	/**
	 * Перехватчик событий соответствующего типа от всех портфелей.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPositionChanged();
	
	/**
	 * Получить количество доступных портфелей.
	 * <p>
	 * @return количество портфелей
	 */
	public int getPortfoliosCount();

}
