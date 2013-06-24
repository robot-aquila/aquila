package ru.prolib.aquila.ib.assembler;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Фасад сборщика.
 */
public class Assembler {
	
	public void update(ContractEntry entry) {
		
	}
	
	public void update(OrderEntry entry) {
		
	}
	
	public void update(OrderStatusEntry entry) {
		
	}
	
	public void update(PositionEntry entry) {
		
	}
	
	public void update(ExecEntry entry) {
		
	}
	
	/**
	 * Обновить атрибут портфеля.
	 * <p>
	 * @param account торговый счет
	 * @param setter сеттер атрибута
	 * @param value значение
	 */
	public void updatePortfolio(Account account, S<EditablePortfolio> setter,
			Double value)
	{
		/*
		EditablePortfolio portfolio;
		Account account = new Account(accountName);
		if ( terminal.isPortfolioAvailable(account) ) {
			portfolio = terminal.getEditablePortfolio(account);
		} else {
			portfolio = terminal.createPortfolio(account);
		}
		
		// TODO: обновить портфель прямо здесь
		
		if ( portfolio.hasChanged() ) {
			if ( portfolio.isAvailable() ) {
				portfolio.fireChangedEvent();
			} else {
				portfolio.setAvailable(true);
				terminal.firePortfolioAvailableEvent(portfolio);
			}
			portfolio.resetChanges();
		}
		*/
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Assembler.class ) {
			return false;
		}
		return true;
	}

}
