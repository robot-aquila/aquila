package ru.prolib.aquila.utils.experimental.chart;

import java.awt.*;

/**
 * Created by TiM on 20.06.2017.
 */
public class ChartConstants {
    public static final int MARGIN = 10;
    public static int OTHER_CHARTS_HEIGHT = 150;

    public static int INITIAL_NUMBER_OF_VISIBLE_CATEGORIES = 40;

    public static final Color CHART_OVERLAY_COLOR = new Color(0, 82, 155);
    public static final int CHART_OVERLAY_FONT_SIZE = 14;

    public static final int Y_AXIS_WIDTH = 60;
    public static final int Y_AXIS_MIN_STEP = 30;
    public static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 10);
    @Deprecated
    public static final int LABEL_INDENT = 5;
    public static final Color GRID_LINES_COLOR = Color.LIGHT_GRAY;
    public static final Color SELECTION_COLOR = new Color(192,192,192,128);

    public static final Color COLOR_BULL = new Color(0, 128, 0);
    public static final Color COLOR_BEAR = Color.RED;
    
    /**
     * Not used. To remove?
     */
    @Deprecated
    public static final double CANDLE_MIN_WIDTH = 5d;
    
    /**
     * Not used. To remove?
     */
    @Deprecated
    public static final double CANDLE_WIDTH_RATIO = 0.8;

    @Deprecated
    public static final int INDICATOR_LINE_WIDTH = 2;
    @Deprecated
    public static final int CANDLE_LINE_WIDTH = 2;

    @Deprecated
    public static final Color TRADE_BUY_COLOR = new Color(186, 243, 0);
    @Deprecated
    public static final Color TRADE_SELL_COLOR = Color.PINK;
    @Deprecated
    public static final Color TRADE_LINE_COLOR = Color.BLACK;

    public static final Color BAR_COLOR = Color.gray;
    public static final Color TOP_BAR_COLOR = new Color(0, 128, 0);
    public static final Color BOTTOM_BAR_COLOR = Color.RED;
    public static int BID_ASK_VOLUME_CHARTS_HEIGHT = 250;

    public static final int TOOLTIP_MARGIN = 10;

    public static final Color CURRENT_VALUE_TEXT_COLOR = Color.BLUE;
    public static final Color CURRENT_VALUE_BG_COLOR = new Color(192,192,192,255);
    public static final Color CURRENT_VALUE_LINE_COLOR = SELECTION_COLOR;
    public static final int CURRENT_VALUE_LINE_WIDTH = 2;
}
