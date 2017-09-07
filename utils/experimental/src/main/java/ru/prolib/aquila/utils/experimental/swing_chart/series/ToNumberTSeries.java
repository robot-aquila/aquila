package ru.prolib.aquila.utils.experimental.swing_chart.series;

import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.data.ValueException;

import java.time.Instant;

/**
 * Created by TiM on 04.09.2017.
 */
public class ToNumberTSeries implements TSeries<Number> {
    TSeries series;

    public ToNumberTSeries(TSeries series) {
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
        return (Number) series.get();
    }

    @Override
    public Number get(int index) throws ValueException {
        return (Number) series.get(index);
    }

    @Override
    public int getLength() {
        return series.getLength();
    }

    @Override
    public Number get(Instant time) {
        return (Number) series.get(time);
    }

    @Override
    public TimeFrame getTimeFrame() {
        return series.getTimeFrame();
    }

    @Override
    public int toIndex(Instant time) {
        return series.toIndex(time);
    }
}
