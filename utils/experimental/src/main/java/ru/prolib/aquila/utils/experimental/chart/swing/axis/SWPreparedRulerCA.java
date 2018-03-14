package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

/**
 * Prepared ruler class to use for category axis with SWING rendering device.
 */
public class SWPreparedRulerCA implements PreparedRuler {
	private final SWRendererCallbackCA renderer;
	private final CategoryAxisDisplayMapper mapper;
	private final List<RLabel> labels;
	private final Font labelFont;
	
	public SWPreparedRulerCA(SWRendererCallbackCA renderer,
							CategoryAxisDisplayMapper mapper,
							List<RLabel> labels,
							Font labelFont)
	{
		this.renderer = renderer;
		this.mapper = mapper;
		this.labels = labels;
		this.labelFont = labelFont;
	}
	
	SWRendererCallbackCA getRenderer() {
		return renderer;
	}
	
	CategoryAxisDisplayMapper getDisplayMapper() {
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
		renderer.drawRuler(setup,
						target,
						(Graphics2D) device,
						mapper,
						labels,
						labelFont);
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
		if ( other == null || other.getClass() != SWPreparedRulerCA.class ) {
			return false;
		}
		SWPreparedRulerCA o = (SWPreparedRulerCA) other;
		return new EqualsBuilder()
				.append(o.labelFont, labelFont)
				.append(o.labels, labels)
				.append(o.mapper, mapper)
				.append(o.renderer, renderer)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ "[renderer=" + renderer
				+ " mapper=" + mapper
				+ " font=" + labelFont
				+ " labels=" + labels
				+ "]";
	}

}
