package ru.prolib.aquila.utils.experimental.chart.interpolator;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 02.06.2017.
 */
public class SmoothLineRenderer implements LineRenderer {

    @Override
    public Shape renderLine(List<Point> points) {
        List<List<Point>> list = new ArrayList<>();
        List<Point> lastList = new ArrayList<>();
        list.add(lastList);
        for(int i=0; i<points.size(); i++){
            Point p = points.get(i);
            if(p==null){
                if(lastList.size()<2){
                    lastList.clear();
                } else {
                    lastList = new ArrayList<>();
                    list.add(lastList);
                }
            } else {
                lastList.add(p);
            }
        }
        Path2D path = new Path2D.Double();
        for(List<Point> l: list){
            if(l.size()>1){
                List<Segment> segments = CubicCurveCalc.calc(l);
                int i=0;
                for(Segment s: segments){
                    if(i==0){
                        path.moveTo(s.getX1(), s.getY1());
                        path.curveTo(s.getXc1(), s.getYc1(), s.getXc2(), s.getYc2(), s.getX2(), s.getY2());
                    } else {
                        path.curveTo(s.getXc1(), s.getYc1(), s.getXc2(), s.getYc2(), s.getX2(), s.getY2());
                    }
                    i++;
                }
            }
        }
        return path;
    }
}
