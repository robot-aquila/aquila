package ru.prolib.aquila.quik.assembler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.quik.dde.*;

/**
 * Сборщик портфеля.
 */
public class PortfolioAssembler {
	private final EditableTerminal terminal;
	private final Cache cache;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param terminal терминал
	 * @param cache кэш
	 */
	public PortfolioAssembler(EditableTerminal terminal, Cache cache) {
		super();
		this.terminal = terminal;
		this.cache = cache;
	}
	
	/**
	 * Получить терминал.
	 * <p>
	 * @return терминал
	 */
	public EditableTerminal getTerminal() {
		return terminal;
	}
	
	/**
	 * Получить DDE-кэш.
	 * <p> 
	 * @return DDE-кэш
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * Собрать портфель по деривативам на основе данных кэш-записи.
	 * <p>
	 * @param entry кэш-запись
	 * @throws PortfolioException
	 * @throws EditableObjectException
	 */
	public void adjustByCache(PortfolioFCache entry)
		throws EditableObjectException
	{
		Account account = new Account(entry.getFirmId(),
				entry.getAccountCode(), entry.getAccountCode());
		EditablePortfolio portfolio = null;
		if ( terminal.isPortfolioAvailable(account) ) {
			portfolio = terminal.getEditablePortfolio(account);
		} else {
			portfolio = terminal.createPortfolio(account);
		}
		portfolio.setBalance(entry.getBalance());
		portfolio.setCash(entry.getCash());
		portfolio.setVariationMargin(entry.getVarMargin());
		if ( portfolio.isAvailable() ) {
			portfolio.fireChangedEvent();
		} else {
			cache.registerAccount(account);
			terminal.firePortfolioAvailableEvent(portfolio);
			portfolio.setAvailable(true);
		}
		portfolio.resetChanges();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != PortfolioAssembler.class ) {
			return false;
		}
		PortfolioAssembler o = (PortfolioAssembler) other;
		return new EqualsBuilder()
			.append(o.cache, cache)
			.append(o.terminal, terminal)
			.isEquals();
	}

}
