package ru.prolib.aquila.utils.experimental.charts.objects;

import javafx.scene.Node;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.utils.experimental.charts.Chart;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by TiM on 26.01.2017.
 */
public interface ChartObject {

    void setChart(Chart chart);

    List<LocalDateTime> getXValues();

    List<Node> paint();

    Pair<Double, Double> getYInterval();
}
