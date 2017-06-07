package ru.prolib.aquila.utils.experimental.charts;

import javafx.beans.NamedArg;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import ru.prolib.aquila.utils.experimental.charts.formatters.CategoriesLabelFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TiM on 22.12.2016.
 */
public class Chart<T> extends ScatterChart {

    private static final int Y_AXIS_PREF_WIDTH = 50;

    private Series highSeries = new Series();
    private Series lowSeries = new Series();
    private final NumberAxis xAxis;
    private final NumberAxis yAxis;
    private List<T> categories = Collections.synchronizedList(new ArrayList<>());
    private CategoriesLabelFormatter<T> categoriesLabelFormatter;
    private boolean categoriesAxisVisible = true;

    private Group gPrice;
    private Label lblPrice;
    private Group plotArea;
    private Line horizontalLine;
    private Rectangle cursorRect, selectionRect;
    private T selection;

    public Chart(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        super(xAxis, yAxis);
        setAnimated(false);
        getStylesheets().add(Chart.class.getResource("/charts.css").toExternalForm());
//        cursor = Cursor.cursor(Chart.class.getResource("/transparent_cursor.png").toExternalForm());
//        setCursor(Cursor.HAND);

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

        lblPrice = new Label("");
        lblPrice.getStyleClass().add("price");
        gPrice = new Group(lblPrice);
        getChartChildren().add(gPrice);
        lblPrice.toFront();
    }

    public void setAxisValues(List<T> categories, double minValue, double maxValue){
        yAxis.setPrefWidth(Y_AXIS_PREF_WIDTH);
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

    public double getCoordByCategory(T category) throws IllegalArgumentException {
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
        horizontalLine = null;
        cursorRect = null;
        selectionRect = null;
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

        if(horizontalLine==null){
            horizontalLine = new Line(0,0,100,0);
            horizontalLine.getStyleClass().add("cursor-line");
            horizontalLine.setId("HORIZONTAL_LINE");
            cursorRect = new Rectangle(0,0,0,0);
            cursorRect.getStyleClass().add("cursor-rectangle");
            cursorRect.setId("CURSOR_RECT");
            selectionRect = new Rectangle(0,0,0,0);
            selectionRect.getStyleClass().add("selection-rectangle");
            selectionRect.setId("SELECTION_RECT");
            getPlotChildren().addAll(horizontalLine, cursorRect, selectionRect);
        }

        updateStyles();
        this.layout();
        plotArea = getPlotArea();
//        System.out.println(lastMouseX + " " + this.getId());
//        javafx.event.Event.fireEvent(this, new MouseEvent(MouseEvent.MOUSE_MOVED, lastMouseX, 0, 0,0, MouseButton.NONE, 0, false, false, false, false, false, false, false, false, false, false, null ));
//        mouseMoved();
    }

    public void mouseMoved(Chart source, double sceneX, double sceneY){
        if(plotArea==null){
            return;
        }
        Point2D point = plotArea.sceneToLocal(sceneX, sceneY);
        double maxX = getXAxis().getWidth();
        double maxY = getYAxis().getHeight();
        if(this == source){
            if(point.getX()<0 || point.getY()<0 || point.getX()>maxX || point.getY()>maxY){
                horizontalLine.setVisible(false);
                gPrice.setVisible(false);
            } else {
                horizontalLine.setVisible(true);
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
        } else {
            horizontalLine.setVisible(false);
            gPrice.setVisible(false);
        }
        setCursorRectPosition(getCategoryByCoord(point.getX()));
        setSelection(selection);
    }

    public void setCursorRectPosition(T category){
        setRectPosition(category, cursorRect);
    }

    public T getCategoryBySceneX(double sceneX){
        if(plotArea==null){
            return null;
        }
        Point2D point = plotArea.sceneToLocal(sceneX, 0);
        try {
            return getCategoryByCoord(point.getX());
        } catch (IllegalArgumentException e){
            return null;
        }
    }

    public T setSelection(double sceneX){
        T category = getCategoryBySceneX(sceneX);
        setSelection(category);
        return category;
    }

    public void setSelection(T category){
        setRectPosition(category, selectionRect);
        selection = category;
    }

    private void setRectPosition(T category, Rectangle rect){
        if(rect!=null){
            if(categories.size()>0 && category!=null ){
                rect.setVisible(true);
                double x;
                try {
                    x = getCoordByCategory(category);
                } catch (IllegalArgumentException e){
                    rect.setVisible(false);
                    return;
                }
                double width = getCoordByCategory(categories.get(0));
                double maxY = getYAxis().getHeight();
                rect.setX(x - width/2);
                rect.setY(0);
                rect.setWidth(width);
                rect.setHeight(maxY);
                rect.toBack();
            } else {
                rect.setVisible(false);
            }
        }
    }

    @Override
    protected void layoutPlotChildren() {
// do nothing
    }

    private void updateStyles() {
        if(categoriesAxisVisible){
//            xAxis.getChildrenUnmodifiable().stream().filter(n->!n.isVisible()).forEach((n)->n.setVisible(true));
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

    private T getCategoryByCoord(double x){
        if(categories.size()>0){
            double width = getCoordByCategory(categories.get(0));
            for(int i=0; i<categories.size(); i++){
                double xCat = getCoordByCategory(categories.get(i));
                if(x > xCat-width/2 && x <= xCat+width/2){
                    return categories.get(i);
                }
            }
        }
        return null;
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
}

