package ru.prolib.aquila.core.BusinessEntities;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

abstract public class CDecimalAbstract implements CDecimal {
	private int hashCode;

	@Override
	public synchronized String toString() {
		String unit = getUnit();
		if ( unit == null ) {
			return toBigDecimal().toString();
		} else {
			return toBigDecimal().toString() + " " + unit;
		}
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || !(other instanceof CDecimal) ) {
			return false;
		}
		return hashCode() == other.hashCode();
	}

	@Override
	public int compareTo(CDecimal other) {
		if ( other == null ) {
			return 1;
		}
		if ( hashCode() == other.hashCode() ) {
			return 0;
		}
		return new CompareToBuilder()
				.append(toBigDecimal(), other.toBigDecimal())
				.append(getUnit(), other.getUnit())
				.toComparison();
	}

	@Override
	public synchronized int hashCode() {
		if ( hashCode == 0 ) {
			hashCode = new HashCodeBuilder(1592781, 77263)
					.append(toBigDecimal())
					.append(getUnit())
					.append(getRoundingMode())
					.toHashCode();
		}
		return hashCode;
	}

}