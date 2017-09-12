package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.*;
import ru.prolib.aquila.utils.experimental.swing_chart.TooltipForm;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.DefaultLabelFormatter;
import ru.prolib.aquila.utils.experimental.swing_chart.axis.formatters.LabelFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.OTHER_CHARTS_HEIGHT;

/**
 * Created by TiM on 08.09.2017.
 */
public class BarChartPanelImpl<TCategory> implements BarChartPanel<TCategory>, MouseWheelListener, MouseMotionListener {
    private final ChartOrientation orientation;

    protected Map<String, BarChart<TCategory>> charts = new LinkedHashMap<>();
    protected final java.util.List<TCategory> displayedCategories = new Vector<>();
    protected AtomicInteger firstVisibleCategoryIndex = new AtomicInteger(0);
    protected AtomicInteger numberOfVisibleCategories = new AtomicInteger(40);
    protected Series<TCategory> categories;

    protected JPanel mainPanel;
    protected JScrollBar scrollBar;
    protected AdjustmentListener scrollBarListener;
    protected int lastX, lastY;
    protected TooltipForm tooltipForm;
    protected LabelFormatter categoryLabelFormatter = new DefaultLabelFormatter();
    protected LabelFormatter valueLabelFormatter = new DefaultLabelFormatter();
    private java.awt.Rectangle screen;
    private final Timer updateTooltipTextTimer;
    private final HashMap<JPanel, BarChart<TCategory>> chartByPanel = new HashMap<>();
    private final JPanel rootPanel;

