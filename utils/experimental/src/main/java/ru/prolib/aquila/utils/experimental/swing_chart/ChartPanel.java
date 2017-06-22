package ru.prolib.aquila.utils.experimental.swing_chart;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.swing_chart.layers.ChartLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.OTHER_CHARTS_HEIGHT;

/**
 * Created by TiM on 18.06.2017.
 */
public class ChartPanel<TCategories> extends JPanel implements MouseWheelListener, MouseMotionListener {

    protected Map<String, Chart<TCategories>> charts = new LinkedHashMap<>();
    protected final List<TCategories> categories = new ArrayList<>();
    protected final List<TCategories> displayedCategories = new ArrayList<>();
    protected AtomicInteger currentPosition = new AtomicInteger(0);
    protected AtomicInteger numberOfPoints = new AtomicInteger(40);

    protected JPanel mainPanel;
    protected JScrollBar scrollBar;
    protected AdjustmentListener scrollBarListener;
    protected int lastX, lastY;

    public ChartPanel() {
        super();
        setLayout(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
        add(mainPanel, BorderLayout.CENTER);
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBarListener = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                setCurrentPosition(e.getValue());
            }
        };
        scrollBar.addAdjustmentListener(scrollBarListener);
        add(scrollBar, BorderLayout.SOUTH);
    }

    public Chart addChart(String id){
        return addChart(id, null);
    }

    public Chart addChart(String id, Integer height) throws IllegalArgumentException {
        if(charts.containsKey(id)){
            throw new IllegalArgumentException("Chart with id='"+id+"' already added");
        }
        Chart<TCategories> chart = new Chart<>(displayedCategories);
        if(charts.size()==0) {
            chart.setPreferredSize(new Dimension(getWidth(), getHeight()));
        } else {
            chart.setMaximumSize(new Dimension(2000, height==null?OTHER_CHARTS_HEIGHT:height));
            chart.setMinimumSize(new Dimension(100, height==null?OTHER_CHARTS_HEIGHT:height));
            chart.setPreferredSize(new Dimension(getWidth(), height==null?OTHER_CHARTS_HEIGHT:height));
        }
        charts.put(id, chart);
        mainPanel.add(chart);
        chart.addMouseWheelListener(this);
        chart.addMouseMotionListener(this);
        return chart;
    }

    public Chart<TCategories> getChart(String id){
        return charts.get(id);
    }

    public void setCurrentPosition(int position){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(position < 0){
                    currentPosition.set(0);
                } else if(categories.size()>0 && position > categories.size()-getNumberOfPoints()){
                    currentPosition.set(categories.size()-getNumberOfPoints());
                } else {
                    currentPosition.set(position);
                }
                updateCategories();
                repaintCharts();
                updateScrollbarAndSetValue();
            }
        });
    }

    public int getCurrentPosition() {
        return currentPosition.get();
    }

    public int getNumberOfPoints() {
        return numberOfPoints.get();
    }

    public void setNumberOfPoints(int numberOfPoints) {
        if(numberOfPoints<2){
            numberOfPoints = 2;
        } else if(numberOfPoints > categories.size()){
            numberOfPoints = categories.size();
        }
        this.numberOfPoints.set(numberOfPoints);
        setCurrentPosition(getCurrentPosition());
    }

    public void clearData(){
        for(Chart<TCategories> c: charts.values()){
            for(ChartLayer<TCategories, ?> l: c.getLayers()){
                l.clearData();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.isControlDown()){
            setNumberOfPoints(getNumberOfPoints()+e.getWheelRotation());
        } else {
            setCurrentPosition(getCurrentPosition()+e.getWheelRotation());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.printf("x=%d; y=%d%n", e.getX(), e.getY());
        setLastX(e.getX());
        setLastY(e.getY());
        repaintCharts();
    }

    public int getLastX() {
        return lastX;
    }

    public void setLastX(int lastX) {
        this.lastX = lastX;
        for(Chart c: charts.values()){
            c.setLastX(lastX);
        }
    }

    public int getLastY() {
        return lastY;
    }

    public void setLastY(int lastY) {
        this.lastY = lastY;
        repaintCharts();
    }

    public List<TCategories> getDisplayedCategories() {
        return displayedCategories;
    }

    public List<TCategories> getCategories() {
        return categories;
    }

    protected boolean isCategoryDisplayed(TCategories category){
        return displayedCategories.contains(category);
    }

    private void updateCategories() {
        if(!SwingUtilities.isEventDispatchThread()){
            throw new IllegalStateException("It should be called from AWT Event queue thread");
        }
        Set<TCategories> set = new TreeSet<>();

        for(Chart<TCategories> chart: charts.values()){
            for (ChartLayer<TCategories, ?> layer : chart.getLayers()) {
                Series<TCategories> c = layer.getCategories();
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
        categories.clear();
        categories.addAll(set);
        displayedCategories.clear();
        for(int i=0; i<categories.size(); i++){
            if(i >= getCurrentPosition() && i < getCurrentPosition() + getNumberOfPoints()){
                displayedCategories.add(categories.get(i));
            }
        }
    }

    private void updateScrollbar() {
        if (scrollBar != null) {
            scrollBar.setMinimum(0);
            scrollBar.setMaximum(categories.size());
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

    private void repaintCharts(){
        if(charts!=null){
            for(Chart chart: charts.values()){
                chart.repaint();
            }
        }
    }
}
