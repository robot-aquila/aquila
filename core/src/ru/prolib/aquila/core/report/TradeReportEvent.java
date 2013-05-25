package ru.prolib.aquila.core.report;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;

/**
 * События в связи с отчетом по трейду.
 */
public class TradeReportEvent extends EventImpl {
	private final TradeReport report;

	public TradeReportEvent(EventType type, TradeReport report) {
		super(type);
		this.report = report;
	}

	/**
	 * Получить отчет.
	 * <p>
	 * @return отчет
	 */
	public TradeReport getReport() {
		return report;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TradeReportEvent.class ) {
			return false;
		}
		TradeReportEvent o = (TradeReportEvent) other;
		return new EqualsBuilder()
			.append(o.report, report)
			.append(o.getType(), getType())
			.isEquals();
	}

}
