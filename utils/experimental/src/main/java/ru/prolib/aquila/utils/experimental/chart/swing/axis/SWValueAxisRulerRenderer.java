package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.ValueAxisLabelGenerator;

public class SWValueAxisRulerRenderer implements ValueAxisRulerRenderer, SWRendererCallbackVA {
	private final String id;
	private final ValueAxisLabelGenerator labelGenerator;
	private CDecimal tickSize;
	private Font labelFont;
	
	public SWValueAxisRulerRenderer(String id,
									ValueAxisLabelGenerator labelGenerator,
									CDecimal tickSize,
									Font labelFont)
	{
		this.id = id;
		this.tickSize = tickSize;
		this.labelFont = labelFont;
		this.labelGenerator = labelGenerator;
	}
	
	public SWValueAxisRulerRenderer(String id) {
		this(id, ValueAxisLabelGenerator.getInstance(), CDecimalBD.of("0.01"), LABEL_FONT);
	}
	
	ValueAxisLabelGenerator getLabelGenerator() {
		return labelGenerator;
	}
	
	public CDecimal getTickSize() {
		return tickSize;
	}
	
	public void setTickSize(CDecimal tickSize) {
		this.tickSize = tickSize;
	}
	
	public Font getLabelFont() {
		return labelFont;
	}
	
	public void setLabelFont(Font labelFont) {
		this.labelFont = labelFont;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public int getMaxLabelWidth(Object device) {
		// TODO: to use label formatter
		return ((Graphics2D) device).getFontMetrics(labelFont).stringWidth("000000000000") + 5;
	}

	@Override
	public int getMaxLabelHeight(Object device) {
		return ((Graphics2D) device).getFontMetrics(labelFont).getHeight() + 5;
	}

	@Override
	public PreparedRuler prepareRuler(ValueAxisDisplayMapper mapper,
			Object device)
	{
		AxisDirection dir = mapper.getAxisDirection();
		if ( dir.isVertical() ) {		
			int labelHeight = getMaxLabelHeight(device);
			List<CDecimal> valueList = labelGenerator.getLabelValues(mapper, tickSize, labelHeight);
			List<RLabel> labelList = new ArrayList<>();
			for ( CDecimal value : valueList ) {
				String labelText = value.toString();
				// TODO: cut label text if too long
				labelList.add(new RLabel(value, labelText, mapper.toDisplay(value)));
			}
			return new SWPreparedRulerVA(this, mapper, labelList, labelFont);
		} else {
			throw new IllegalArgumentException("Axis direction is not supported: " + dir);
		}
	}
	
	@Override
	public void drawRuler(RulerSetup setup,
						  Rectangle target,
						  Graphics2D graphics,
						  ValueAxisDisplayMapper mapper,
						  List<RLabel> labels,
						  Font labelFont)
	{
		AxisDirection dir = mapper.getAxisDirection();
		if ( dir.isHorizontal() ) {
			throw new UnsupportedOperationException("Axis direction is not supported: " + dir);	
		}
		FontMetrics fontMetrics = graphics.getFontMetrics(labelFont);
		int textHalfHeight = fontMetrics.getAscent() / 2;
		graphics.setFont(labelFont);
		if ( setup.getRulerID().isLowerPosition() ) {
			for ( RLabel label : labels ) {
				int x = target.getRightX();
				int y = label.getCoord();
				graphics.drawLine(x - 2, y, x, y);
				int labelWidth = fontMetrics.stringWidth(label.getText());
				x = x - 5 - labelWidth;
				y += textHalfHeight;
				graphics.drawString(label.getText(), x, y);
			}
		} else {
			for ( RLabel label : labels ) {
				int x = target.getLeftX();
				int y = label.getCoord();
				graphics.drawLine(x, y, x + 2, y);
				x += 5;
				y += textHalfHeight;
				graphics.drawString(label.getText(), x, y);
			}
		}
	}
	
	@Override
	public void drawGridLines(Rectangle plot,
							Graphics2D graphics,
							ValueAxisDisplayMapper mapper,
							List<RLabel> labels)
	{
		AxisDirection dir = mapper.getAxisDirection(); 
		if ( dir.isHorizontal() ) {
			throw new UnsupportedOperationException("Axis direction is not supported: " + dir);
		}
		int x1 = plot.getLeftX(), x2 = plot.getRightX();
		for ( RLabel label : labels ) {
			graphics.drawLine(x1, label.getCoord(), x2, label.getCoord());
		}
	}

	@Override
	public RulerSetup createRulerSetup(RulerID rulerID) {
		return new RulerSetup(rulerID);
	}

}
