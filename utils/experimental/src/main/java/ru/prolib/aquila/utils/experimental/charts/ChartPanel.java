package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.utils.experimental.charts.formatters.TimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.objects.ChartObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by TiM on 22.12.2016.
 */
public class ChartPanel extends JFXPanel {

    public static final String CURRENT_POSITION_CHANGE="CURRENT_POSITION_CHANGE";
    public static final String NUMBER_OF_POINTS_CHANGE="NUMBER_OF_POINTS_CHANGE";
    private ActionListener actionListener;

//    private final Chart chart;
    private final VBox mainPanel;
    private final HashMap<String, Chart> charts = new LinkedHashMap<>();
    private final HashMap<String, List<ChartObject>> chartObjects = new HashMap<>();
    private final List<LocalDateTime> xValues = new ArrayList<>();
    private TimeAxisSettings timeAxisSettings;
    private int currentPosition = 0;
    private int numberOfPoints = 15;

    public ChartPanel() {
        super();
        mainPanel = new VBox();

        this.setScene(new Scene(mainPanel));

        this.getScene().widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> refresh());
        this.getScene().heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> refresh());

    }

    public void addChart(String id){
        Chart chart = new Chart(new NumberAxis(), new NumberAxis());
        chart.setOnMouseClicked(event->{

        });
        chart.setOnScroll(event -> {
            if (event.isControlDown()) {
                setNumberOfPoints(getNumberOfPoints() - (int) Math.signum(event.getDeltaY()));
            } else {
                setCurrentPosition(getCurrentPosition() - (int) Math.signum(event.getDeltaY()));
            }
        });
        chart.setTimeAxisSettings(timeAxisSettings);
        charts.put(id, chart);
        chartObjects.put(id, new ArrayList<>());

        Platform.runLater(()->{
            mainPanel.getChildren().add(chart);
            if(charts.size()==1){
                VBox.setVgrow(chart, Priority.ALWAYS);
            }
        });
    }

    public List<LocalDateTime> getXValues(){
        return xValues;
    }

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        updateXValues();
        if(xValues.size()==0){
            return;
        }
        if (currentPosition < 0) {
            setCurrentPosition(0);
        } else if (currentPosition > 0 && currentPosition > xValues.size() - numberOfPoints) {
            setCurrentPosition(xValues.size() - numberOfPoints);
        } else {
            this.currentPosition = currentPosition;
            refresh();
            if(actionListener!=null){
                actionListener.actionPerformed(new ActionEvent(this, 1, CURRENT_POSITION_CHANGE));
            }
        }
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        if (numberOfPoints < 2) {
            numberOfPoints = 2;
        }
        if (numberOfPoints > xValues.size()) {
            numberOfPoints = xValues.size();
        }
        this.numberOfPoints = numberOfPoints;
        if(actionListener!=null){
            actionListener.actionPerformed(new ActionEvent(this, 1, NUMBER_OF_POINTS_CHANGE));
        }
        setCurrentPosition(currentPosition);
    }

    boolean started = false;
    Timer timerUpdate = new Timer();

    public void refresh(){
//        System.out.println("REFRESH NEEDED");
        if(!started){
            started = true;
            TimerTask timerUpdateTask = new TimerTask() {
                @Override
                public void run() {
//                    System.out.println("REFRESH PROCESSING");
                    started = false;
                    _refresh();
                }
            };
            timerUpdate.schedule(timerUpdateTask, 100);
        }
    }

    private void _refresh(){
        Platform.runLater(()->{
            setAxisValues();
            for(String id: charts.keySet()){
                Chart chart = getChart(id);
                chart.updatePlotChildren(paintChartObjects(id));
            }
        });
    }

    public void addChartObject(String chartId, ChartObject object){
        object.setChart(getChart(chartId));
        getChartObjects(chartId).add(object);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setTimeAxisSettings(TimeAxisSettings timeAxisSettings) {
        this.timeAxisSettings = timeAxisSettings;
        for(Chart chart: charts.values()){
            chart.setTimeAxisSettings(timeAxisSettings);
        }
    }

    public boolean isTimeDisplayed(LocalDateTime time){
        for(Chart chart: charts.values()){
            if(chart.isTimeDisplayed(time)){
                return true;
            }
        }
        return false;
    }

    public boolean isTimeDisplayed(Instant time){
        return isTimeDisplayed(Utils.toLocalDateTime(time));
    }

    private void updateXValues(){
        Set<LocalDateTime> set = new TreeSet<>();

        xValues.clear();
        for(List<ChartObject> objects: chartObjects.values()){
            for(ChartObject obj: objects){
                set.addAll(obj.getXValues());
            }
        }
        xValues.addAll(set);
    }

    private void setAxisValues(){
        List<LocalDateTime> xValuesToDisplay = new ArrayList<>();
        int cnt = Math.min(currentPosition + numberOfPoints, xValues.size());
        for(int i=currentPosition; i<cnt; i++){
            xValuesToDisplay.add(xValues.get(i));
        }

        for(String id: charts.keySet()){
            Chart chart = getChart(id);
            double maxY = 0;
            double minY = 1e6;
            for(ChartObject obj: getChartObjects(id)){
                Pair<Double, Double> interval = obj.getYInterval(xValuesToDisplay);
                if(interval.getRight() > maxY){
                    maxY = interval.getRight();
                }
                if(interval.getLeft() < minY){
                    minY = interval.getLeft();
                }
            }
            chart.setAxisValues(xValuesToDisplay, minY, maxY);
        }
    }

    private List<Node> paintChartObjects(String id){
        List<Node> result = new ArrayList<>();
        for(ChartObject obj: getChartObjects(id)){
            result.addAll(obj.paint());
        }
        return result;
    }

    public Chart getChart(String chartId) {
        Chart chart = charts.get(chartId);
        if(chart==null){
            throw new IllegalArgumentException("Unknown chart with id = "+chartId);
        }
        return chart;
    }

    public List<ChartObject> getChartObjects(String chartId) {
        List<ChartObject> objects = chartObjects.get(chartId);
        if(objects==null){
            throw new IllegalArgumentException("Unknown chart with id = "+chartId);
        }
        return objects;
    }
}

