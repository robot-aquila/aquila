package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.BCDisplayContext;
import ru.prolib.aquila.utils.experimental.chart.Polygon2D;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.data.OEEntry;
import ru.prolib.aquila.utils.experimental.chart.data.OEEntrySet;

import java.awt.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Order executions layer.
 * <p>
 * This class is SWING implementation of a layer to highlight all order executions.
 */
public class SWOELayer extends SWAbstractLayer {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SWOELayer.class);
	}

	public static final int COLOR_ARROW_UP  = 1;
	public static final int COLOR_ARROW_DOWN = 2;
	
	/**
	 * Width of arrow basis in pixels.<br>
	 * Type: int<br>
	 * Default: 20
	 */
	public static final int PARAM_ARROW_WIDTH = 0;
	
	/**
	 * Height of arrow in pixels.<br>
	 * Type: int<br>
	 * Default: 10
	 */
	public static final int PARAM_ARROW_HEIGHT = 1;
	
	private Series<OEEntrySet> entries;
	
	public SWOELayer(Series<OEEntrySet> entries) {
		super(entries.getId());
		this.entries = entries;
		setColor(COLOR_ARROW_UP,	 Color.GREEN);
		setColor(COLOR_ARROW_DOWN,	 Color.PINK);
		setParam(PARAM_ARROW_WIDTH,	 20);
		setParam(PARAM_ARROW_HEIGHT, 10);
	}
    
	@Override
	public Range<CDecimal> getValueRange(int first, int number) {
		Range<CDecimal> vr = null, cvr;
		OEEntrySet eset;
		entries.lock();
		try {
			for ( int i = 0; i < number; i ++ ) {
				try {
					eset = entries.get(first + i);
				} catch ( ValueException e ) {
					logger.error("Error accessing value: ", e);
					continue;
				}
				if ( eset == null ) {
					continue;
				}
				cvr = new Range<>(eset.getMinPrice(), eset.getMaxPrice());
				vr = cvr.extend(vr);
			}
		} finally {
			entries.unlock();
		}
		return vr;
	}
    
    @Override
    protected void paintLayer(BCDisplayContext context, Graphics2D graphics) {
		CategoryAxisDisplayMapper cMapper = context.getCategoryAxisMapper();
		ValueAxisDisplayMapper vMapper = context.getValueAxisMapper();
		if ( cMapper.getAxisDirection().isVertical() ) {
			logger.warn("Axis direction now unsupported: " + cMapper.getAxisDirection());
			return;
		}
		Color clrBody, clrBodyB = getColor(COLOR_ARROW_UP), clrBodyS = getColor(COLOR_ARROW_DOWN);
		int arrHalfWidth = (int) getParam(PARAM_ARROW_WIDTH) / 2, arrHeight = (int) getParam(PARAM_ARROW_HEIGHT); 
		entries.lock();
		try {
			int last = cMapper.getLastVisibleCategory(), y1, y2;
			OEEntrySet eset;
			Segment1D barSegment;
			for ( int i = cMapper.getFirstVisibleCategory(); i <= last; i ++ ) {
				try {
					eset = entries.get(i);
				} catch ( ValueException e ) {
					logger.error("Error accessing data: ", e);
					continue;
				}
				if ( eset == null ) {
					continue;
				}
				barSegment = cMapper.toDisplay(i);
				int xc = barSegment.getMidpoint();
				for ( OEEntry entry : eset.getEntries() ) {
					y1 = vMapper.toDisplay(entry.getPrice());
					if ( entry.isBuy() ) {
						clrBody = clrBodyB;
						y2 = y1 + arrHeight;
					} else {
						clrBody = clrBodyS;
						y2 = y1 - arrHeight;
					}
					Polygon2D poly = new Polygon2D()
						.addPointEx(xc, y1)
						.addPointEx(xc - arrHalfWidth, y2)
						.addPointEx(xc + arrHalfWidth, y2);
					graphics.setColor(clrBody);
					graphics.fillPolygon(poly);
				}
			}
		} finally {
			entries.unlock();
		}
    }

    /*
    @Override
    protected void paintObject(int categoryIdx, TradeInfoList tradeInfoList, BarChartVisualizationContext context, Graphics2D g) {
        if (tradeInfoList != null) {
            tradeInfoList = new TradeInfoList(tradeInfoList, accounts);
            Color color = colors.get(LINE_COLOR);
            int x = context.toCanvasX(categoryIdx);
            for (int j = 0; j < tradeInfoList.size(); j++) {
                TradeInfo tradeInfo = tradeInfoList.get(j);
                int y = context.toCanvasY(tradeInfo.getPrice());
                int y2;
                Color fillColor;
                if (tradeInfo.getAction().equals(OrderAction.BUY)) {
                    y2 = y + HEIGHT;
                    fillColor = colors.get(BUY_COLOR);
                } else {
                    y2 = y - HEIGHT;
                    fillColor = colors.get(SELL_COLOR);
                }
                int[] xPoints = {x, x - WIDTH / 2, x + WIDTH / 2};
                int[] yPoints = {y, y2, y2};
                g.setColor(color);
                g.drawPolygon(xPoints, yPoints, 3);
                g.setColor(fillColor);
                g.fillPolygon(xPoints, yPoints, 3);
            }
        }
    }

    @Override
    public Range<CDecimal> getValueRange(int first, int number) {
        CDecimal minY = CDecimalBD.ZERO;
        CDecimal maxY = CDecimalBD.ZERO;
        if ( ! visible || data == null ) {
            return null;
        }
        data.lock();
        try {
            for(int i=first; i< first + number; i++){
                TradeInfoList value = null;
                try {
                    value = data.get(i);
                } catch (ValueException e) {
                    value = null;
                }
                if(value!=null) {
                    value = new TradeInfoList(value, accounts);
                }
                if(value!=null && value.size()>0){
                    maxY = maxY.max(getMaxValue(value));
                    minY = minY.min(getMinValue(value));
                }
            }
        } finally {
            data.unlock();
        }
        if(minY!=null && maxY!=null){
            return Range.between(minY, maxY);
        }
        return null;
    }

    @Override
    protected CDecimal getMaxValue(TradeInfoList value) {
    	CDecimal x = value.getMaxValue();
        return x == null ? CDecimalBD.ZERO : x;
    }

    @Override
    protected CDecimal getMinValue(TradeInfoList value) {
    	CDecimal x = value.getMinValue();
        return x == null ? CDecimalBD.ZERO : x;
    }

    @Override
    protected String createTooltipText(TradeInfoList value, LabelFormatter labelFormatter) {
        value = new TradeInfoList(value, accounts);
        if(value.size()>0){
            StringBuilder sb = new StringBuilder("Orders:\n");
            for(int i=0; i< value.size(); i++){
                TradeInfo ti = value.get(i);
                sb.append(ti.getOrderId());
                sb.append(";\n");
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public BarChartLayer<TCategory> setParam(int paramId, Object value) {
        super.setParam(paramId, value);
        if(paramId == ACCOUNTS_PARAM && value instanceof List){
            accounts = (List<Account>) value;
        }
        return this;
    }
    */
}
