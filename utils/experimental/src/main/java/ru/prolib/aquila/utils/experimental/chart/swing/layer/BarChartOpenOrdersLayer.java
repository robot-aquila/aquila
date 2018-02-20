package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;

import java.awt.*;
import java.util.*;
import java.util.List;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.*;

/**
 * Created by TiM on 06.10.2017.
 */
public class BarChartOpenOrdersLayer extends BarChartAbstractLayer {
	public static final int BUY_COLOR = 0;
	public static final int SELL_COLOR = 1;
	
	public static final int ACCOUNTS_PARAM = 0;
	public static final int LINE_WIDTH_PARAM = 1;
	
	private final Series<TradeInfoList> data;
	protected final Map<Integer, Color> colors = new HashMap<>();
	protected final Map<Integer, Object> params = new HashMap<>();
	private List<Account> accounts;
	
	public BarChartOpenOrdersLayer(Series<TradeInfoList> data) {
		super(data.getId());
		this.data = data;
		setColor(BUY_COLOR, COLOR_BULL);
		setColor(SELL_COLOR, COLOR_BEAR);
		setParam(LINE_WIDTH_PARAM, 2f);
	}

	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		// TODO: fixme, I have this range
		return null;
	}

	@Override
	protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		
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
