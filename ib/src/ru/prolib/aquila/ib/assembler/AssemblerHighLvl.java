package ru.prolib.aquila.ib.assembler;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.IBEditableTerminal;

/**
 * Высокоуровневые функции сборки.
 */
public class AssemblerHighLvl {
	private final IBEditableTerminal terminal;
	
	public AssemblerHighLvl(IBEditableTerminal terminal) {
		super();
		this.terminal = terminal;
	}
	
	/**
	 * Получить портфель по счету.
	 * <p>
	 * Создает или возвращает существующий портфель, соответствующий
	 * указанному счету.
	 * <p>
	 * @param accountName код торгового счета
	 * @return портфель
	 * @throws PortfolioException 
	 */
	public EditablePortfolio getPortfolio(String accountName)
		throws PortfolioException
	{
		Account account = new Account(accountName);
		if ( terminal.isPortfolioAvailable(account) ) {
			return terminal.getEditablePortfolio(account);
		} else {
			return terminal.createPortfolio(account);
		}
	}
	
	/**
	 * Генерировать события портфеля.
	 * <p>
	 * В зависимости от состояния портфеля генерирует соответствующие события.
	 * <p>
	 * @param portfolio экземпляр портфеля
	 * @throws EditableObjectException 
	 */
	public void fireEvents(EditablePortfolio portfolio)
		throws EditableObjectException
	{
		if ( portfolio.hasChanged() ) {
			if ( portfolio.isAvailable() ) {
				portfolio.fireChangedEvent();
			} else {
				portfolio.setAvailable(true);
				terminal.firePortfolioAvailableEvent(portfolio);
			}
			portfolio.resetChanges();
		}
	}

}
