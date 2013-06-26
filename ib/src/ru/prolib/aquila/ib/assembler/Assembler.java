package ru.prolib.aquila.ib.assembler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.ib.IBEditableTerminal;
import ru.prolib.aquila.ib.assembler.cache.*;

/**
 * Фасад сборщика.
 */
public class Assembler {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Assembler.class);
	}
	
	private final IBEditableTerminal terminal;
	private final AssemblerHighLvl high;
	
	Assembler(IBEditableTerminal terminal, AssemblerHighLvl high) {
		super();
		this.terminal = terminal;
		this.high = high;
	}
	
	public IBEditableTerminal getTerminal() {
		return terminal;
	}
	
	public Assembler(IBEditableTerminal terminal) {
		this(terminal, new AssemblerHighLvl(terminal));
	}
	
	public void update(ContractEntry entry) {
		terminal.getCache().update(entry);
	}
	
	public void update(OrderEntry entry) {
		terminal.getCache().update(entry);
	}
	
	public void update(OrderStatusEntry entry) {
		terminal.getCache().update(entry);
	}
	
	public void update(PositionEntry entry) {
		terminal.getCache().update(entry);
	}
	
	public void update(ExecEntry entry) {
		terminal.getCache().update(entry);
	}
	
	/**
	 * Обновить атрибут портфеля.
	 * <p>
	 * @param accountName код торгового счета
	 * @param setter сеттер атрибута
	 * @param value значение
	 */
	public void updatePortfolio(String accountName,
			S<EditablePortfolio> setter, Double value)
	{
		try {
			EditablePortfolio portfolio = high.getPortfolio(accountName);
			setter.set(portfolio, value);
			if ( portfolio.getCash() != null
					&& portfolio.getBalance() != null )
			{
				high.fireEvents(portfolio);
			}
		} catch ( Exception e ) {
			logger.error("Error update portfolio: ", e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Assembler.class ) {
			return false;
		}
		Assembler o = (Assembler) other;
		return o.terminal == terminal;
	}

}
