package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.TimeCategoryDataProvider;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;

public class TimeAxisRendererImpl extends SwingAxisRenderer {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TimeAxisRendererImpl.class);
	}
	
	private Font labelFont;
	private TimeCategoryDataProvider dataProvider;
	private DateTimeFormatter labelFormatDate, labelFormatTime;

	public TimeAxisRendererImpl(AxisPosition position, TimeCategoryDataProvider dataProvider) {
		super(position);
		this.labelFont = LABEL_FONT;
		this.dataProvider = dataProvider;
		labelFormatDate = DateTimeFormatter.ofPattern("YYYY-MM-dd");
		labelFormatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
	}
	
	public TimeCategoryDataProvider getDataProvider() {
		return dataProvider;
	}

	public Font getLabelFont() {
		return labelFont;
	}
	
	public void setLabelFont(Font font) {
		this.labelFont = font;
	}

	@Override
	protected Rectangle getRulerArea(ChartLayout layout, Graphics2D graphics) {
		if ( position != AxisPosition.BOTTOM && position != AxisPosition.TOP ) {
			throw new IllegalStateException("Unsupported position: " + position);
		}
		if ( ! isVisible() ) {
			return null;
		}
		Rectangle root = layout.getRoot();
		int width = root.getWidth();
		int height = getLabelDimension(graphics).getHeight();
		int y = position == AxisPosition.BOTTOM ?
				root.getLowerY() - height + 1:
				root.getUpperY();
		// TODO: Special cases
		// 1) axis area may be greater than root
		// 2) the root may have not enough space (possible case for plot auto calculation)
		return new Rectangle(new Point2D(0, y), width, height, root);
	}
	
	@Override
	protected void paintRuler(BCDisplayContext context, Graphics2D graphics) {
		graphics.setFont(labelFont);
		ChartLayout layout = context.getChartLayout();
		Rectangle axisRect = null, labelRect = getLabelDimension(graphics);
		TSeries<Instant> categories = dataProvider.getCategories();
		ZoneId zoneID = categories.getTimeFrame().getZoneID();
		CategoryAxisDisplayMapper cMapper = context.getCategoryAxisMapper();
		switch ( position ) {
		case TOP:
		case BOTTOM:
			int lineY = 0, dateY = 0, timeY = 0;
			if ( position == AxisPosition.TOP ) {
				// TODO: use closest Y only
				axisRect = layout.getTopAxis();
				lineY = axisRect.getLowerY();
				dateY = lineY - 2 - axisRect.getHeight() / 2;
				timeY = lineY - 2;
				
			} else if ( position == AxisPosition.BOTTOM ) {
				// TODO: use closest Y only
				axisRect = layout.getBottomAxis();
				lineY = axisRect.getUpperY();
				dateY = axisRect.getLowerY() - 2;
				timeY = axisRect.getLowerY() - 2 - axisRect.getHeight() / 2;
			}
			graphics.drawLine(axisRect.getLeftX(), lineY,
					  axisRect.getRightX(), lineY);
			int prevLabelRightX = 0;
			// Show labels
			int n = cMapper.getNumberOfVisibleCategories();;
			for ( int i = cMapper.getFirstVisibleCategory(); i < n; i ++  ) {
				Segment1D segment = cMapper.toDisplay(i);
				int labelLeftX = segment.getStart() + (segment.getLength() / 2);
				int labelRightX = labelLeftX + labelRect.getWidth();
				if ( labelLeftX > prevLabelRightX && labelRightX <= axisRect.getRightX() ) {
					prevLabelRightX = labelRightX;
					graphics.drawLine(labelLeftX, axisRect.getUpperY(),
									  labelLeftX, axisRect.getLowerY());
					try {
						ZonedDateTime zdt = ZonedDateTime.ofInstant(categories.get(i), zoneID) ;
						graphics.drawString(labelFormatDate.format(zdt), labelLeftX + 2, dateY);
						graphics.drawString(labelFormatTime.format(zdt), labelLeftX + 2, timeY);
					} catch ( ValueException e ) {
						logger.error("Error painting label. Possible invalid viewport: ", e);
					}
				}
			}
			break;
		case LEFT:
		case RIGHT:
		default:
			throw new IllegalStateException("Unsupported axis position: " + position);
		}
	}
	
	private Rectangle getLabelDimension(Graphics2D graphics) {
		// Default label format is:
		//yyyy-mm-dd
		// hh:mm:ss
		FontMetrics fm = graphics.getFontMetrics(labelFont);
		return new Rectangle(Point2D.ZERO, fm.stringWidth("0000-00-00") + 2, fm.getHeight() * 2 + 3);
	}
	
	/*
    public void paint(BarChartVisualizationContext context, AxisLabelProvider labelProvider) {
        if(!isVisible()){
            return;
        }
        Graphics2D g = (Graphics2D) getGraphics(context).create();
        try {
            g.setFont(LABEL_FONT);
            FontMetrics metrics = g.getFontMetrics(LABEL_FONT);

            int maxWidth = 0;
            for (int i=0; i<labelProvider.getLength(); i++) {
                int width = metrics.stringWidth(labelProvider.getLabel(i, labelFormatter));
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }	

            int scaleCoeff = (int) Math.floor(maxWidth * 2 / context.getStepX()) + 1;

            for (int i = 0; i < labelProvider.getLength(); i++) {
                int x = labelProvider.getCanvasX(i);
                if ((i + 1) % scaleCoeff == 0) {
                    String label = labelProvider.getLabel(i, labelFormatter);
                    float width = metrics.stringWidth(label);
                    float height = metrics.getHeight();
                    float y = 0;
                    if (position == POSITION_TOP) {
                        y = context.getPlotBounds().getUpperLeftY() - LABEL_INDENT;
                    } else {
                        y = context.getPlotBounds().getUpperLeftY() + context.getPlotBounds().getHeight() + LABEL_INDENT + height;
                    }
                    g.drawString(label, x - width / 2, y);
                }
            }
        } finally {
            g.dispose();
        }
    }
    */

}
