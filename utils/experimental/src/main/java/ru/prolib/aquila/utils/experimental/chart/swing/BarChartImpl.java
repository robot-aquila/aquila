package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.*;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.AbstractBarChartAxis;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.BarChartAxisH;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.BarChartAxisV;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.CategoriesLabelProvider;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.ValuesLabelProvider;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.AbstractBarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.HistogramBarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.IndicatorBarChartLayer;
import ru.prolib.aquila.utils.experimental.chart.StaticOverlay;
import ru.prolib.aquila.utils.experimental.chart.formatters.*;
import ru.prolib.aquila.utils.experimental.chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.chart.interpolator.SmoothLineRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettings;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettingsButton;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettingsPopup;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.*;

/**
 * Created by TiM on 09.09.2017.
 */
public class BarChartImpl<TCategory> implements BarChart<TCategory> {

    private final JPanel rootPanel;
    private final List<TCategory> categories;
    private final List<BarChartLayer<TCategory>> layers = new CopyOnWriteArrayList<>();
    private int firstVisibleCategoryIndex, numberOfVisibleCategories;
    private int height;
    private BarChartOrientation chartOrientation;
    private final GraphicsProvider graphicsProvider;
    private AbstractBarChartAxis topAxis, bottomAxis, leftAxis, rightAxis;
    private LabelFormatter valuesLabelFormatter = new DefaultLabelFormatter();
    private List<ChartOverlay> overlays = new Vector<>();
    private final RangeCalculator rangeCalculator;
    private AtomicInteger lastX, lastY, lastCategoryIdx;
    private Map<String, List<String>> tooltips;
    private Set<String> systemLayers = new HashSet<>();
    private ChartSettingsButton chartSettingsButton;
    private final ChartSettings<TCategory> settings;
    private final ChartSettingsPopup settingsPopup;
    private final BarChartValueAxisDriver valueAxisDriver;
    private final BarChartCategoryAxisDriver categoryAxisDriver;


