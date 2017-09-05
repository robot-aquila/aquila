package ru.prolib.aquila.utils.experimental.swing_chart.axis;

import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.RangeInfo;

import javax.swing.*;
import java.awt.*;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.LABEL_INDENT;
import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.Y_AXIS_WIDTH;

/**
 * Created by TiM on 20.06.2017.
 */
public class ValueAxis<T> extends Axis<T> {

    public ValueAxis(int position, LabelFormatter labelFormatter) {
        super(position);
        this.labelFormatter = labelFormatter;
    }

    public double getSize(){
        return showLabels?Y_AXIS_WIDTH:0;
    }

    @Override
    public void paint(CoordConverter<T> cc) {
        if(showLabels){
            Graphics2D g = (Graphics2D) cc.getGraphics().create();
            try {
                FontMetrics metrics = g.getFontMetrics(font);
                g.setFont(font);
                RangeInfo ri = cc.getYRangeInfo();
                for(Double yVal=ri.getFirstValue(); yVal<=ri.getLastValue()+1e-6; yVal+=ri.getStepValue()){
                    String label = labelFormatter.format(yVal);
                    float width = metrics.stringWidth(label);
                    float height = metrics.getHeight();
                    double y = cc.getY(yVal) + height/4;
                    double x;
                    if(position == SwingConstants.LEFT){
                        x = cc.getPlotBounds().getMinX() - LABEL_INDENT - width;
                    } else {
                        x = cc.getPlotBounds().getMaxX() + LABEL_INDENT;
                    }
                    g.drawString(label, (int)x, (int)y);
                }
            }finally {
                g.dispose();
            }
        }
    }
}
