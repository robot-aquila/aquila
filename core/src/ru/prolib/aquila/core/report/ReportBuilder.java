package ru.prolib.aquila.core.report;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Конструктор отчетов.
 */
public class ReportBuilder {
	
	public ReportBuilder() {
		super();
	}
	
	/**
	 * Создать отчет по трейдам портфеля.
	 * <p>
	 * Примечание: Портфель должен быть связан с экземпляром терминала,
	 * реализующим интерфейс {@link EditableTerminal}. 
	 * <p>
	 * @param portfolio портфель
	 * @return отчет
	 */
	public Trades createPortfolioTrades(Portfolio portfolio) {
		EventDispatcher d = ((EditableTerminal) portfolio.getTerminal())
			.getEventSystem().createEventDispatcher("Trades");
		return new PortfolioTrades(new TradesImpl(d, d.createType("Enter"),
				d.createType("Exit"), d.createType("Changed")), portfolio);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == ReportBuilder.class;
	}

}
