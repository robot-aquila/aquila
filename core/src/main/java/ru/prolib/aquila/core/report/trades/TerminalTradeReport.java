package ru.prolib.aquila.core.report.trades;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.report.RTrade;
import ru.prolib.aquila.core.report.ReportBuilder;
import ru.prolib.aquila.core.report.TradeReport;
import ru.prolib.aquila.core.report.TradeSelector;

/**
 * Реализация трейд-отчета по сделкам терминала и селектору.
 * <p>
 * Используй {@link ReportBuilder} для инстанцирования экземпляра.
 */
public class TerminalTradeReport implements TradeReport, EventListener {
	private final Terminal terminal;
	private final TradeSelector selector;
	private final EditableTradeReport report;
	
	public TerminalTradeReport(Terminal terminal, TradeSelector selector,
			EditableTradeReport report)
	{
		super();
		this.terminal = terminal;
		this.selector = selector;
		this.report = report;
	}
	
	Terminal getTerminal() {
		return terminal;
	}
	
	TradeSelector getTradeSelector() {
		return selector;
	}
	
	EditableTradeReport getUnderlyingReport() {
		return report;
	}

	@Override
	public void start() throws StarterException {
		report.start();
		terminal.OnOrderTrade().addSyncListener(this);
	}

	@Override
	public void stop() throws StarterException {
		terminal.OnOrderTrade().removeListener(this);
		report.stop();
	}

	@Override
	public int size() {
		return report.size();
	}

	@Override
	public List<RTrade> getRecords() {
		return report.getRecords();
	}

	@Override
	public RTrade getRecord(int index) {
		return report.getRecord(index);
	}

	@Override
	public EventType OnEnter() {
		return report.OnEnter();
	}

	@Override
	public EventType OnExit() {
		return report.OnExit();
	}

	@Override
	public EventType OnChanged() {
		return report.OnChanged();
	}

	@Override
	public void onEvent(Event event) {
		final OrderTradeEvent e = (OrderTradeEvent) event;
		if ( selector.mustBeAdded(e.getTrade(), e.getOrder()) ) {
			report.addTrade(e.getTrade());
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TerminalTradeReport.class ) {
			return false;
		}
		TerminalTradeReport o = (TerminalTradeReport) other;
		return new EqualsBuilder()
			.appendSuper(o.terminal == terminal)
			.append(o.report, report)
			.append(o.selector, selector)
			.isEquals();
		
	}

	@Override
	public RTrade getCurrent(SecurityDescriptor descr) {
		return report.getCurrent(descr);
	}

	@Override
	public RTrade getCurrent(Security security) {
		return report.getCurrent(security);
	}

	@Override
	public long getPosition(SecurityDescriptor descr) {
		return report.getPosition(descr);
	}

	@Override
	public long getPosition(Security security) {
		return report.getPosition(security);
	}

}
