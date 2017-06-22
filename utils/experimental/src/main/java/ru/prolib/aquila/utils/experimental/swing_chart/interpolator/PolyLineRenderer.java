package ru.prolib.aquila.utils.experimental.swing_chart.interpolator;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * Created by TiM on 02.06.2017.
 */
public class PolyLineRenderer implements LineRenderer {

    @Override
    public Shape renderLine(List<Point> points) {
        Path2D path = new Path2D.Double();
        int i=0;
        for(Point p: points){
            if(i==0){
                path.moveTo(p.getX(), p.getY());
            } else {
                path.lineTo(p.getX(), p.getY());
            }
            i++;
        }
        return path;
    }
}
