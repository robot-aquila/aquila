package ru.prolib.aquila.utils.experimental.charts.interpolator;

import javafx.scene.shape.Path;

import java.util.List;

/**
 * Created by TiM on 02.06.2017.
 */
public interface LineRenderer {
    void renderLine(Path path, List<Segment> segments);
}
