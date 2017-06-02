package ru.prolib.aquila.utils.experimental.charts.interpolator;

import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import java.util.List;

/**
 * Created by TiM on 02.06.2017.
 */
public class SmoothLineRenderer implements LineRenderer{

    @Override
    public void renderLine(Path path, List<Segment> segments) {
        for(Segment s: segments){
            PathElement element;
            if(path.getElements().size()==0){
                element = new MoveTo(s.getX1(), s.getY1());
                path.getElements().add(element);
            }
            element = new CubicCurveTo(s.getXc1(), s.getYc1(),
                    s.getXc2(), s.getYc2(), s.getX2(), s.getY2());
            path.getElements().add(element);
        }
    }
}
