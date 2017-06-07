package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.charts.formatters.CategoriesLabelFormatter;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorChartLayer;
import ru.prolib.aquila.utils.experimental.charts.layers.ChartLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by TiM on 22.12.2016.
 */
public class ChartPanel<T> extends JPanel implements EventHandler<Event> {

    public static final String CURRENT_POSITION_CHANGE = "CURRENT_POSITION_CHANGE";
    public static final String NUMBER_OF_POINTS_CHANGE = "NUMBER_OF_POINTS_CHANGE";
    private ActionListener actionListener;

    private final JFXPanel rootFxPanel;
    private final VBox mainPanel;
    private final HashMap<String, Chart<T>> charts = new LinkedHashMap<>();
    private final Map<String, List<ChartLayer>> chartLayers = new ConcurrentHashMap<>();
    private final Vector<T> categories = new Vector<>();
    private CategoriesLabelFormatter<T> categoriesLabelFormatter;
    private int currentPosition = 0;
    private int numberOfPoints = 120;
    private JScrollBar scrollBar;
    private AdjustmentListener scrollBarListener;
    private JTextArea infoText;

    private TooltipForm tooltipForm;
    private Rectangle screen;

    private final JPanel rootPanel;

    private boolean fullRedraw = true;

    private T selection;
    private Chart<T> lastActiveChart;
    private double lastMouseX, lastMouseY;

    public ChartPanel() {
        super();
        screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setLayout(new BorderLayout());
        rootPanel = new JPanel(new BorderLayout());
        rootFxPanel = new JFXPanel();
        mainPanel = new VBox();
        rootPanel.add(rootFxPanel, BorderLayout.CENTER);
        add(rootPanel, BorderLayout.CENTER);

        rootFxPanel.setScene(new Scene(mainPanel));

        rootFxPanel.getScene().widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> refresh());
        rootFxPanel.getScene().heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> refresh());

        tooltipForm = new TooltipForm();