    public BarChartPanelImpl(ChartOrientation orientation) {
        this.orientation = orientation;
        rootPanel = new JPanel(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
        rootPanel.add(mainPanel, BorderLayout.CENTER);
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBarListener = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                setVisibleArea(e.getValue(), numberOfVisibleCategories.get());
            }
        };
        scrollBar.addAdjustmentListener(scrollBarListener);
        rootPanel.add(scrollBar, BorderLayout.SOUTH);
        tooltipForm = new TooltipForm();
        screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        updateTooltipTextTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTooltipText_();
            }
        });
        updateTooltipTextTimer.setRepeats(false);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    @Override
    public ChartOrientation getOrientation() {
        return orientation;
    }

    @Override
    public BarChart<TCategory> addChart(String id) {
        if(charts.containsKey(id)){
            throw new IllegalArgumentException("Chart with id='"+id+"' already added");
        }
        BarChartImpl<TCategory> chart = new BarChartImpl<>(displayedCategories);
        if(charts.size()==0) {
            chart.setHeight(rootPanel.getHeight());
        } else {
            chart.setHeight(OTHER_CHARTS_HEIGHT);
        }
        mainPanel.add(chart.getRootPanel());
        charts.put(id, chart);
        chart.getRootPanel().addMouseWheelListener(this);
        chart.getRootPanel().addMouseMotionListener(this);
        chart.getRootPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                tooltipForm.setVisible(false);
            }
        });
        updateLabelsConfig();
        chartByPanel.put(chart.getRootPanel(), chart);
        getRootPanel().validate();
        getRootPanel().repaint();
        return chart;
    }

    @Override
    public BarChart<TCategory> getChart(String id) {
        return charts.get(id);
    }

    @Override
    public void setVisibleArea(int first, int number) {
        int size = categories.getLength();
        if(number<2){
            number = 2;
        } else if(number > size){
            number = size;
        }
        this.numberOfVisibleCategories.set(number);

        if(first < 0){
            firstVisibleCategoryIndex.set(0);
        } else if(size>0 && first > size-getNumberOfVisibleCategories()){
            firstVisibleCategoryIndex.set(size-getNumberOfVisibleCategories());
        } else {
            firstVisibleCategoryIndex.set(first);
        }
        for(BarChart<TCategory> c: charts.values()){
            c.setVisibleArea(firstVisibleCategoryIndex.get(), numberOfVisibleCategories.get());
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCategories();
                paint();
                updateScrollbarAndSetValue();
//                updateTooltipText();
            }
        });
    }

    @Override
    public int getFirstVisibleCategory() {
        return firstVisibleCategoryIndex.get();
    }

    @Override
    public int getNumberOfVisibleCategories() {
        return numberOfVisibleCategories.get();
    }

    @Override
    public void setCategories(Series<TCategory> categories) {
        this.categories = categories;
        for(BarChart<TCategory> chart: charts.values()){
            for(BarChartLayer<TCategory> layer: chart.getLayers()){
                layer.setCategories(this.categories);
            }
        }
    }

    @Override
    public void paint() {
        if(charts!=null){
            for(BarChart<TCategory> chart: charts.values()){
                chart.paint();
            }
        }
    }


    private void updateTooltipText_(){
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int first = getFirstVisibleCategory();
        int number = getNumberOfVisibleCategories();
        if(e.isControlDown()){
            number += e.getWheelRotation();
        } else {
            first += e.getWheelRotation();
        }
        setVisibleArea(first, number);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        setLastX(e.getX());
        setLastY(e.getY());
        paint();

//        Chart<TCategories> chart = chartByPanel.get(e.getSource());
//        if(chart!=null){
//            TCategories category = chart.getLastCategory();
//            if(category==null){
//                tooltipForm.setVisible(false);
//            } else {
//                updateTooltipText(category);
//                Point point = chart.getRootPanel().getLocationOnScreen();
//                int x = point.x + lastX;
//                int y = point.y + lastY;
//                if(x + tooltipForm.getWidth() <= screen.getMaxX()){
//                    x = x+TOOLTIP_MARGIN;
//                } else {
//                    x = x - tooltipForm.getWidth()-TOOLTIP_MARGIN;
//                }
//                if(y + tooltipForm.getHeight() <= screen.getMaxY()){
//                    y = y+TOOLTIP_MARGIN;
//                } else {
//                    y = y - tooltipForm.getHeight()-TOOLTIP_MARGIN;
//                }
//                tooltipForm.setLocation(x, y);
//                tooltipForm.setVisible(true);
//            }
//        }
    }

    protected void updateLabelsConfig(){
        int i=0;
        for(BarChart c: charts.values()){
            c.getTopAxis().setVisible(false);
            c.getBottomAxis().setVisible(false);
            if(i==0){
                c.getTopAxis().setLabelFormatter(categoryLabelFormatter);
                c.getTopAxis().setVisible(true);
                c.getBottomAxis().setVisible(false);
            }
            if(i==charts.size()-1){
                c.getBottomAxis().setLabelFormatter(categoryLabelFormatter);
                c.getBottomAxis().setVisible(true);
            }
            c.getLeftAxis().setVisible(true);
            c.getRightAxis().setVisible(true);
            if(c.getValuesLabelFormatter().getClass().equals(DefaultLabelFormatter.class)){
                c.setValuesLabelFormatter(valueLabelFormatter);
            }
            i++;
        }
    }

    protected void updateCategories() {
        if(!SwingUtilities.isEventDispatchThread()){
            throw new IllegalStateException("It should be called from AWT Event queue thread");
        }
        if (categories!=null){
            categories.lock();
            try {
                displayedCategories.clear();
                for(int i=0; i<categories.getLength(); i++){
                    if(i >= getFirstVisibleCategory() && i < getFirstVisibleCategory() + getNumberOfVisibleCategories()){
                        displayedCategories.add(categories.get(i));
                    }
                }
            } catch (ValueException e) {
                e.printStackTrace();
            } finally {
                categories.unlock();
            }
        }
    }

    protected void setLastX(int lastX) {
        this.lastX = lastX;
        for(BarChart c: charts.values()){
            c.setLastX(lastX);
        }
    }

    protected void setLastY(int lastY) {
        this.lastY = lastY;
    }

    protected void updateScrollbar(int countCategories){
        if (scrollBar != null) {
            scrollBar.setMinimum(0);
            scrollBar.setMaximum(countCategories);
            scrollBar.setVisibleAmount(getNumberOfVisibleCategories());
        }
    }

    private void updateScrollbar() {
        updateScrollbar(categories.getLength());
    }

    private void updateScrollbarAndSetValue() {
        if (scrollBar != null) {
            scrollBar.removeAdjustmentListener(scrollBarListener);
            updateScrollbar();
            scrollBar.setValue(getFirstVisibleCategory());
            scrollBar.addAdjustmentListener(scrollBarListener);
        }
    }
}
