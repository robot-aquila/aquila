package ru.prolib.aquila.ta;

import org.apache.commons.lang3.text.StrBuilder;

import ru.prolib.aquila.core.data.Candle;

/**
 * Разворотная точка.
 */
public class Pivot extends Candle {
	private final PivotType type;
	
	public Pivot(PivotType type, Candle bar) {
		super(bar.getId(), bar);
		this.type = type;
	}
	
	public PivotType getType() {
		return type;
	}
	
	public boolean isMax() {
		return type == PivotType.MAX;
	}
	
	public boolean isMin() {
		return type == PivotType.MIN;
	}
	
	@Override
	public String toString() {
		return new StrBuilder()
			.append("Pivot[")
			.append(getId())
			.append("]")
			.append(type)
			.append(type == PivotType.MAX ? getHigh() : getLow())
			.append(" at ")
			.append(getTime())
			.toString();
	}
	
	public int distance(Candle other) {
		return Math.abs(getId() - other.getId()); 
	}

}