    public BarChartImpl(List<TCategory> categories) {
        this.categories = categories;
        rootPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BarChartImpl.this.paintComponent(g);
            }

        };
        rangeCalculator = new RangeCalculatorImpl();
        graphicsProvider = new GraphicsProvider();
        topAxis = new BarChartAxisH<TCategory>(BarChartAxisH.POSITION_TOP, graphicsProvider);
        bottomAxis = new BarChartAxisH<TCategory>(BarChartAxisH.POSITION_BOTTOM, graphicsProvider);
        leftAxis = new BarChartAxisV(BarChartAxisV.POSITION_LEFT);
        rightAxis = new BarChartAxisV(BarChartAxisV.POSITION_RIGHT);
        settings = new ChartSettings<>(layers);
        settingsPopup = new ChartSettingsPopup(settings);
        settingsPopup.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                paint();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        chartSettingsButton = new ChartSettingsButton(getRootPanel(), settingsPopup);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public BarChartOrientation getOrientation() {
        return chartOrientation;
    }

    @Override
    public BarChart<TCategory> setHeight(int points) {
        rootPanel.setPreferredSize(new Dimension(rootPanel.getWidth(), points));
        this.height = points;
        return this;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public BarChartAxis getTopAxis() {
        return topAxis;
    }

    @Override
    public BarChartAxis getLeftAxis() {
        return leftAxis;
    }

    @Override
    public BarChartAxis getRightAxis() {
        return rightAxis;
    }

    @Override
    public BarChartAxis getBottomAxis() {
        return bottomAxis;
    }

    @Override
    public List<BarChartLayer<TCategory>> getLayers() {
        return layers;
    }

    @Override
    public BarChartLayer<TCategory> getLayer(String id) {
        List<BarChartLayer<TCategory>> list = getLayers();
        for(BarChartLayer<TCategory> l: list){
            if(l.getId().equals(id)){
                return l;
            }
        }
        return null;
    }

    @Override
    public BarChartLayer<TCategory> addLayer(BarChartLayer<TCategory> layer) {
        return addLayer(layer, false);
    }

    public BarChartLayer<TCategory> addLayer(BarChartLayer<TCategory> layer, boolean isSystem) {
        if(layer instanceof AbstractBarChartLayer){
            List<String> list = new Vector<String>();
            tooltips.put(layer.getId(), list);
            ((AbstractBarChartLayer) layer).setTooltips(list);
        }
        layers.add(layer);
        if(isSystem){
            systemLayers.add(layer.getId());
        }
        return layer;
    }

    @Override
    public BarChartLayer<TCategory> addSmoothLine(Series<CDecimal> series) {
        IndicatorBarChartLayer layer = new IndicatorBarChartLayer(series, new SmoothLineRenderer());
        addLayer(layer);
        return layer;
    }

    @Override
    public BarChartLayer<TCategory> addPolyLine(Series<CDecimal> series) {
        IndicatorBarChartLayer layer = new IndicatorBarChartLayer(series, new PolyLineRenderer());
        addLayer(layer);
        return layer;
    }

    @Override
    public BarChartLayer<TCategory> addHistogram(Series<CDecimal> series) {
        HistogramBarChartLayer layer = new HistogramBarChartLayer(series);
        addLayer(layer);
        return layer;
    }

    @Override
    public BarChart<TCategory> dropLayer(String id) {
        if(!systemLayers.contains(id)){
            BarChartLayer<TCategory> layer = getLayer(id);
            if(layer != null) {
                layers.remove(layer);
            }
        }
        return this;
    }

    @Override
    public BarChart<TCategory> setVisibleArea(int first, int number) {
        firstVisibleCategoryIndex = first;
        numberOfVisibleCategories = number;
        return this;
    }

    @Override
    public BarChart<TCategory> setValuesInterval(CDecimal minValue, CDecimal maxValue) {
        settings.setMinValue(minValue);
        settings.setMaxValue(maxValue);
        return this;
    }

    @Override
    public List<ChartOverlay> getOverlays() {
        return overlays;
    }

    @Override
    public BarChart<TCategory> addStaticOverlay(String text, int y) {
        overlays.add(new StaticOverlay(text, y));
        return this;
    }

    @Override
    public BarChart<TCategory> addOverlay(ChartOverlay overlay) {
        overlays.add(overlay);
        return this;
    }

    @Override
    public void paint() {
        rootPanel.repaint();
    }

    @Override
    public LabelFormatter getValuesLabelFormatter() {
        return valuesLabelFormatter;
    }

    @Override
    public BarChartImpl setValuesLabelFormatter(LabelFormatter valuesLabelFormatter) {
        this.valuesLabelFormatter = valuesLabelFormatter;
        leftAxis.setLabelFormatter(valuesLabelFormatter);
        rightAxis.setLabelFormatter(valuesLabelFormatter);
        return this;
    }

    public void setMouseVariables(AtomicInteger lastX, AtomicInteger lastY, AtomicInteger lastCategoryIdx) {
        this.lastX = lastX;
        this.lastY = lastY;
        this.lastCategoryIdx = lastCategoryIdx;
    }

    public void setTooltips(HashMap<String, List<String>> tooltips) {
        this.tooltips = tooltips;
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
        		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        graphicsProvider.setGraphics(g2);
        
        Rectangle rootRect = new Rectangle(new Point2D(0, getRootPanel().getHeight() - 1),
        		getRootPanel().getWidth(), getRootPanel().getHeight());
        ChartLayout layout = new ChartLayout(rootRect);
        BarChartViewport viewport = new BarChartViewportImpl();
        viewport.setVisibleCategories(firstVisibleCategoryIndex, numberOfVisibleCategories);
        viewport.setVisibleValueRange(getValueRange());
        // TODO: set preferred value range
        valueAxisDriver.updateViewport(viewport);
        categoryAxisDriver.updateViewport(viewport);
        
        layout.setTopAxis(topAxis.getPaintArea(viewport, layout));
        layout.setBottomAxis(bottomAxis.getPaintArea(viewport, layout));
        layout.setLeftAxis(leftAxis.getPaintArea(viewport, layout));
        layout.setRightAxis(rightAxis.getPaintArea(viewport, layout));
        layout.autoPlotArea();
        valueAxisDriver.updateLayout(layout);
        categoryAxisDriver.updateLayout(layout);

        RangeInfo ri = rangeCalculator.autoRange(valuesRange.getMinimum(),
        		valuesRange.getMaximum(),
        		drawArea.getHeight(),
        		Y_AXIS_MIN_STEP,
        		valuesLabelFormatter.getPrecision());
        BarChartVisualizationContextImpl vc = new BarChartVisualizationContextImpl(firstVisibleCategoryIndex,
        		numberOfVisibleCategories,
        		g2,
        		drawArea,
        		ri,
        		valuesLabelFormatter);

        CategoriesLabelProvider<TCategory> clp = new CategoriesLabelProvider<>(categories, vc);
        ValuesLabelProvider vlp = new ValuesLabelProvider(vc);
        if ( layout.getTopAxis() != null ) {
        	topAxis.paint(vc, clp);
        }
        if ( layout.getBottomAxis() != null ) {
        	bottomAxis.paint(vc, clp);
        }
        if ( layout.getLeftAxis() != null ) {
        	leftAxis.paint(vc, vlp);
        }
        if ( layout.getRightAxis() != null ) {
        	rightAxis.paint(vc, vlp);
        }

        drawGridLines(vc);
        g2.clip(new Rectangle2D.Double(g2.getClipBounds().getMinX(), vc.getPlotBounds().getUpperLeftY(), g2.getClipBounds().getWidth(), vc.getPlotBounds().getHeight()));
        for(BarChartLayer<TCategory> layer: layers){
            layer.paint(vc);
        }
        drawOverlays(vc);

        lastCategoryIdx.set(vc.toCategoryIdx(lastX.get(), lastY.get()));

        g2.setClip(0, 0, getRootPanel().getWidth(), getRootPanel().getHeight());
        chartSettingsButton.paint(g2, getRootPanel().getWidth());
    }

    private Range<CDecimal> getValueRange() {
    	// TODO: replace to Range
        //CDecimal minValueLimit = settings.getMinValue();
        //CDecimal maxValueLimit = settings.getMaxValue();
        //if( minValueLimit != null && maxValueLimit != null){
        //    return new Range<>(minValueLimit, maxValueLimit);
        //}
        Range <CDecimal> x, range = null;
        for (BarChartLayer<TCategory> layer : layers) {
            x = layer.getValueRange(firstVisibleCategoryIndex, numberOfVisibleCategories);
            if ( range == null ) {
            	range = x;
            } else {
            	range = range.extend(x);
            }
        }
        if ( range != null ) {
        	return range;
        }
        return new Range<>(CDecimalBD.ZERO, CDecimalBD.of(1000000L));
    }

    private void drawGridLines(BarChartVisualizationContextImpl vc){
    	// TODO: fixme
    	/*
    	Graphics2D g = (Graphics2D) vc.getGraphics().create();
        try {
            g.setColor(GRID_LINES_COLOR);
            Rectangle2D plotBounds = new Rectangle2D.Double(vc.getPlotBounds().getX(), vc.getPlotBounds().getY(), vc.getPlotBounds().getWidth(), vc.getPlotBounds().getHeight());
            g.draw(plotBounds);
            for(int i=0; i<numberOfVisibleCategories; i++){
                int x = vc.toCanvasX(i);
                g.draw(new Line2D.Double(x, plotBounds.getMinY(), x, plotBounds.getMaxY()));
            }
            RangeInfo ri = vc.getRangeInfo();
            for(double yVal=ri.getFirstValue(); yVal<=ri.getLastValue()+(1e-6); yVal+=ri.getStepValue()){
                int y = vc.toCanvasY(yVal);
                g.draw(new Line2D.Double(plotBounds.getMinX(), y, plotBounds.getMaxX(), y));
            }
        } finally {
            g.dispose();
        }
        */
    }

    private void drawOverlays(BarChartVisualizationContextImpl vc){
        Graphics2D g2 = (Graphics2D) vc.getGraphics().create();
        g2.setColor(CHART_OVERLAY_COLOR);
        g2.setFont(new Font("default", Font.BOLD, CHART_OVERLAY_FONT_SIZE));
        int height = Math.round(g2.getFontMetrics().getHeight());
        for(ChartOverlay o: overlays){
            int x = vc.getPlotBounds().getUpperLeftX() + LABEL_INDENT;
            int y;
            if(o.getY()>=0){
                y = vc.getPlotBounds().getUpperLeftY() + LABEL_INDENT + height;
            } else {
                y = vc.getPlotBounds().getUpperLeftY()+vc.getPlotBounds().getHeight() - LABEL_INDENT;
            }
            g2.drawString(o.getText(), x, y + o.getY());
        }
    }

}
