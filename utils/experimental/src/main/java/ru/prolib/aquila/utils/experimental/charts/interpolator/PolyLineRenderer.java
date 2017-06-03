package ru.prolib.aquila.utils.experimental.charts.interpolator;

import javafx.scene.shape.*;

import java.util.List;

/**
 * Created by TiM on 02.06.2017.
 */
public class PolyLineRenderer implements LineRenderer{

    @Override
    public void renderLine(Path path, List<Point> points) {
        for(Point p: points){
            PathElement element;
            if(path.getElements().size()==0){
                element = new MoveTo(p.getX(), p.getY());
                path.getElements().add(element);
            } else {
                element = new LineTo(p.getX(), p.getY());
                path.getElements().add(element);
            }
        }
    }
}
