package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.ChartLayout;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.LABEL_FONT;

import java.awt.Font;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Created by TiM on 12.09.2017.
 */
public class ValueAxisRendererImpl extends SwingAxisRenderer {
	protected Font labelFont;
	protected ValueAxisDriver driver;

	public ValueAxisRendererImpl(AxisPosition position) {
		super(position);
		this.labelFont = LABEL_FONT;
	}

	@Override
	public AxisPosition getAxisPosition() {
		return position;
	}

	@Override
	public void setAxisPosition(AxisPosition position) {
		this.position = position;
	}

	@Override
	protected Rectangle getRulerArea(ChartLayout layout, Graphics2D graphics) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void paintRuler(BCDisplayContext context, Graphics2D graphics) {
		// TODO Auto-generated method stub
	}
	

	/*
	@Override
	public void paint(BarChartVisualizationContext context, AxisLabelProvider labelProvider) {
	    if(!isVisible()){
	        return;
	    }
	    Graphics2D g = (Graphics2D) getGraphics(context).create();
	    try {
	        g.setFont(LABEL_FONT);
	        FontMetrics metrics = g.getFontMetrics(LABEL_FONT);
	        for (int i=0; i<labelProvider.getLength(); i++) {
	            String label = labelProvider.getLabel(i, labelFormatter);
	            int width = metrics.stringWidth(labelProvider.getLabel(i, labelFormatter));
	            int height = metrics.getHeight();
	            int y = Math.round(labelProvider.getCanvasY(i) + height/4f);
	            int x;
	            if(position == POSITION_LEFT){
	                x = context.getPlotBounds().getUpperLeftX() - LABEL_INDENT - width;
	            } else {
	                x = context.getPlotBounds().getUpperLeftX() + context.getPlotBounds().getWidth() + LABEL_INDENT;
	            }
	            g.drawString(label, (int)x, (int)y);
	        }
	    } finally {
	        g.dispose();
	    }
	}
    */
	
}
