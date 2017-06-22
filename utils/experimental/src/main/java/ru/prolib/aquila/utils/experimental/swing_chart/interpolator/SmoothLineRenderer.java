package ru.prolib.aquila.utils.experimental.swing_chart.interpolator;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * Created by TiM on 02.06.2017.
 */
public class SmoothLineRenderer implements LineRenderer {

    @Override
    public Shape renderLine(List<Point> points) {
        List<Segment> segments = CubicCurveCalc.calc(points);
        int i=0;
        Path2D path = new Path2D.Double();
        for(Segment s: segments){
            if(i==0){
                path.moveTo(s.getX1(), s.getY1());
            } else {
                path.curveTo(s.getXc1(), s.getYc1(), s.getXc2(), s.getYc2(), s.getX2(), s.getY2());
            }
            i++;
        }
        return path;
    }
}
