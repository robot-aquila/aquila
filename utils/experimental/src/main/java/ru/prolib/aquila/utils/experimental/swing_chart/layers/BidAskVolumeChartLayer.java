package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.data.ChartLayerDataStorage;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.time.Instant;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.*;

/**
 * Created by TiM on 27.01.2017.
 */
public class BidAskVolumeChartLayer extends AbstractChartLayer<Instant, Long> {

    public static final int TYPE_BID = 1;
    public static final int TYPE_ASK = -1;

    private Color color;
    private final int type;

    public BidAskVolumeChartLayer(String id, int type) {
        super(id);
        this.type = type;
        color = type==TYPE_BID?BID_VOLUME_COLOR:ASK_VOLUME_COLOR;
    }

    public BidAskVolumeChartLayer(String id, ChartLayerDataStorage<Instant, Long> storage, int type) {
        super(id, storage);
        this.type = type;
        color = type==TYPE_BID?BID_VOLUME_COLOR:ASK_VOLUME_COLOR;
    }

    @Override
    protected void paintObject(Instant category, Long value, CoordConverter<Instant> converter, Graphics2D g) {
        int cnt = converter.getCategories().size();
        double x = converter.getX(category);
        double y = converter.getY(Double.valueOf(type * value));
        double height = Math.abs(y - converter.getY(0d));
        if(height==0){
            height = 1;
        }
        double width = converter.getStepX()*CANDLE_WIDTH_RATIO;
        width= width<CANDLE_MIN_WIDTH?CANDLE_MIN_WIDTH:width;
        g.setColor(color);
        g.fill(new Rectangle2D.Double(x - width/2, type==TYPE_BID?y:y-height, width, height));
    }

    @Override
    protected double getMaxValue(Long value) {
        return type==TYPE_BID?value.doubleValue():0d;
    }

    @Override
    protected double getMinValue(Long value) {
        return type==TYPE_BID?0d:-value.doubleValue();
    }

    @Override
    protected String createTooltipText(Long volume) {
        return String.format("%s VOLUME: %d", type==TYPE_BID?"BID":"ASK", volume);
    }
}
