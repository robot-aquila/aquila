package ru.prolib.aquila.utils.experimental.swing_chart;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.Axis;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.CategoryAxis;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.ValueAxis;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.*;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.ChartLayer;
import ru.prolib.aquila.utils.experimental.swing_chart.settings.ChartSettingsButton;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by TiM on 10.06.2017.
 */
public class Chart<TCategories> {

    private final List<ChartLayer<TCategories, ?>> layers = new ArrayList<>();
    private final List<TCategories> categories;
    private final CategoryAxis<TCategories> topAxis, bottomAxis;
    private final ValueAxis<TCategories> leftAxis, rightAxis;
    private final RangeCalculator rangeCalculator;

    private int lastX, lastY;
    private TCategories lastCategory;

    private LabelFormatter<TCategories> categoriesLabelFormatter = new DefaultLabelFormatter<TCategories>();
    private LabelFormatter valuesLabelFormatter = new DefaultLabelFormatter();

    private Double minValueInterval;
    private Double maxValueInterval;

    private ChartSettingsButton settingsButton;
    private final JPanel rootPanel = new JPanel(){
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Chart.this.paintComponent(g);
        }
    };
    private List<Overlay> overlays = new Vector<>();

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public Chart(List<TCategories> categories) {
        super();
//        settingsButton = new ChartSettingsButton(this.getRootPanel());
        this.categories = categories;
        topAxis = new CategoryAxis<>(SwingConstants.TOP, this.categories, categoriesLabelFormatter);
        bottomAxis = new CategoryAxis<>(SwingConstants.BOTTOM, this.categories, categoriesLabelFormatter);
        leftAxis = new ValueAxis<TCategories>(SwingConstants.LEFT, valuesLabelFormatter);
        rightAxis = new ValueAxis<TCategories>(SwingConstants.RIGHT, valuesLabelFormatter);
        rangeCalculator = new RangeCalculatorImpl();
    }

    public void addLayer(ChartLayer<TCategories, ?> layer){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                layers.add(layer);
            }
        });
    }

    public void dropLayer(ChartLayer<TCategories, ?> layer){
        layers.remove(layer);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        double leftMargin = leftAxis.getSize();
        double rightMargin = rightAxis.getSize();
        double topMargin = topAxis.getSize(g2)+LABEL_INDENT;
        double bottomMargin = bottomAxis.getSize(g2)+LABEL_INDENT;

        if(settingsButton!=null){
            settingsButton.paint(g2, getRootPanel().getWidth() - rightMargin);
        }

        Rectangle2D drawArea = new Rectangle2D.Double(MARGIN+leftMargin,
                MARGIN+topMargin,
                getRootPanel().getWidth()-2*MARGIN-leftMargin-rightMargin,
                getRootPanel().getHeight()-2*MARGIN - topMargin - bottomMargin);

        Pair<Double, Double> valuesInterval = getValuesInterval();
        RangeInfo ri = rangeCalculator.autoRange(valuesInterval.getLeft(), valuesInterval.getRight(), drawArea.getHeight(), Y_AXIS_MIN_STEP, valuesLabelFormatter.getPrecision());
        CoordConverter<TCategories> coordConverter = new CoordConverterImpl<TCategories>(categories, g2, drawArea, ri);

        topAxis.paint(coordConverter);
        bottomAxis.paint(coordConverter);
        leftAxis.paint(coordConverter);
        rightAxis.paint(coordConverter);
        drawGridLines(coordConverter);
        drawSelection(coordConverter);
        g2.clip(coordConverter.getPlotBounds());
        for(ChartLayer layer: layers){
            layer.paint(coordConverter);
        }
        drawOverlays(coordConverter);
    }

    public List<ChartLayer<TCategories, ?>> getLayers() {
        return layers;
    }

    public CategoryAxis<TCategories> getTopAxis() {
        return topAxis;
    }

    public CategoryAxis<TCategories> getBottomAxis() {
        return bottomAxis;
    }

    public Axis<TCategories> getLeftAxis() {
        return leftAxis;
    }

    public Axis<TCategories> getRightAxis() {
        return rightAxis;
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
    }

    public TCategories getLastCategory() {
        return lastCategory;
    }

    public LabelFormatter<TCategories> getCategoriesLabelFormatter() {
        return categoriesLabelFormatter;
    }

    public void setCategoriesLabelFormatter(LabelFormatter<TCategories> categoriesLabelFormatter) {
        this.categoriesLabelFormatter = categoriesLabelFormatter;
        topAxis.setLabelFormatter(categoriesLabelFormatter);
        bottomAxis.setLabelFormatter(categoriesLabelFormatter);
    }

    public LabelFormatter getValuesLabelFormatter() {
        return valuesLabelFormatter;
    }

    public void setValuesLabelFormatter(LabelFormatter valuesLabelFormatter) {
        this.valuesLabelFormatter = valuesLabelFormatter;
        leftAxis.setLabelFormatter(valuesLabelFormatter);
        rightAxis.setLabelFormatter(valuesLabelFormatter);
    }

    public void setMinValueInterval(Double minValueInterval) {
        this.minValueInterval = minValueInterval;
    }

    public void setMaxValueInterval(Double maxValueInterval) {
        this.maxValueInterval = maxValueInterval;
    }

    public List<Overlay> getOverlays() {
        return overlays;
    }

    private Pair<Double, Double> getValuesInterval(){
        if(minValueInterval!=null && maxValueInterval!=null){
            return new ImmutablePair<>(minValueInterval, maxValueInterval);
        }
        double maxY = 0;
        double minY = 1e6;
        for (ChartLayer layer : layers) {
            Pair<Double, Double> interval = layer.getValuesInterval(categories);
            if (interval != null) {
                if (interval.getRight() != null && interval.getRight() > maxY) {
                    maxY = interval.getRight();
                }
                if (interval.getLeft() != null && interval.getLeft() < minY) {
                    minY = interval.getLeft();
                }
            }
        }
        return new ImmutablePair<>(minValueInterval==null?minY:minValueInterval, maxValueInterval==null?maxY:maxValueInterval);
    }

    private void drawSelection(CoordConverter<TCategories> coordConverter){
        Graphics2D g = (Graphics2D) coordConverter.getGraphics().create();
        try {
            lastCategory = coordConverter.getCategory(lastX);
            if(lastCategory!=null){
                double x = coordConverter.getX(lastCategory);
                double width = coordConverter.getStepX();
                g.setColor(SELECTION_COLOR);
                g.fill(new Rectangle2D.Double(x-width/2, coordConverter.getPlotBounds().getMinY(), width, coordConverter.getPlotBounds().getHeight()));
            }
        } finally {
            g.dispose();
        }
    }

    private void drawGridLines(CoordConverter coordConverter){
        Graphics2D g = (Graphics2D) coordConverter.getGraphics().create();
        try {
            g.setColor(GRID_LINES_COLOR);
            g.draw(coordConverter.getPlotBounds());
            for(TCategories category: categories){
                Double x = coordConverter.getX(category);
                if(x!=null){
                    g.draw(new Line2D.Double(x, coordConverter.getPlotBounds().getMinY(), x, coordConverter.getPlotBounds().getMaxY()));
                }
            }
            RangeInfo ri = coordConverter.getYRangeInfo();
            for(double yVal=ri.getFirstValue(); yVal<=ri.getLastValue()+(1e-6); yVal+=ri.getStepValue()){
                Double y = coordConverter.getY(yVal);
                if(y!=null){
                    g.draw(new Line2D.Double(coordConverter.getPlotBounds().getMinX(), y, coordConverter.getPlotBounds().getMaxX(), y));
                }
            }
        } finally {
            g.dispose();
        }
    }

    private void drawOverlays(CoordConverter converter){
        Graphics2D g2 = (Graphics2D) converter.getGraphics().create();
        g2.setColor(CHART_OVERLAY_COLOR);
        g2.setFont(new Font("default", Font.BOLD, CHART_OVERLAY_FONT_SIZE));
        int height = (int) Math.round(g2.getFontMetrics().getStringBounds("A", g2).getHeight());
        for(Overlay o: overlays){
            int x = new Double(converter.getPlotBounds().getMinX()).intValue() + LABEL_INDENT;
            int y = 0;
            if(o.getY()>=0){
                y = new Double(converter.getPlotBounds().getMinY()).intValue() + LABEL_INDENT + height;
            } else {
                y = new Double(converter.getPlotBounds().getMaxY()).intValue() - LABEL_INDENT;
            }

            g2.drawString(o.getText(), x, y + o.getY());
        }
    }
}
