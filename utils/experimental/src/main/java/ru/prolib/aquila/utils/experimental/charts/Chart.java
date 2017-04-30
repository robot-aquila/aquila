package ru.prolib.aquila.utils.experimental.charts;

import javafx.beans.NamedArg;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.utils.experimental.charts.formatters.DefaultTimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.formatters.TimeAxisSettings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by TiM on 22.12.2016.
 */
public class Chart extends ScatterChart {

    private Series highSeries = new Series();
    private Series lowSeries = new Series();
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private List<LocalDateTime> xValues = Collections.synchronizedList(new ArrayList<>());
    private TimeAxisSettings timeAxisSettings = new DefaultTimeAxisSettings();
    private boolean xAxisVisible = true;

    private Group gInfo, gPrice;
    private Label lblInfo, lblPrice;
    private Group plotArea;
    private Line verticalLine, horizontalLine;
    private double lastMouseX, lastMouseY;
    private final Cursor cursor;

    private final List<Pair<Bounds, String>> textByObjectBounds;

    public Chart(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        super(xAxis, yAxis);
        setAnimated(false);
        getStylesheets().add(Chart.class.getResource("/charts.css").toExternalForm());
        cursor = Cursor.cursor(Chart.class.getResource("/transparent_cursor.png").toExternalForm());
        textByObjectBounds = new ArrayList<>();

        setLegendVisible(false);
        getData().add(highSeries);
        getData().add(lowSeries);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        yAxis.setForceZeroInRange(false);
        xAxis.setForceZeroInRange(false);
        xAxis.setTickUnit(1);
        xAxis.setMinorTickVisible(false);
        xAxis.setMinorTickCount(1);
        xAxis.setTickLabelRotation(-90);
        xAxis.setAutoRanging(false);

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int i = object.intValue();
                if(i>=0 && i<xValues.size()){
                    return getTimeLabelText(xValues.get(i));
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        setOnMouseMoved(e->{
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            mouseMoved();
        });

        lblPrice = new Label("");
        lblPrice.getStyleClass().add("price");
        gPrice = new Group(lblPrice);
        getChartChildren().add(gPrice);
        lblPrice.toFront();
    }

    public void setAxisValues(List<LocalDateTime> xValues, double minY, double maxY){
        yAxis.setPrefWidth(50);
        this.xValues.clear();
        this.xValues.addAll(xValues);
        xAxis.setLowerBound(-1);
        xAxis.setUpperBound(xValues.size());
        highSeries.getData().clear();
        lowSeries.getData().clear();
        highSeries.getData().add(new Data<>(0, maxY));
        lowSeries.getData().add(new Data<>(0, minY));
        List<Number> list = new ArrayList<>();
        for(int i=0; i<xValues.size(); i++){
            list.add(i);
        }
        xAxis.invalidateRange(list);
        xAxis.setTickLabelsVisible(xAxisVisible);
        this.layout();
    }

    public boolean isTimeDisplayed(LocalDateTime time){
        return xValues.contains(time);
    }

    public double getY(double chartY) {
        return yAxis.getDisplayPosition(chartY);
    }

    public double getX(LocalDateTime time) {
        int idx = xValues.indexOf(time);
        if(idx<0){
            throw new IllegalArgumentException("Can not find time: "+time.toString());
        }
        return xAxis.getDisplayPosition(idx);
    }

    public double getDistance(double distance){
        return Math.abs(getY(distance) - getY(0));
    }

    public void clearPlotChildren(){
        getPlotChildren().clear();
    }

    public void updatePlotChildren(List<Node> nodes){
        for(int i=getPlotChildren().size()-1; i>=0; i--){
            Node node = (Node) getPlotChildren().get(i);
            if(node.getId() == null || node.getId().equals("")){
                getPlotChildren().remove(node);
            }
        }

        getPlotChildren().addAll(nodes);

        for(int i=getPlotChildren().size()-1; i>=0; i--){
            Node node = (Node) getPlotChildren().get(i);
            LocalDateTime time = getTime(node);
            if(time != null && !isTimeDisplayed(time)){
                getPlotChildren().remove(node);
            }
        }
        lblInfo = new Label("");
        lblInfo.getStyleClass().add("info");
        gInfo = new Group(lblInfo);
        getPlotChildren().add(gInfo);
        lblInfo.toFront();

        verticalLine = new Line(0,0,0,100);
        verticalLine.getStyleClass().add("cursor-line");
        horizontalLine = new Line(0,0,100,0);
        horizontalLine.getStyleClass().add("cursor-line");
        getPlotChildren().addAll(verticalLine, horizontalLine);

        updateStyles();
        this.layout();
        plotArea = getPlotArea();
        mouseMoved();
    }

    @Override
    protected void layoutPlotChildren() {
// do nothing
    }

    private void updateStyles() {
        if(xAxisVisible){
            xAxis.getChildrenUnmodifiable().stream().filter(n->!n.isVisible()).forEach((n)->n.setVisible(true));
            int[] idx = { 0 };
            xAxis.getChildrenUnmodifiable().filtered(n-> n instanceof Text).forEach(n ->{
                int i = xAxis.getTickMarks().get(idx[0]++).getValue().intValue();
                if(i>=0 && i < xValues.size()){
                    if(timeAxisSettings.isMinorLabel(xValues.get(i))){
                        n.getStyleClass().add(timeAxisSettings.getMinorLabelStyleClass());
                    } else {
                        n.getStyleClass().add(timeAxisSettings.getLabelStyleClass());
                    }
                }
            });
        }
    }

    private String getTimeLabelText(LocalDateTime time) {
        if(time==null){
            return "";
        }
        return timeAxisSettings.formatDateTime(time);
    }

    public void setTimeAxisSettings(TimeAxisSettings timeAxisSettings) {
        this.timeAxisSettings = timeAxisSettings;
    }

    public List<LocalDateTime> getXValues() {
        return xValues;
    }

    public Node getNodeById(String id){
        for(Object obj: getPlotChildren()) {
            if (obj instanceof Node) {
                Node node = (Node) obj;
                if (id.equals(node.getId())) {
                    return node;
                }
            }
        }
        return null;
    }

    public void setXAxisVisible(boolean xAxisVisible) {
        this.xAxisVisible = xAxisVisible;
    }

    public void clearObjectBounds(){
        textByObjectBounds.clear();
    }

    public void addObjectBounds(Bounds bounds, String text){
        textByObjectBounds.add(new ImmutablePair<>(bounds, text));
    }

    private LocalDateTime getTime(Node node){
        String timeStr = node.getId();
        if(timeStr!=null){
            int idx = timeStr.indexOf("@");
            if(idx >= 0){
                timeStr = timeStr.substring(idx+1);
            }
            try {
                return LocalDateTime.parse(timeStr);
            } catch (Exception e){

            }
        }
        return null;
    }

    private Group getChartArea() {
        for (Node node : getChartChildren()) {
            if (node instanceof Group) {
                return (Group) node;
            }
        }
        return null;
    }

    private Group getPlotArea(){
        Node node = getChartArea();
        if(node !=null ){
            List<Node> nodes = ((Group) node).getChildrenUnmodifiable();
            for(Node n2: nodes){
                if(n2 instanceof Group && n2.getStyleClass().contains("plot-content")){
                    return (Group) n2;
                }
            }
        }
        return null;
    }

    private void mouseMoved(){
        Point2D point = plotArea.sceneToLocal(localToScene(lastMouseX, lastMouseY));
        double maxX = getXAxis().getWidth();
        double maxY = getYAxis().getHeight();
        if(point.getX()<0 || point.getY()<0 || point.getX()>maxX || point.getY()>maxY){
            getScene().setCursor(Cursor.DEFAULT);
            gInfo.setVisible(false);
            horizontalLine.setVisible(false);
            verticalLine.setVisible(false);
        } else {
//            getScene().setCursor(Cursor.CROSSHAIR);
            getScene().setCursor(cursor);
//            gInfo.setVisible(true);
            horizontalLine.setVisible(true);
            verticalLine.setVisible(true);
            String tooltipText = getTooltipText(point);
            if(tooltipText==null || tooltipText.equals("")){
                gInfo.setVisible(false);
            } else {
                lblInfo.setText(getTooltipText(point));
                double infoWidth = gInfo.getBoundsInLocal().getWidth();
                double infoHeight = gInfo.getBoundsInLocal().getHeight();
                if(point.getX()+ infoWidth > maxX){
                    gInfo.setTranslateX(point.getX()-5);
                    gInfo.setLayoutX(-infoWidth);
                } else {
                    gInfo.setTranslateX(point.getX()+5);
                    gInfo.setLayoutX(0);
                }
                if(point.getY()+ infoHeight > maxY){
                    gInfo.setTranslateY(point.getY()-5);
                    gInfo.setLayoutY(-infoHeight);
                } else {
                    gInfo.setTranslateY(point.getY()+5);
                    gInfo.setLayoutY(0);
                }
                gInfo.setVisible(true);
            }
        }
        verticalLine.setStartX(point.getX());
        verticalLine.setStartY(0);
        verticalLine.setEndX(point.getX());
        verticalLine.setEndY(maxY);
        horizontalLine.setStartX(0);
        horizontalLine.setStartY(point.getY());
        horizontalLine.setEndX(maxX);
        horizontalLine.setEndY(point.getY());
        if(point.getY() >= 0 && point.getY() <= maxY){
            lblPrice.setText(String.format("%.2f", yAxis.getValueForDisplay(point.getY()).doubleValue()));
            gPrice.setVisible(true);
            gPrice.setLayoutX(-gPrice.getBoundsInLocal().getWidth());
            gPrice.setTranslateY(point.getY());
            gPrice.setTranslateX(getYAxis().getBoundsInLocal().getMaxX());
        } else {
            gPrice.setVisible(false);
        }
    }

    private String getTooltipText(Point2D point) {
        return textByObjectBounds.stream().filter(e -> e.getLeft().contains(point))
                .map(e -> e.getValue())
                .sorted()
                .collect(Collectors.joining("\n-------------------\n"));
    }
}

