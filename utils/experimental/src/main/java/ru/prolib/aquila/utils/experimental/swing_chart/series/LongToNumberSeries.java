package ru.prolib.aquila.utils.experimental.swing_chart.series;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Created by TiM on 04.09.2017.
 */
public class LongToNumberSeries implements Series<Number> {
    Series<Long> series;

    public LongToNumberSeries(Series<Long> series) {
        this.series = series;
    }

    @Override
    public LID getLID() {
        return series.getLID();
    }

    @Override
    public void lock() {
        series.lock();
    }

    @Override
    public void unlock() {
        series.unlock();
    }

    @Override
    public String getId() {
        return series.getId();
    }

    @Override
    public Number get() throws ValueException {
        return series.get();
    }

    @Override
    public Number get(int index) throws ValueException {
        return series.get(index);
    }

    @Override
    public int getLength() {
        return series.getLength();
    }
}
