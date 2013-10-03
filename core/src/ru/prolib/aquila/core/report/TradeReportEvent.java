package ru.prolib.aquila.core.report;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;

/**
 * События в связи с отчетом по трейду.
 */
public class TradeReportEvent extends EventImpl {
	private final RTrade report;
	private final Integer index;

	public TradeReportEvent(EventType type, RTrade report, Integer index) {
		super(type);
		this.report = report;
		this.index = index;
	}
	
	public TradeReportEvent(EventType type, RTrade report) {
		this(type, report, null);
	}

	/**
	 * Получить отчет.
	 * <p>
	 * @return отчет
	 */
	public RTrade getReport() {
		return report;
	}
	
	/**
	 * Получить индекс отчета в последовательности.
	 * <p>
	 * @return индекс
	 */
	public Integer getIndex() {
		return index;
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
			.append(o.index, index)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return getType().toString() + "." + getClass().getSimpleName()
			+ "[#" + index + ", report=" + report + "]";
	}

}
