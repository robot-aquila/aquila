package ru.prolib.aquila.utils.experimental.chart;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSpace;

public class ChartSpaceLayoutImpl implements ChartSpaceLayout {
	private static final Segment1D ZERO_SEGMENT = new Segment1D(0, 0);
	private final Segment1D dataSpace;
	private final List<RulerSpace> listRulers;
	private final List<GridLinesSetup> listGridLinesSetup;
	
	public ChartSpaceLayoutImpl(Segment1D dataSpace,
								List<RulerSpace> listRulers,
								List<GridLinesSetup> listGridLinesSetup)
	{
		this.dataSpace = dataSpace;
		this.listRulers = listRulers;
		this.listGridLinesSetup = listGridLinesSetup;
	}

	@Override
	public Segment1D getLowerRulersTotalSpace() {
		Integer c1 = null, c2 = null;
		for ( RulerSpace s : listRulers ) {
			RulerID id = s.getRulerID();
			if ( id.isLowerPosition() ) {
				Segment1D segment = s.getSpace();
				if ( c1 == null ) {
					c1 = segment.getStart();
					c2 = segment.getEnd();
				} else {
					c1 = Math.min(segment.getStart(), c1);
					c2 = Math.max(segment.getEnd(), c2);
				}
			}
		}
		if ( c1 != null && c2 != null ) {
			return new Segment1D(c1, c2 - c1 + 1);
		}
		return ZERO_SEGMENT;
	}

	@Override
	public Segment1D getUpperRulersTotalSpace() {
		Integer c1 = null, c2 = null;
		for ( RulerSpace s : listRulers ) {
			RulerID id = s.getRulerID();
			if ( id.isUpperPosition() ) {
				Segment1D segment = s.getSpace();
				if ( c1 == null ) {
					c1 = segment.getStart();
					c2 = segment.getEnd();
				} else {
					c1 = Math.min(segment.getStart(), c1);
					c2 = Math.max(segment.getEnd(), c2);
				}
			}
		}
		if ( c1 != null && c2 != null ) {
			return new Segment1D(c1, c2 - c1 + 1);
		}
		return ZERO_SEGMENT;
	}

	@Override
	public Segment1D getDataSpace() {
		return dataSpace;
	}

	@Override
	public List<RulerSpace> getRulersToDisplay() {
		return listRulers;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ChartSpaceLayoutImpl.class ) {
			return false;
		}
		ChartSpaceLayoutImpl o = (ChartSpaceLayoutImpl) other;
		return new EqualsBuilder()
				.append(o.dataSpace, dataSpace)
				.append(o.listRulers, listRulers)
				.append(o.listGridLinesSetup, listGridLinesSetup)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("dataSpace", dataSpace)
				.append("rulers", listRulers)
				.append("gridLines", listGridLinesSetup)
				.toString();
	}

	@Override
	public List<GridLinesSetup> getGridLinesToDisplay() {
		return listGridLinesSetup;
	}

}