//        tooltipForm.setPreferredSize(new Dimension(200, 200));
    }

    public void addChart(String id) {
        Chart<T> chart = new Chart<T>(new NumberAxis(), new NumberAxis());
        chart.setId(id);

//        chart.setOnMouseClicked(this::handle);
        chart.setOnScroll(this::handle);
        chart.setOnMouseMoved(this::handle);
        chart.setOnMouseExited(this::handle);
        chart.setCategoriesLabelFormatter(categoriesLabelFormatter);
        charts.put(id, chart);
        chartLayers.put(id, new Vector<>());

        Platform.runLater(() -> {
            mainPanel.getChildren().add(chart);
            if (charts.size() == 1) {
                VBox.setVgrow(chart, Priority.ALWAYS);
            }
            int i=0;
            for(Chart c: charts.values()){
                if(i<charts.size()-1){
                    c.setCategoriesAxisVisible(false);
                }
                i++;
            }
        });
    }

    @Override
    public void handle(Event event) {
        if(event instanceof ScrollEvent){
            ScrollEvent e = (ScrollEvent) event;
            if (e.isControlDown()) {
                setNumberOfPoints(getNumberOfPoints() - (int) Math.signum(e.getDeltaY()));
            } else {
                setCurrentPosition(getCurrentPosition() - (int) Math.signum(e.getDeltaY()));
            }
        } else if(event instanceof MouseEvent) {
            MouseEvent e = (MouseEvent) event;
            if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED)){
                for (Chart<T> c : charts.values()) {
                    selection = c.setSelection(e.getSceneX());
                }
            } else if(event.getEventType().equals(MouseEvent.MOUSE_MOVED)){
                lastActiveChart = (Chart)event.getSource();
                lastMouseX = e.getSceneX();
                lastMouseY = e.getSceneY();
                mouseMoved();
            } else if(event.getEventType().equals(MouseEvent.MOUSE_EXITED)){
                lastActiveChart = null;
                mouseMoved();
            }
        }
    }


    public List<T> getCategories() {
        return categories;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public synchronized void setCurrentPosition(int currentPosition) {
        Platform.runLater(() -> {
            updateCategories();
            if (categories.size() == 0) {
                return;
            }
            if (currentPosition < 0) {
                setCurrentPosition(0);
            } else if (currentPosition > 0 && currentPosition > categories.size() - numberOfPoints) {
                setCurrentPosition(categories.size() - numberOfPoints);
            } else {
                this.currentPosition = currentPosition;
                refresh();
                updateScrollbarAndSetValue();
                if (actionListener != null) {
                    actionListener.actionPerformed(new ActionEvent(this, 1, CURRENT_POSITION_CHANGE));
                }
            }
        });
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        if (numberOfPoints < 2) {
            numberOfPoints = 2;
        }
        if (numberOfPoints > categories.size()) {
            numberOfPoints = categories.size();
        }
        this.numberOfPoints = numberOfPoints;
        updateScrollbar();
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, 1, NUMBER_OF_POINTS_CHANGE));
        }
        setCurrentPosition(currentPosition);
    }

    public void clearData(){
        for (List<ChartLayer> layers : chartLayers.values()) {
            for (ChartLayer layer : layers) {
                layer.clearData();
            }
        }
    }

    public void removeIndicators(String chartId){
        for(int i=getChartLayers(chartId).size()-1; i>=0; i--){
            ChartLayer chartLayer = getChartLayers(chartId).get(i);
            if(chartLayer instanceof IndicatorChartLayer){
                getChartLayers(chartId).remove(i);
            }
        }
        setFullRedraw(true);
    }

    AtomicBoolean started = new AtomicBoolean(false);
    Timer timerUpdate = new Timer();

    public void refresh() {
//        System.out.println("REFRESH NEEDED");
        if (!started.get()) {
            started.set(true);
            TimerTask timerUpdateTask = new TimerTask() {
                @Override
                public void run() {
//                    System.out.println("REFRESH PROCESSING");
                    started.set(false);
                    _refresh();
                }
            };
            timerUpdate.schedule(timerUpdateTask, 100);
        }
    }

    private synchronized void _refresh() {
        Platform.runLater(() -> {
            setAxisValues();
            for (String id : charts.keySet()) {
                Chart chart = getChart(id);
                if (fullRedraw) {
                    chart.clearPlotChildren();
                }
                chart.updatePlotChildren(paintChartObjects(id));
            }
            fullRedraw = false;
            mouseMoved();
        });
    }

    public void addChartLayer(String chartId, ChartLayer object) {
        object.setChart(getChart(chartId));
        getChartLayers(chartId).add(object);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setCategoriesLabelFormatter(CategoriesLabelFormatter formatter) {
        this.categoriesLabelFormatter = formatter;
        for (Chart chart : charts.values()) {
            chart.setCategoriesLabelFormatter(formatter);
        }
    }

    public boolean isCategoryDisplayed(T category) {
        for (Chart chart : charts.values()) {
            if (chart.isCategoryDisplayed(category)) {
                return true;
            }
        }
        return false;
    }

    private synchronized void updateCategories() {
        Set<T> set = new TreeSet<>();

        categories.clear();
        for (List<ChartLayer> objects_ : chartLayers.values()) {
            List<ChartLayer> objects = objects_.stream().collect(Collectors.toList());

            for (ChartLayer obj : objects) {
                Series<T> c = obj.getCategories();
                if(c!=null){
                    for (int i = 0; i < c.getLength(); i++) {
                        try {
                            set.add(c.get(i));
                        } catch (ValueException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        categories.addAll(set);
    }

    private void setAxisValues() {
        List<T> categoriesToDisplay = new ArrayList<>();
        int cnt = Math.min(currentPosition + numberOfPoints, categories.size());
        for (int i = currentPosition; i < cnt; i++) {
            categoriesToDisplay.add(categories.get(i));
        }

        for (String id : charts.keySet()) {
            Chart chart = getChart(id);
            double maxY = 0;
            double minY = 1e6;
            for (ChartLayer obj : getChartLayers(id)) {
                Pair<Double, Double> interval = obj.getValuesInterval(categoriesToDisplay);
                if (interval != null) {
                    if (interval.getRight() != null && interval.getRight() > maxY) {
                        maxY = interval.getRight();
                    }
                    if (interval.getLeft() != null && interval.getLeft() < minY) {
                        minY = interval.getLeft();
                    }
                }
            }
            chart.setAxisValues(categoriesToDisplay, minY, maxY);
        }
    }

    private List<Node> paintChartObjects(String id) {
        List<Node> result = new ArrayList<>();
        for (ChartLayer obj : getChartLayers(id)) {
            result.addAll(obj.paint());
        }
        return result;
    }

    public Chart getChart(String chartId) {
        Chart chart = charts.get(chartId);
        if (chart == null) {
            throw new IllegalArgumentException("Unknown chart with id = " + chartId);
        }
        return chart;
    }

    public List<ChartLayer> getChartLayers(String chartId) {
        List<ChartLayer> objects = chartLayers.get(chartId);
        if (objects == null) {
            throw new IllegalArgumentException("Unknown chart with id = " + chartId);
        }
        return objects;
    }

    public void setFullRedraw(boolean fullRedraw) {
        this.fullRedraw = fullRedraw;
    }

    public void setScrollbar(JScrollBar scrollbar) {
        if (this.scrollBar == null) {
            this.scrollBar = scrollbar;
            scrollBarListener = new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    setCurrentPosition(e.getValue());
                }
            };
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {
                        rootPanel.add(scrollbar, BorderLayout.SOUTH);
                    } else {
                        rootPanel.add(scrollbar, BorderLayout.EAST);
                    }
                    updateScrollbar();
                    scrollbar.addAdjustmentListener(scrollBarListener);
                }
            });
        } else {
            throw new IllegalStateException("Scrollbar already attached");
        }
    }

    private void updateScrollbar() {
        if (scrollBar != null) {
            scrollBar.setMinimum(0);
            scrollBar.setMaximum(getCategories().size());
            scrollBar.setVisibleAmount(getNumberOfPoints());
        }
    }

    private void updateScrollbarAndSetValue() {
        if (scrollBar != null) {
            scrollBar.removeAdjustmentListener(scrollBarListener);
            updateScrollbar();
            scrollBar.setValue(getCurrentPosition());
            scrollBar.addAdjustmentListener(scrollBarListener);
        }
    }

    private void setCursorRectPosition(T category){
        for(Chart<T> c: charts.values()){
            c.setCursorRectPosition(category);
        }
    }

    private void mouseMoved(){
        for(Chart c: charts.values()){
            c.mouseMoved(lastActiveChart, lastMouseX, lastMouseY);
        }
        if(lastActiveChart==null) {
            tooltipForm.setVisible(false);
        } else {
            T category = lastActiveChart.getCategoryBySceneX(lastMouseX);
            if(category==null){
                tooltipForm.setVisible(false);
            } else {
                Point2D point = lastActiveChart.localToScreen(lastActiveChart.sceneToLocal(lastMouseX, lastMouseY));
//                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = (int)point.getX();
                int y = (int)point.getY();
                if(x + tooltipForm.getWidth() <= screen.getMaxX()){
                    x = x+10;
                } else {
                    x = x - tooltipForm.getWidth()-10;
                }
                if(y + tooltipForm.getHeight() <= screen.getMaxY()){
                    y = y+10;
                } else {
                    y = y - tooltipForm.getHeight()-10;
                }
                tooltipForm.setLocation(x, y);
                StringBuilder sb = new StringBuilder();
                for(List<ChartLayer> layers: chartLayers.values()){
                    for(ChartLayer layer: layers){
                        String txt = layer.getTooltip(category);
                        if(txt!=null){
                            if(sb.length()!=0){
                                sb.append("\n----------\n");
                            }
                            sb.append(txt);
                        }
                    }
                }
                String txt = sb.toString();
                txt = txt.replace("\n----------\n", "<hr>").replace("\n", "<br>");
                tooltipForm.setText("<html>"+txt+"</html>");
//                tooltipForm.setText(sb.toString());
                tooltipForm.setVisible(true);
//                System.out.println(category);
            }
        }
    }
}

