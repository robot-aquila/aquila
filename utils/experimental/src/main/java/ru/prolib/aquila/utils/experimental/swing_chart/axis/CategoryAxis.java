package ru.prolib.aquila.utils.experimental.swing_chart.axis;

import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import sun.java2d.SunGraphics2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.LABEL_INDENT;

/**
 * Created by TiM on 18.06.2017.
 */
public class CategoryAxis<T> extends Axis<T> {
    private final List<T> categories;

    private int labelOrientation = SwingConstants.VERTICAL;

    public CategoryAxis(int position, List<T> categories, LabelFormatter labelFormatter) {
        super(position);
        this.categories = categories;
        this.labelFormatter = labelFormatter;
    }

    public double getSize(Graphics2D g){
        return getLabelDimension(g).getHeight();
    }

    public Dimension getLabelDimension(Graphics2D g){
        FontMetrics metrics = g.getFontMetrics(font);
        int maxWidth = 0, maxHeight = 0;
        if(showLabels){
            for(T category: categories){
                String label = labelFormatter.format(category);
                int width = metrics.stringWidth(label);
                int height = metrics.getHeight();
                if(labelOrientation == SwingConstants.VERTICAL){
                    if(height > maxWidth){
                        maxWidth = height;
                    }
                    if(width > maxHeight){
                        maxHeight = width;
                    }
                } else {
                    if(width > maxWidth){
                        maxWidth = width;
                    }
                    if(height > maxHeight){
                        maxHeight = height;
                    }
                }
            }
        }
        return new Dimension(maxWidth, maxHeight);
    }

    public void paint(CoordConverter<T> coordConverter) {
        if (showLabels) {
            Graphics2D g = (Graphics2D) coordConverter.getGraphics().create();
            try {
                FontMetrics metrics = g.getFontMetrics(font);
                g.setFont(font);
                Dimension maxLabelDimension = getLabelDimension(g);
                double maxWidth = maxLabelDimension.getWidth();
                double maxHeight = maxLabelDimension.getHeight();

                int scaleCoeff = (int) Math.floor(maxWidth * 2 / coordConverter.getStepX()) + 1;
//            System.out.printf("maxWidth=%f; maxHeight=%f; scaleCoeff=%d%n", maxWidth, maxHeight, scaleCoeff);

                if (labelOrientation == SwingConstants.VERTICAL) {
                    g.setTransform(AffineTransform.getQuadrantRotateInstance(3));
                }
                for (int i = 0; i < categories.size(); i++) {
                    Double x = coordConverter.getX(categories.get(i));
                    if (x != null) {
                        if ((i + 1) % scaleCoeff == 0) {
                            String label = labelFormatter.format(categories.get(i));
                            float width = metrics.stringWidth(label);
                            float height = metrics.getHeight();
                            double y = 0;
                            if (position == SwingConstants.TOP) {
                                y = coordConverter.getPlotBounds().getMinY() - LABEL_INDENT;
                            } else {
                                y = coordConverter.getPlotBounds().getMaxY() + LABEL_INDENT + (labelOrientation == SwingConstants.VERTICAL ? width : height);
                            }
                            if (labelOrientation == SwingConstants.VERTICAL) {
                                g.drawString(label, -(float) y, (float) (x + height / 4.));
                            } else {
                                g.drawString(label, x.floatValue() - width / 2, (float) y);
                            }
                        }
                    }
                }
            } finally {
                g.dispose();
            }
        }
    }

    public void setLabelOrientation(int labelOrientation) {
        this.labelOrientation = labelOrientation;
    }

}
