package ru.prolib.aquila.utils.experimental.swing_chart.interpolator;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by TiM on 31.01.2017.
 */
public class Segment {
    private final double x1, y1, x2, y2, xc1, yc1,xc2, yc2;

    public Segment(double x1, double y1, double x2, double y2, double xc1, double yc1, double xc2, double yc2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.xc1 = xc1;
        this.yc1 = yc1;
        this.xc2 = xc2;
        this.yc2 = yc2;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public double getXc1() {
        return xc1;
    }

    public double getYc1() {
        return yc1;
    }

    public double getXc2() {
        return xc2;
    }

    public double getYc2() {
        return yc2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("x1", x1)
                .append("y1", y1)
                .append("x2", x2)
                .append("y2", y2)
                .append("xc1", xc1)
                .append("yc1", yc1)
                .append("xc2", xc2)
                .append("yc2", yc2)
                .toString();
    }
}
