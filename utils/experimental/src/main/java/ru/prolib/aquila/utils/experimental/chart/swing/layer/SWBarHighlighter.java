package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.SelectedCategoryTracker;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;

import java.awt.Color;
import java.awt.Graphics2D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SWBarHighlighter extends SWAbstractLayer {
	public static final String DEFAULT_ID = "BAR_HIGHLIGHTER";
	public static final int HIGHLIGHT_COLOR = 1;
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SWBarHighlighter.class);
	}
	
	private final SelectedCategoryTracker tracker;

	public SWBarHighlighter(SelectedCategoryTracker tracker, String layerID) {
		super(layerID);
		setColor(HIGHLIGHT_COLOR, new Color(192, 192, 192, 128));
		this.tracker = tracker;
	}
	
	public SWBarHighlighter(SelectedCategoryTracker tracker) {
		this(tracker, DEFAULT_ID);
	}
	
	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		return null;
	}
	
	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		if ( ! tracker.isSelected() ) {
			return;
		}
		CategoryAxisDisplayMapper cam = context.getCategoryAxisMapper();
		if ( cam.getAxisDirection().isVertical() ) {
			logger.warn("Axis direction now unsupported: " + cam.getAxisDirection());
			return;
		}
		Rectangle rect = context.getCanvasArea();
		Segment1D bar = cam.toDisplay(tracker.getAbsoluteIndex());
		graphics.setColor(getColor(HIGHLIGHT_COLOR));
		graphics.fillRect(bar.getStart(), rect.getUpperY(), bar.getLength(), rect.getHeight());
	}

}
