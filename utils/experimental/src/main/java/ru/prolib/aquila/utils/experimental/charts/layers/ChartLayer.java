package ru.prolib.aquila.utils.experimental.charts.layers;

import javafx.scene.Node;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.utils.experimental.charts.Chart;

import java.util.List;

/**
 * Created by TiM on 26.01.2017.
 */
public interface ChartLayer<TCategories, TValues> {

    void setChart(Chart<TCategories> chart);

    void setCategories(Series<TCategories> categories);

    Series<TCategories> getCategories();

    void setData(Series<TValues> data);

    Series<TValues> getData();

    void clearData();

    List<Node> paint();

    Pair<Double, Double> getValuesInterval(List<TCategories> displayCategories);

    String getTooltip(TCategories category);
}
