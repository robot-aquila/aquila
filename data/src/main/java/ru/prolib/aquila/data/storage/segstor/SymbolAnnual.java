package ru.prolib.aquila.data.storage.segstor;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

/**
 * Symbol segment associated with the year.
 */
public class SymbolAnnual extends SymbolSegment implements Comparable<SymbolAnnual> {
	private final YearPoint point;

	public SymbolAnnual(Symbol symbol, YearPoint point) {
		super(symbol);
		this.point = point;
	}
	
	public SymbolAnnual(Symbol symbol, int year) {
		this(symbol, new YearPoint(year));
	}
	
	public YearPoint getPoint() {
		return point;
	}

	@Override
	public int compareTo(SymbolAnnual o) {
		return new CompareToBuilder()
				.append(getSymbol(), o.getSymbol())
				.append(point, o.point)
				.toComparison();
	}
	
	@Override
	public String toString() {
		return getSymbol() + "[" + point.getYear() + "]";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(19009, 532671)
				.append(getSymbol())
				.append(point)
				.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolAnnual.class ) {
			return false;
		}
		SymbolAnnual o = (SymbolAnnual) other;
		return new EqualsBuilder()
				.append(getSymbol(), o.getSymbol())
				.append(point, o.point)
				.isEquals();
	}

}
