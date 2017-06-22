package ru.prolib.aquila.utils.experimental.swing_chart.layers;

import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;

import java.util.List;

/**
 * Created by TiM on 13.06.2017.
 */
public interface ChartLayer<TCategories, TValues> {

    public String getId();

    void setCategories(Series<TCategories> categories);

    Series<TCategories> getCategories();

    void setData(Series<TValues> data);

    Series<TValues> getData();

    void clearData();

    void paint(CoordConverter<TCategories> converter);

    Pair<Double, Double> getValuesInterval(List<TCategories> displayCategories);

    String getTooltip(TCategories category);


}
