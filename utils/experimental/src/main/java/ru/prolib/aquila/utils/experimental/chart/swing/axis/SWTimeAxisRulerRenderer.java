package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRendererID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabel;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelGenerator;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

/**
 * This implementation of ruler renderer isn't good enough. It shows labels
 * without considering desired priority to display labels. For example it may
 * display a label of last minute of hour instead of next hour label. It's just
 * count in on label visibility according by its dimensions. Nothing more. Use
 * another implementation instead.
 */
@Deprecated
public class SWTimeAxisRulerRenderer implements CategoryAxisRulerRenderer, SWRendererCallbackCA {
	
	public static class LabelDimensions implements SWLabelDimensions {
		private final SWTimeAxisRulerRenderer renderer;
		private final FontMetrics fontMetrics;
		
		public LabelDimensions(SWTimeAxisRulerRenderer renderer, FontMetrics fontMetrics) {
			this.renderer = renderer;
			this.fontMetrics = fontMetrics;
		}

		@Override
		public int getLabelWidth(String labelText) {
			return renderer.getLabelWidth(labelText, fontMetrics);
		}

		@Override
		public int getLabelHeight(String labelText) {
			return renderer.getLabelHeight(labelText, fontMetrics);
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != LabelDimensions.class ) {
				return false;
			}
			LabelDimensions o = (LabelDimensions) other;
			return new EqualsBuilder()
					.append(o.renderer, renderer)
					.append(o.fontMetrics, fontMetrics)
					.isEquals();
		}
		
	}
	
	private final String id;
	private final SW2MTFAdapter adapter;
	private Font labelFont;
	private TSeries<Instant> categories;
	
	public SWTimeAxisRulerRenderer(String id,
								   SW2MTFAdapter adapter,
								   Font labelFont)
	{
		this.id = id;
		this.adapter = adapter;
		this.labelFont = labelFont;
	}
	
	public SWTimeAxisRulerRenderer(String id, TSeries<Instant> categories) {
		this(id, SW2MTFAdapterImpl.getInstance(), LABEL_FONT);
		this.categories = categories;
	}
	
	public SWTimeAxisRulerRenderer(String id) {
		this(id, new TSeriesImpl<Instant>(ZTFrame.M1));
	}

	@Override
	public String getID() {
		return id;
	}
	
	SW2MTFAdapter getMTFAdapter() {
		return adapter;
	}
	
	public Font getLabelFont() {
		return labelFont;
	}
	
	public void setLabelFont(Font labelFont) {
		this.labelFont = labelFont;
	}
	
	public TSeries<Instant> getCategories() {
		return categories;
	}
	
	public void setCategories(TSeries<Instant> categories) {
		this.categories = categories;
	}
	
	public int getLabelWidth(String labelText, FontMetrics fontMetrics) {
		return fontMetrics.stringWidth(labelText) + 5;
	}
	
	public int getLabelHeight(String labelText, FontMetrics fontMetrics) {
		return fontMetrics.getHeight() + 5;
	}

	@Override
	public int getMaxLabelWidth(Object device) {
		return getLabelWidth(MTFLabelGenerator.getLargestLabelTemplate(),
				((Graphics2D) device).getFontMetrics(labelFont));
	}
	
	@Override
	public int getMaxLabelHeight(Object device) {
		return getLabelHeight("X", ((Graphics2D) device).getFontMetrics(labelFont));
	}

	@Override
	public PreparedRuler prepareRuler(CategoryAxisDisplayMapper mapper, Object device) {
		Graphics2D graphics = (Graphics2D) device;
		AxisDirection dir = mapper.getAxisDirection();
		if ( dir.isVertical() ) {
			throw new UnsupportedOperationException("Axis direction is not supported: " + dir);
		}
		LabelDimensions labelDimensions = new LabelDimensions(this, graphics.getFontMetrics(labelFont));
		MTFLabelMapper labelMapper = adapter.getLabelMapper(mapper, categories.getTimeFrame(), labelDimensions);
		int f = mapper.getFirstVisibleCategory();
		int n = mapper.getNumberOfVisibleCategories();
		categories.lock();
		try {
			List<RLabel> result = new ArrayList<>();
			Segment1D lastUsedSegment = null;
			for ( int j = 0; j < n; j ++ ) {
				int i = f + j;
				Instant t = categories.get(i);
				if ( t == null ) {
					continue;
				}
				MTFLabel label = labelMapper.convertToLabel(t);
				if ( label == null ) {
					continue;
				}
				Segment1D labelSegment = mapper.toDisplay(i);
				if ( lastUsedSegment != null
				  && lastUsedSegment.getEnd() >= labelSegment.getStart() )
				{
					continue;
				}
				// TODO: Better processing of hourly labels
				int c = labelSegment.getStart() + labelSegment.getLength() / 2;
				result.add(new RLabel(i, label.getText(), c));
				lastUsedSegment = new Segment1D(labelSegment.getStart(),
						labelDimensions.getLabelWidth(label.getText()));
			}
			return new SWPreparedRulerCA(this, mapper, result, labelFont);
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception: ", e);
		} finally {
			categories.unlock();
		}
	}

	@Override
	public void drawRuler(RulerSetup s,
						Rectangle target,
						Graphics2D graphics,
						CategoryAxisDisplayMapper mapper,
						List<RLabel> labels,
						Font labelFont)
	{
		SWTimeAxisRulerRendererCallback.getInstance()
			.drawRuler(s, target, graphics, mapper, labels, labelFont);
	}

	@Override
	public void drawGridLines(GridLinesSetup setup,
						Rectangle plot,
						Graphics2D graphics,
						CategoryAxisDisplayMapper mapper,
						List<RLabel> labels)
	{
		SWTimeAxisRulerRendererCallback.getInstance()
			.drawGridLines(setup, plot, graphics, mapper, labels);
	}

	@Override
	public RulerSetup createRulerSetup(RulerID rulerID) {
		return new SWTimeAxisRulerSetup(rulerID);
	}

	@Override
	public GridLinesSetup createGridLinesSetup(RulerRendererID rendererID) {
		return new GridLinesSetup(rendererID);
	}

}
