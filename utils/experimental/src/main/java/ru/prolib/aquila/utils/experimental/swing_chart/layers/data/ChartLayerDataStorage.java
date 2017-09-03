package ru.prolib.aquila.utils.experimental.swing_chart.layers.data;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Created by TiM on 02.09.2017.
 */
public interface ChartLayerDataStorage<TCategories, TValues> {
    Series<TCategories> getCategories();

    void setCategories(Series<TCategories> categories);

    Series<TValues> getData();

    void setData(Series<TValues> data);

    void clearData();

    TValues getByCategory(TCategories category) throws ValueException;
}
