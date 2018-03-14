package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

/**
 * Prepared ruler class to use for value axis with SWING rendering device.
 */
public class SWPreparedRulerVA implements PreparedRuler {
	private final SWRendererCallbackVA renderer;
	private final ValueAxisDisplayMapper mapper;
	private final List<RLabel> labels;
	private final Font labelFont;
	
	public SWPreparedRulerVA(SWRendererCallbackVA renderer,
						   ValueAxisDisplayMapper mapper,
						   List<RLabel> labels,
						   Font labelFont)
	{
		this.renderer = renderer;
		this.mapper = mapper;
		this.labels = labels;
		this.labelFont = labelFont;
	}
	
	SWRendererCallbackVA getRenderer() {
		return renderer;
	}
	
	ValueAxisDisplayMapper getDisplayMapper() {
		return mapper;
	}
	
	List<RLabel> getLabels() {
		return labels;
	}
	
	Font getLabelFont() {
		return labelFont;
	}

	@Override
	public void drawRuler(RulerSetup setup, Rectangle target, Object device) {
		renderer.drawRuler(setup, target, (Graphics2D) device, mapper, labels, labelFont);
	}

	@Override
	public void drawGridLines(Rectangle plot, Object device) {
		renderer.drawGridLines(plot, (Graphics2D) device, mapper, labels);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SWPreparedRulerVA.class ) {
			return false;
		}
		SWPreparedRulerVA o = (SWPreparedRulerVA) other;
		return new EqualsBuilder()
				.append(o.renderer, renderer)
				.append(o.labels, labels)
				.append(o.mapper, mapper)
				.append(o.labelFont, labelFont)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("builder", renderer)
				.append("mapper", mapper)
				.append("labels", labels)
				.append("labelFont", labelFont)
				.toString();
	}

}
