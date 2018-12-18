package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.TextOverlay;

public class SWSimpleTextOverlay extends SWAbstractLayer {
	public static final String DEFAULT_ID = "OVERLAY";
	public static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 11);
	public static final int PARAM_FONT = 1;
	public static final int COLOR_BACKGROUND = 1;
	public static final int COLOR_BORDER = 2;
	
	private final TextOverlay overlay;

	public SWSimpleTextOverlay(String layerID, TextOverlay overlay) {
		super(layerID);
		this.overlay = overlay;
		setColor(Color.BLACK);
		setColor(COLOR_BACKGROUND, new Color(160, 200, 255, 191));
		setColor(COLOR_BORDER, Color.BLACK);
		setParam(PARAM_FONT, DEFAULT_FONT);
	}
	
	public SWSimpleTextOverlay(TextOverlay overlay) {
		this(DEFAULT_ID, overlay);
	}

	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		return null;
	}

	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		if ( ! overlay.isVisible() ) {
			return;
		}
		String text = overlay.getText();
		graphics.setFont((Font) getParam(PARAM_FONT));
		FontMetrics fontMetrics = graphics.getFontMetrics();
		Rectangle plot = context.getPlotArea();
		int r1_y = plot.getUpperY() + 2;
		int r1_x = plot.getLeftX() + 2;
		int r1_w = fontMetrics.stringWidth(text) + 4;
		int r1_h = fontMetrics.getHeight() + 4;
		int t_y = r1_y + fontMetrics.getAscent() + 2;
		int t_x = r1_x + 2;
		graphics.setColor(getColor(COLOR_BACKGROUND));
		graphics.fillRect(r1_x, r1_y, r1_w, r1_h);
		graphics.setColor(getColor(COLOR_BORDER));
		graphics.setStroke(new BasicStroke(1));
		graphics.drawRect(r1_x, r1_y, r1_w, r1_h);
		graphics.setColor(getColor());
		graphics.drawString(text, t_x, t_y);
	}

}
