package ru.prolib.aquila.utils.experimental.chart.axis;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.utils.experimental.chart.Segment1D;

/**
 * Область отображения линейки.
 * <p>
 * Область отображения линейки это отрезок на координатной оси экрана,
 * зарезервированный для отрисовки линейки. Какая конкретно координатная ось
 * экрана имеется в виду, зависит от ориентации проекции исходной оси значений.
 * В случае, если ось значений проецируется на экран вертикально, область
 * отображения описывает координату X и ширину выделенной области экрана. Если
 * ось проецируется на экран горизонтально, дескриптор описывает координату Y и
 * высоту области отображения линейки. 
 */
public class ChartRulerSpace {
	private final ChartRulerID rulerID;
	private final Segment1D space;
	
	public ChartRulerSpace(ChartRulerID rulerID, Segment1D space) {
		this.rulerID = rulerID;
		this.space = space;
	}
	
	public ChartRulerID getRulerID() {
		return rulerID;
	}
	
	public Segment1D getSpace() {
		return space;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ChartRulerSpace.class ) {
			return false;
		}
		ChartRulerSpace o = (ChartRulerSpace) other;
		return new EqualsBuilder()
				.append(o.rulerID, rulerID)
				.append(o.space, space)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + rulerID + ", " + space + "]";
	}

}
