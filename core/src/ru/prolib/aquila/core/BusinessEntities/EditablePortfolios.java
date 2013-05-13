package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс редактируемого набора портфелей.
 * <p>
 * 2012-08-17<br>
 * $Id: EditablePortfolios.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public interface EditablePortfolios extends Portfolios {
	
	/**
	 * Генерировать событие о появлении информации о новом портфеле.
	 * <p>
	 * @param portfolio портфель
	 */
	public void firePortfolioAvailableEvent(Portfolio portfolio);
	
	/**
	 * Получить экземпляр редактируемого портфеля.
	 * <p>
	 * @param account идентификатор счета
	 * @return портфель
	 * @throws PortfolioNotExistsException
	 */
	public EditablePortfolio getEditablePortfolio(Account account)
			throws PortfolioException;
	
	/**
	 * Создать и зарегистрировать новый портфель.
	 * <p>
	 * @param terminal терминал
	 * @param account торговый счет портфеля
	 * @throws PortfolioAlreadyExistsException
	 */
	public EditablePortfolio
		createPortfolio(EditableTerminal terminal, Account account)
			throws PortfolioException;
	
	/**
	 * Установить портфель по-умолчанию.
	 * <p>
	 * @param portfolio экземпляр портфеля
	 */
	public void setDefaultPortfolio(EditablePortfolio portfolio);

}
