package ru.prolib.aquila.utils.experimental.chart.interpolator;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;

/**
 * Created by TiM on 02.06.2017.
 */
public class PolyLineRenderer implements LineRenderer {

    @Override
    public Shape renderLine(List<Point> points) {
        boolean needMove = true;
        Path2D path = new Path2D.Double();
        if(points.size()>0){
            for(int i=0; i<points.size(); i++){
                Point p = points.get(i);
                if(p==null){
                    needMove = true;
                } else {
                    if(needMove){
                        path.moveTo(p.getX(), p.getY());
                        needMove = false;
                    } else {
                        path.lineTo(p.getX(), p.getY());
                    }
                }
            }
        }
        return path;
    }
}
