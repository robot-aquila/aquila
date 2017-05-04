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
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.prolib.aquila.utils.experimental.charts.formatters.CategoriesLabelFormatter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by TiM on 22.12.2016.
 */
public class Chart<T> extends ScatterChart {

    private Series highSeries = new Series();
    private Series lowSeries = new Series();
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private List<T> categories = Collections.synchronizedList(new ArrayList<>());
    private CategoriesLabelFormatter<T> categoriesLabelFormatter;
    private boolean categoriesAxisVisible = true;

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
                if(i>=0 && i< categories.size()){
                    return getCategoriesLabelText(categories.get(i));
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });
        setOnMouseMoved(e->{
            synchronized (this){
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                mouseMoved();
            }
        });

        lblPrice = new Label("");
        lblPrice.getStyleClass().add("price");
        gPrice = new Group(lblPrice);
        getChartChildren().add(gPrice);
        lblPrice.toFront();
    }

    public void setAxisValues(List<T> categories, double minValue, double maxValue){
        yAxis.setPrefWidth(50);
        this.categories.clear();
        this.categories.addAll(categories);
        xAxis.setLowerBound(-1);
        xAxis.setUpperBound(categories.size());
        highSeries.getData().clear();
        lowSeries.getData().clear();
        highSeries.getData().add(new Data<>(0, maxValue));
        lowSeries.getData().add(new Data<>(0, minValue));
        List<Number> list = new ArrayList<>();
        for(int i=0; i<categories.size(); i++){
            list.add(i);
        }
        xAxis.invalidateRange(list);
        xAxis.setTickLabelsVisible(categoriesAxisVisible);
        this.layout();
    }

    public boolean isCategoryDisplayed(T x){
        return categories.contains(x);
    }

    public double getCoordByVal(double chartValue) {
        return yAxis.getDisplayPosition(chartValue);
    }

    public double getCoordByCategory(T category) {
        int idx = categories.indexOf(category);
        if(idx<0){
            throw new IllegalArgumentException("Can not find category: "+category.toString());
        }
        return xAxis.getDisplayPosition(idx);
    }

    public double getDistance(double distance){
        return Math.abs(getCoordByVal(distance) - getCoordByVal(0));
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
            T x = getCategory(node);
            if(x != null && !isCategoryDisplayed(x)){
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
        if(categoriesAxisVisible){
            xAxis.getChildrenUnmodifiable().stream().filter(n->!n.isVisible()).forEach((n)->n.setVisible(true));
            int[] idx = { 0 };
            xAxis.getChildrenUnmodifiable().filtered(n-> n instanceof Text).forEach(n ->{
                int i = xAxis.getTickMarks().get(idx[0]++).getValue().intValue();
                if(i>=0 && i < categories.size()){
                    if(categoriesLabelFormatter.isMinorLabel(categories.get(i))){
                        n.getStyleClass().add(categoriesLabelFormatter.getMinorLabelStyleClass());
                    } else {
                        n.getStyleClass().add(categoriesLabelFormatter.getLabelStyleClass());
                    }
                }
            });
        }
    }

    private String getCategoriesLabelText(T x) {
        if(x==null){
            return "";
        }
        if(categoriesLabelFormatter ==null){
            return x.toString();
        }
        return categoriesLabelFormatter.format(x);
    }

    public void setCategoriesLabelFormatter(CategoriesLabelFormatter<T> categoriesLabelFormatter) {
        this.categoriesLabelFormatter = categoriesLabelFormatter;
    }

    public List<T> getCategories() {
        return categories;
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

    public void setCategoriesAxisVisible(boolean visible) {
        this.categoriesAxisVisible = visible;
    }

    public void clearObjectBounds(){
        textByObjectBounds.clear();
    }

    public void addObjectBounds(Bounds bounds, String text){
        textByObjectBounds.add(new ImmutablePair<>(bounds, text));
    }

    private T getCategory(Node node){
        String str = node.getId();
        if(str!=null){
            int idx = str.indexOf("@");
            if(idx >= 0){
                str = str.substring(idx+1);
            }
            try {
                return categoriesLabelFormatter.parse(str);
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
//        System.out.println(lastMouseY);
        double maxX = getXAxis().getWidth();
        double maxY = getYAxis().getHeight();
        if(point.getX()<0 || point.getY()<0 || point.getX()>maxX || point.getY()>maxY){
            getScene().setCursor(Cursor.DEFAULT);
            gInfo.setVisible(false);
            horizontalLine.setVisible(false);
            verticalLine.setVisible(false);
        } else {
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

