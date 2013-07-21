package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс хранилища портфелей.
 */
public interface EditablePortfolios extends Portfolios {
	
	/**
	 * Генерировать события портфеля.
	 * <p>
	 * @param portfolio портфель
	 */
	public void fireEvents(EditablePortfolio portfolio);
	
	/**
	 * Получить экземпляр редактируемого портфеля.
	 * <p>
	 * Если портфель не существует, то он будет создан.
	 * <p>
	 * @param terminal терминал
	 * @param account идентификатор счета
	 * @return портфель
	 */
	public EditablePortfolio
		getEditablePortfolio(EditableTerminal terminal, Account account);
	
	/**
	 * Установить портфель по-умолчанию.
	 * <p>
	 * @param portfolio экземпляр портфеля
	 */
	public void setDefaultPortfolio(EditablePortfolio portfolio);

}
