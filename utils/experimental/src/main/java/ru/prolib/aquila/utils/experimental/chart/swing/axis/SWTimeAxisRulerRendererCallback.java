package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

public class SWTimeAxisRulerRendererCallback implements SWRendererCallbackCA {
	private static final SWTimeAxisRulerRendererCallback instance;
	
	static {
		instance = new SWTimeAxisRulerRendererCallback();
	}
	
	public static SWRendererCallbackCA getInstance() {
		return instance;
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
		graphics.setColor(Color.BLACK);
		FontMetrics fontMetrics = graphics.getFontMetrics();
		int y, x, label_count = labels.size(), last_label_index = label_count - 1;
		if ( setup.getRulerID().isLowerPosition() ) {
			y = target.getLowerY();
			if ( setup.isShowInnerLine() ) {
				graphics.drawLine(target.getLeftX(), y, target.getRightX(), y);
			}
			if ( setup.isShowOuterLine() ) {
				graphics.drawLine(target.getLeftX(), target.getUpperY(), target.getRightX(), target.getUpperY());
			}
			for ( int i = 0; i < label_count; i ++ ) {
				RLabel label = labels.get(i);
				x = label.getCoord();
				graphics.drawLine(x, target.getUpperY(), x, target.getLowerY());
				boolean show_text = true;
				if ( i == last_label_index ) {
					int w = fontMetrics.stringWidth(label.getText());
					if ( x + 2 + w > target.getRightX() ) {
						show_text = false;
					}
				}
				if ( show_text ) {
					graphics.drawString(label.getText(), x + 2, y - 2);
				}
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
			for ( int i = 0; i < label_count; i ++ ) {
				RLabel label = labels.get(i);
				x = label.getCoord();
				graphics.drawLine(x, target.getUpperY(), x, target.getLowerY());
				boolean show_text = true;
				if ( i == last_label_index ) {
					int w = fontMetrics.stringWidth(label.getText());
					if ( x + 2 + w > target.getRightX() ) {
						show_text = false;
					}
				}
				if ( show_text ) {
					graphics.drawString(label.getText(), x + 2, textY);
				}
			}
		}
	}

	@Override
	public void drawGridLines(GridLinesSetup setup,
			Rectangle plot,
			Graphics2D graphics,
			CategoryAxisDisplayMapper mapper,
			List<RLabel> labels)
	{
		AxisDirection dir = mapper.getAxisDirection(); 
		if ( dir.isVertical() ) {
			throw new UnsupportedOperationException("Axis direction is not supported: " + dir);
		}
		int y1 = plot.getUpperY(), y2 = plot.getLowerY();
		graphics.setColor(Color.GRAY);
		for ( RLabel label : labels ) {
			int x = label.getCoord();
			graphics.drawLine(x, y1, x, y2);
		}
	}

}
