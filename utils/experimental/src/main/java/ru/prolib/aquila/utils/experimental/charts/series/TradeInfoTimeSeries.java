package ru.prolib.aquila.utils.experimental.charts.series;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.layers.TradeInfo;

import java.time.Instant;
import java.util.List;

/**
 * Created by TiM on 13.05.2017.
 */
public class TradeInfoTimeSeries implements Series<Instant> {

    private TradeInfoSeries trades;

    public TradeInfoTimeSeries(Series<List<TradeInfo>> trades) {
        this.trades = (TradeInfoSeries) trades;
    }

    @Override
    public String getId() {
        return trades.getId() + ".TIME";
    }

    @Override
    public Instant get() throws ValueException {
        return getTime(trades.get());
    }

    @Override
    public Instant get(int index) throws ValueException {
        return getTime(trades.get(index));
    }

    @Override
    public int getLength() {
        return trades.getLength();
    }

    private Instant getTime(List<TradeInfo> list) throws ValueException {
        if(list.size()>0){
            return trades.getTimeFrame().getInterval(list.get(0).getTime()).getStart();
        } else {
            throw new ValueException("TradeInfo list is empty");
        }
    }
}
