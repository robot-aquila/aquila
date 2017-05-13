package ru.prolib.aquila.utils.experimental.charts.series;

import org.threeten.extra.Interval;
import ru.prolib.aquila.core.data.SeriesImpl;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.layers.TradeInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Created by TiM on 13.05.2017.
 */
public class TradeInfoSeries extends SeriesImpl<List<TradeInfo>> {

    private final TimeFrame timeFrame;

    public TradeInfoSeries(String valueId, TimeFrame timeFrame) {
        super(valueId);
        this.timeFrame = timeFrame;
    }

    public TradeInfoSeries(String valueId, TimeFrame timeFrame, List<TradeInfo> initialList) throws ValueException {
        this(valueId, timeFrame);
        List<TradeInfo> list = initialList.stream().sorted(Comparator.comparing(TradeInfo::getTime)).collect(Collectors.toList());
        for(TradeInfo ti: list){
            add(ti);
        }
    }

    public synchronized void add(TradeInfo value) throws ValueException {
        Interval interval = timeFrame.getInterval(value.getTime());
        List<TradeInfo> trades = null;
        try {
            trades = get();
        } catch (ValueException e){
            trades = new Vector<>();
            super.add(trades);
        }

        if(trades.size()>0){
            Interval lastInterval = timeFrame.getInterval(trades.get(0).getTime());
            if(interval.equals(lastInterval)){
                trades.add(value);
            } else if(interval.getStart().isAfter(lastInterval.getStart())){
                trades = new Vector<>();
                super.add(trades);
                trades.add(value);
            } else {
                throw new ValueException("We can change only last value");
            }

        } else {
            trades.add(value);
        }
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }
}
