package ru.prolib.aquila.data.storage.segstor;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Symbol segment associated with date.
 */
public class SymbolDaily extends SymbolSegment implements Comparable<SymbolDaily> {
	private final DatePoint point;

	public SymbolDaily(Symbol symbol, DatePoint point) {
		super(symbol);
		this.point = point;
	}
	
	public SymbolDaily(Symbol symbol, int year, int month, int dayOfMonth) {
		this(symbol, new DatePoint(year, month, dayOfMonth));
	}
	
	public DatePoint getPoint() {
		return point;
	}

	@Override
	public int compareTo(SymbolDaily o) {
		return new CompareToBuilder()
				.append(getSymbol(), o.getSymbol())
				.append(point, o.point)
				.toComparison();
	}
	
	@Override
	public String toString() {
		return getSymbol() + "[" + point.getDate() + "]";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1824927, 144801)
				.append(getSymbol())
				.append(point)
				.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolDaily.class ) {
			return false;
		}
		SymbolDaily o = (SymbolDaily) other;
		return new EqualsBuilder()
				.append(getSymbol(), o.getSymbol())
				.append(point, o.point)
				.isEquals();
	}
	
	public SymbolMonthly toMonthly() {
		return new SymbolMonthly(getSymbol(),
				point.getDate().getYear(),
				point.getDate().getMonthValue());
	}
	
	public SymbolAnnual toAnnual() {
		return new SymbolAnnual(getSymbol(), point.getDate().getYear());
	}

}
