package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabel;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelGenerator;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.MTFLabelMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

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
	
	public SWTimeAxisRulerRenderer(String id) {
		this(id, SW2MTFAdapterImpl.getInstance(), LABEL_FONT);
	}
	
	public SWTimeAxisRulerRenderer(String id, TSeries<Instant> categories) {
		this(id);
		this.categories = categories;
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
		SWTimeAxisRulerSetup setup = (SWTimeAxisRulerSetup) s;
		AxisDirection dir = mapper.getAxisDirection();
		if ( dir.isVertical() ) {
			throw new UnsupportedOperationException("Axis direction is not supported: " + dir); 
		}
		graphics.setFont(labelFont);
		FontMetrics fontMetrics = graphics.getFontMetrics();
		int y, x;
		if ( setup.getRulerID().isLowerPosition() ) {
			y = target.getLowerY();
			if ( setup.isShowInnerLine() ) {
				graphics.drawLine(target.getLeftX(), y, target.getRightX(), y);
			}
			if ( setup.isShowOuterLine() ) {
				graphics.drawLine(target.getLeftX(), target.getUpperY(), target.getRightX(), target.getUpperY());
			}
			for ( RLabel label : labels ) {
				x = label.getCoord();
				graphics.drawLine(x, target.getUpperY(), x, target.getLowerY());
				graphics.drawString(label.getText(), x + 2, y - 2);
			}
		} else {
			y = target.getUpperY();
			int textY = fontMetrics.getAscent() + y;
			if ( setup.isShowInnerLine() ) {
				graphics.drawLine(target.getLeftX(), y, target.getRightX(), y);
			}
			if ( setup.isShowOuterLine() ) {
				graphics.drawLine(target.getLeftX(), target.getLowerY(), target.getRightX(), target.getLowerY());
			}
			for ( RLabel label : labels ) {
				x = label.getCoord();
				graphics.drawLine(x, target.getUpperY(), x, target.getLowerY());
				graphics.drawString(label.getText(), x + 2, textY);
			}
		}
	}

	@Override
	public void drawGridLines(Rectangle plot,
						Graphics2D graphics,
						CategoryAxisDisplayMapper mapper,
						List<RLabel> labels)
	{
		AxisDirection dir = mapper.getAxisDirection(); 
		if ( dir.isVertical() ) {
			throw new UnsupportedOperationException("Axis direction is not supported: " + dir);
		}
		int y1 = plot.getUpperY(), y2 = plot.getLowerY();
		for ( RLabel label : labels ) {
			int x = label.getCoord();
			graphics.drawLine(x, y1, x, y2);
		}
	}

	@Override
	public RulerSetup createRulerSetup(RulerID rulerID) {
		return new SWTimeAxisRulerSetup(rulerID);
	}

}
