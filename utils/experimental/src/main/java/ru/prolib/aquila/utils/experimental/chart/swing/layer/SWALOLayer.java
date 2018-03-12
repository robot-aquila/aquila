package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.data.ALOData;
import ru.prolib.aquila.utils.experimental.chart.data.ALODataProvider;

import java.awt.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Active limit orders layer.
 * <p>
 * This class is SWING implementation of a layer to highlight price level
 * of active orders (or any volumes to buy or to sell).
 */
public class SWALOLayer extends SWAbstractLayer {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SWALOLayer.class);
	}
	
	/**
	 * Color of buy orders highlight line.<br>
	 * Type: Color<br>
	 * Default: green
	 */
	public static final int COLOR_BUY_ORDER = 0;
	
	/**
	 * Color of sell orders highlight line.<br>
	 * Type: Color<br>
	 * Default: red
	 */
	public static final int COLOR_SELL_ORDER = 1;
	
	/**
	 * Width of price level line.<br>
	 * Type: float<br>
	 * Default: 1
	 */
	public static final int PARAM_LINE_WIDTH = 1;

	private final ALODataProvider dataProvider;
	
	public SWALOLayer(String layerID, ALODataProvider dataProvider) {
		super(layerID);
		setColor(COLOR_BUY_ORDER, new Color(0, 128, 0));
		setColor(COLOR_SELL_ORDER, new Color(210, 0, 0));
		setParam(PARAM_LINE_WIDTH, 2f);
		this.dataProvider = dataProvider;
	}

	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		if ( ! isVisible() ) {
			return null;
		}
		CDecimal minPrice = null, maxPrice = null;
		for ( ALOData data : dataProvider.getOrderVolumes() ) {
			CDecimal p = data.getPrice();
			minPrice = p.min(minPrice);
			maxPrice = p.max(maxPrice);
		}
		return ( minPrice == null || maxPrice == null ) ? null : new Range<>(minPrice, maxPrice);
	}

	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		ValueAxisDisplayMapper vMapper = context.getValueAxisMapper();
		if ( ! vMapper.getAxisDirection().isVertical() ) {
			logger.warn("Value axis direction is not supported: " + vMapper.getAxisDirection());
			return;
		}
		int x1 = context.getPlotArea().getLeftX(), x2 = context.getPlotArea().getRightX();
		float lw = (float) getParam(PARAM_LINE_WIDTH);
		Stroke cStroke = new BasicStroke(lw);
		float dp[] = { 40f, 40f };
		Stroke dStroke = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dp, 0.0f);
		Color bvColor = getColor(COLOR_BUY_ORDER), svColor = getColor(COLOR_SELL_ORDER);
		for ( ALOData data : dataProvider.getOrderVolumes() ) {
			int y = vMapper.toDisplay(data.getPrice());
			boolean hasBV = data.getTotalBuyVolume().compareTo(CDecimalBD.ZERO) != 0,
					hasSV = data.getTotalSellVolume().compareTo(CDecimalBD.ZERO) != 0;
			if ( hasBV ) {
				graphics.setStroke(cStroke);
				graphics.setColor(bvColor);
				graphics.drawLine(x1, y, x2, y);
			}
			if ( hasSV ) {
				graphics.setStroke(hasBV ? dStroke : cStroke);
				graphics.setColor(svColor);
				graphics.drawLine(x1, y, x2, y);
			}
		}
	}

    /*
    @Override
    public void paint(BarChartVisualizationContext context) {
        if(!visible){
            return;
        }
        Graphics2D g = (Graphics2D) getGraphics(context).create();
        data.lock();
        try {
            TradeInfoList value = null;
            int i = data.getLength() - 1;
            while (value == null && i >= 0){
                value = data.get(i--);
            }
            if(value!=null){
                paint(context, g, value);
            }
        } catch (ValueException e) {
            e.printStackTrace();
        } finally {
            g.dispose();
            data.unlock();
        }
    }

    protected void paint(BarChartVisualizationContext context, Graphics2D g, TradeInfoList value){
        if(value == null){
            return;
        }
        Set<Integer> yValuesBuy = new HashSet<>();
        Set<Integer> yValuesSell = new HashSet<>();
        Set<Integer> yDashed = new HashSet<>();
        value = new TradeInfoList(value, accounts);
        int xStartLine = context.getPlotBounds().getUpperLeftX();
        int xEndLine = xStartLine + context.getPlotBounds().getWidth();

        float lineWidth = (Float) params.get(LINE_WIDTH_PARAM);
        Stroke stroke = new BasicStroke(lineWidth);
        float[] dashedPattern = {40f, 40f};
        Stroke dashedStroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, dashedPattern, 0.0f);
        for(int i=0; i<value.size(); i++){
            TradeInfo ti = value.get(i);
            int y = context.toCanvasY(ti.getPrice());
            if(yDashed.contains(y)){
                continue;
            }
            g.setStroke(stroke);
            if(OrderAction.BUY.equals(ti.getAction())){
                g.setColor(colors.get(BUY_COLOR));
                if(yValuesSell.contains(y)){
                    g.setStroke(dashedStroke);
                    yDashed.add(y);
                }
                yValuesBuy.add(y);
            } else {
                g.setColor(colors.get(SELL_COLOR));
                if(yValuesBuy.contains(y)){
                    g.setStroke(dashedStroke);
                    yDashed.add(y);
                }
                yValuesSell.add(y);
            }
            g.drawLine(xStartLine, y, xEndLine, y);
        }
    }
    */
}
