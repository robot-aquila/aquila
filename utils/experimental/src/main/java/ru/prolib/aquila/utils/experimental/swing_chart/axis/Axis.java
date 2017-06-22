package ru.prolib.aquila.utils.experimental.swing_chart.axis;


import ru.prolib.aquila.utils.experimental.swing_chart.CoordConverter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.DefaultLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;

import java.awt.*;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.LABEL_FONT;

/**
 * Created by TiM on 18.06.2017.
 */
public abstract class Axis<T> {
    protected final int position;

    protected boolean showLabels = false;
    protected Font font = LABEL_FONT;
    protected LabelFormatter labelFormatter;


    public Axis(int position) {
        this.position = position;
        labelFormatter = new DefaultLabelFormatter();
    }

    public abstract void paint(CoordConverter<T> coordConverter);

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setLabelFormatter(LabelFormatter labelFormatter) {
        this.labelFormatter = labelFormatter;
    }
}
