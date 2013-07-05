package ru.prolib.aquila.core.report;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.trades.*;

/**
 * Конструктор отчетов.
 */
public class ReportBuilder {
	
	public ReportBuilder() {
		super();
	}
	
	/**
	 * Создать отчет по трейдам счета.
	 * <p>
	 * @param terminal терминал
	 * @param account торговый счет
	 * @return отчет
	 */
	public TradeReport
		createAccountTradeReport(Terminal terminal, Account account)
	{
		return createTerminalTradeReport(terminal,
				new AccountTradeSelector(account));
	}
	
	/**
	 * Создать базовый трейд-отчет.
	 * <p> 
	 * @param es фасад системы событий
	 * @return отчет
	 */
	public EditableTradeReport createTradeReport(EventSystem es) {
		EventDispatcher d = es.createEventDispatcher("Trades");
		return new CommonTradeReport(d, d.createType("Enter"),
				d.createType("Exit"), d.createType("Changed"));
	}
	
	/**
	 * Создать отчет по трейдам терминала.
	 * <p>
	 * @param terminal терминал
	 * @return отчет
	 */
	public TradeReport createTerminalTradeReport(Terminal terminal) {
		return createTerminalTradeReport(terminal, new StubTradeSelector());
	}
	
	private TradeReport
		createTerminalTradeReport(Terminal terminal, TradeSelector selector)
	{
		return new TerminalTradeReport(terminal, selector,
			createTradeReport(((EditableTerminal) terminal).getEventSystem()));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == ReportBuilder.class;
	}

}
