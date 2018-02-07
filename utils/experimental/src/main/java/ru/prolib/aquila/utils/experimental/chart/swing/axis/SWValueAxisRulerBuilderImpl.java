package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.axis.Ruler;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisRulerBuilder;

public class SWValueAxisRulerBuilderImpl implements ValueAxisRulerBuilder {
	
	public static class Label {
		private final CDecimal value;
		private final String text;
		private final int coord;
		
		public Label(CDecimal value, String text, int coord) {
			this.value = value;
			this.text = text;
			this.coord = coord;
		}
		
		public CDecimal getValue() {
			return value;
		}
		
		public String getText() {
			return text;
		}
		
		public int getCoord() {
			return coord;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != Label.class ) {
				return false;
			}
			Label o = (Label) other;
			return new EqualsBuilder()
					.append(o.value, value)
					.append(o.text, text)
					.append(o.coord, coord)
					.isEquals();
		}
		
		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("value", value)
					.append("text", text)
					.append("coord", coord)
					.toString();
		}
		
	}
	
	private CDecimal tickSize;
	private Font labelFont;
	
	public SWValueAxisRulerBuilderImpl() {
		tickSize = CDecimalBD.of("0.01");
		labelFont = LABEL_FONT;
	}
	
	public synchronized CDecimal getTickSize() {
		return tickSize;
	}
	
	public synchronized void setTickSize(CDecimal tickSize) {
		this.tickSize = tickSize;
	}
	
	public synchronized Font getLabelFont() {
		return labelFont;
	}
	
	public synchronized void setLabelFont(Font labelFont) {
		this.labelFont = labelFont;
	}

	@Override
	public synchronized int getMaxLabelWidth(Object device) {
		// TODO: to use label formatter
		return ((Graphics2D) device).getFontMetrics(labelFont).stringWidth("000000000000") + 5;
	}

	@Override
	public synchronized int getMaxLabelHeight(Object device) {
		return ((Graphics2D) device).getFontMetrics(labelFont).getHeight() + 5;
	}

	@Override
	public synchronized Ruler prepareRuler(ValueAxisDisplayMapper mapper, Object device) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
