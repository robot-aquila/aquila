package ru.prolib.aquila.utils.experimental.charts.series;

import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;

import java.time.Instant;
import java.util.List;

/**
 * Created by TiM on 27.05.2017.
 */
public class StampedListTimeSeries implements Series<Instant> {

    private StampedListSeries<? extends TStamped> series;

    public StampedListTimeSeries(Series<List<? extends TStamped>> series) {
        this.series = (StampedListSeries) series;
    }

    public StampedListTimeSeries(StampedListSeries<? extends TStamped> series) {
        this.series = series;
    }

    @Override
    public String getId() {
        return series.getId() + ".TIME";
    }

    @Override
    public Instant get() throws ValueException {
        return getTime(series.get());
    }

    @Override
    public Instant get(int index) throws ValueException {
        return getTime(series.get(index));
    }

    @Override
    public int getLength() {
        return series.getLength();
    }

    private Instant getTime(List<? extends TStamped> list) throws ValueException {
        if(list.size()>0){
            return series.getTimeFrame().getInterval(list.get(0).getTime()).getStart();
        } else {
            throw new ValueException("List is empty");
        }
    }
}
