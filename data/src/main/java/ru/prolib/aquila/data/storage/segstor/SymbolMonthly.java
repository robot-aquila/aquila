package ru.prolib.aquila.data.storage.segstor;

import java.time.Month;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Symbol segment associated with year and month.
 */
public class SymbolMonthly extends SymbolSegment implements Comparable<SymbolMonthly> {
	private final MonthPoint point;

	public SymbolMonthly(Symbol symbol, MonthPoint point) {
		super(symbol);
		this.point = point;
	}
	
	public SymbolMonthly(Symbol symbol, int year, Month month) {
		this(symbol, new MonthPoint(year, month));
	}
	
	public SymbolMonthly(Symbol symbol, int year, int month) {
		this(symbol, year, Month.of(month));
	}
	
	public MonthPoint getPoint() {
		return point;
	}

	@Override
	public int compareTo(SymbolMonthly o) {
		return new CompareToBuilder()
				.append(getSymbol(), o.getSymbol())
				.append(point, o.point)
				.toComparison();
	}
	
	@Override
	public String toString() {
		return getSymbol() + "[" + point.getYear() + ", " + point.getMonth() + "]";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(6517, 91192231)
				.append(getSymbol())
				.append(point)
				.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolMonthly.class ) {
			return false;
		}
		SymbolMonthly o = (SymbolMonthly) other;
		return new EqualsBuilder()
				.append(o.getSymbol(), getSymbol())
				.append(o.point, point)
				.isEquals();
	}
	
	public SymbolAnnual toAnnual() {
		return new SymbolAnnual(getSymbol(), point.getYear());
	}

}
