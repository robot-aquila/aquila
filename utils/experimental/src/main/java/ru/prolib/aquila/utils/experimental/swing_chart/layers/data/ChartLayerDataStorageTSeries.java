package ru.prolib.aquila.utils.experimental.swing_chart.layers.data;

import ru.prolib.aquila.core.data.*;

import java.time.Instant;

/**
 * Created by TiM on 02.09.2017.
 */
public class ChartLayerDataStorageTSeries<TValues> implements ChartLayerDataStorage<Instant, TValues> {

    protected TSeries<Instant> categories;
    protected TSeries<TValues> data;

    @Override
    public Series<Instant> getCategories() {
        return categories;
    }

    @Override
    public void setCategories(Series<Instant> categories) {
        this.categories = (TSeries<Instant>) categories;
    }

    @Override
    public Series<TValues> getData() {
        return data;
    }

    @Override
    public void setData(Series<TValues> data) {
        this.data = (TSeries<TValues>) data;
    }

    @Override
    public void clearData() {
        this.data = new TSeriesImpl<>(TimeFrame.M1);
        this.categories = new TSeriesImpl<>(TimeFrame.M1);
    }

    @Override
    public TValues getByCategory(Instant category) throws ValueException {
        return data.get(category);
    }
}
