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
	public TradeReport createReport(Terminal terminal, Account account) {
		return createReport(terminal,
				new AccountTradeSelector(account));
	}
	
	/**
	 * Создать базовый трейд-отчет.
	 * <p> 
	 * @param es фасад системы событий
	 * @return отчет
	 */
	public EditableTradeReport createReport(EventSystem es) {
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
	public TradeReport createReport(Terminal terminal) {
		return createReport(terminal, new StubTradeSelector());
	}
	
	/**
	 * Создать отчет по трейдам пула заявок.
	 * <p>
	 * @param orders пул заявок
	 * @return отчет
	 */
	public TradeReport createReport(OrderPool orders) {
		return createReport(orders.getTerminal(),
				new OrderPoolTradeSelector(orders));
	}
	
	private TradeReport createReport(Terminal terminal, TradeSelector selector)
	{
		return new TerminalTradeReport(terminal, selector,
			createReport(((EditableTerminal) terminal).getEventSystem()));
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == ReportBuilder.class;
	}

}
