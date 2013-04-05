package ru.prolib.aquila.ta;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.text.StrBuilder;

/**
 * Шаблон последовательности баров.
 */
public class BarSequencePattern {
	private final List<BarPattern> bars;
	
	public BarSequencePattern(List<BarPattern> bars) {
		super();
		this.bars = new LinkedList<BarPattern>(bars);
	}
	
	public List<BarPattern> getBars() {
		return bars;
	}
	
	public int getLength() {
		return bars.size();
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o == this ) {
			return true;
		}
		if ( o instanceof BarSequencePattern ) {
			BarSequencePattern other = (BarSequencePattern) o;
			List<BarPattern> otherBars = other.getBars();
			if ( otherBars.size() != bars.size() ) {
				return false;
			}
			for ( int i = 0; i < bars.size(); i ++ ) {
				if ( ! bars.get(i).equals(otherBars.get(i)) ) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder b = new HashCodeBuilder().append(bars.size());
		for ( int i = 0; i < bars.size(); i ++ ) {
			b.append(bars.get(i).hashCode());
		}
		return b.toHashCode();
	}
	
	@Override
	public String toString() {
		return new StrBuilder()
			.append("[")
			.appendWithSeparators(bars, "; ")
			.append("]")
			.toString();
	}

}
