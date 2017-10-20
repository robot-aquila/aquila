package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.utils.experimental.chart.*;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.CursorLayer;
import ru.prolib.aquila.utils.experimental.chart.TooltipForm;
import ru.prolib.aquila.utils.experimental.chart.formatters.DefaultLabelFormatter;
import ru.prolib.aquila.utils.experimental.chart.formatters.LabelFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.Autoscroll;
import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.OTHER_CHARTS_HEIGHT;
import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.TOOLTIP_MARGIN;

/**
 * Created by TiM on 08.09.2017.
 */
public class BarChartPanelImpl<TCategory> implements BarChartPanel<TCategory>, MouseWheelListener, MouseMotionListener {
    private final ChartOrientation orientation;

    protected Map<String, BarChart<TCategory>> charts = new LinkedHashMap<>();
    protected final java.util.List<TCategory> displayedCategories = new Vector<>();
    protected final BarChartPanelViewport viewport;
    protected Series<TCategory> categories;

    protected JPanel mainPanel;
    protected JScrollBar scrollBar;
    protected AdjustmentListener scrollBarListener;
    protected AtomicInteger lastX, lastY, lastCategoryIdx;
    protected Map<String, Map<String, List<String>>> tooltips;
    protected TooltipForm tooltipForm;
    protected LabelFormatter categoryLabelFormatter = new DefaultLabelFormatter();
    protected LabelFormatter valueLabelFormatter = new DefaultLabelFormatter();
    private java.awt.Rectangle screen;
    private final Timer updateTooltipTextTimer;
    private final HashMap<JPanel, BarChart<TCategory>> chartByPanel = new HashMap<>();
    private final JPanel rootPanel;
    private Timer timerRefresh;
    private int timerRefreshDelay = 50;

    private boolean showTooltipForm = false;

    public BarChartPanelImpl(ChartOrientation orientation) {
        viewport = new BarChartPanelViewport();
        final Icon autoScrollTrue = new ImageIcon(getClass().getClassLoader().getResource("locked.png"));
        final Icon autoScrollFalse = new ImageIcon(getClass().getClassLoader().getResource("unlocked.png"));
        lastX = new AtomicInteger();
        lastY = new AtomicInteger();
        lastCategoryIdx = new AtomicInteger();
        tooltips = new HashMap<>();
        this.orientation = orientation;
        rootPanel = new JPanel(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
        rootPanel.add(mainPanel, BorderLayout.CENTER);

        createRefreshTimer();

        JPanel scrollBarPanel = new JPanel(new BorderLayout());
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBarListener = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                setVisibleArea(e.getValue(), viewport.getNumberOfVisibleCategories());
            }
        };
        scrollBar.addAdjustmentListener(scrollBarListener);
        scrollBarPanel.add(scrollBar, BorderLayout.CENTER);
        final JButton autoScrollButton = new JButton(autoScrollTrue);
        autoScrollButton.setToolTipText("Autoscroll On/Off");
        autoScrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewport.setAutoScroll(!viewport.getAutoScroll());
                autoScrollButton.setIcon(viewport.getAutoScroll()?autoScrollTrue:autoScrollFalse);
            }
        });
        autoScrollButton.setPreferredSize(new Dimension(20, 20));
        scrollBarPanel.add(autoScrollButton, BorderLayout.EAST);
        rootPanel.add(scrollBarPanel, BorderLayout.SOUTH);

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
        chart.setMouseVariables(lastX, lastY, lastCategoryIdx);
        HashMap<String, List<String>> map = new HashMap<>();
        tooltips.put(id, map);
        chart.setTooltips(map);
        if(charts.size()==0) {
            chart.setHeight(rootPanel.getHeight());
        } else {
            chart.setHeight(OTHER_CHARTS_HEIGHT);
        }
        chart.addLayer(new CursorLayer(lastX), true);
        mainPanel.add(chart.getRootPanel());
        charts.put(id, chart);
        chart.getRootPanel().addMouseWheelListener(this);
        chart.getRootPanel().addMouseMotionListener(this);
        chart.getRootPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                tooltipForm.setVisible(false);
                showTooltipForm = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                tooltipForm.setVisible(true);
                showTooltipForm = true;
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
    public void setVisibleArea(final int first, final int number) {
        viewport.setNumberOfVisibleCategories(number);
        viewport.setFirstVisibleCategory(first);
    }

    @Override
    public int getFirstVisibleCategory() {
        return viewport.getFirstVisibleCategory();
    }

    @Override
    public int getNumberOfVisibleCategories() {
        return viewport.getNumberOfVisibleCategories();
    }

    @Override
    public void setCategories(Series<TCategory> categories) {
        this.categories = categories;
    }

    @Override
    public void paint() {
        if(charts!=null){
            for(BarChart<TCategory> chart: charts.values()){
                chart.paint();
            }
        }
    }

    public BarChartPanelViewport getViewport() {
        return viewport;
    }

    private void updateTooltipText_(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateTooltipText();
            }
        });
    }

    private void updateTooltipText() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                int idx = lastCategoryIdx.get();
                if (idx >= 0 && idx < viewport.getNumberOfVisibleCategories()) {
                    for (String chartId : charts.keySet()) {
                        for (BarChartLayer<TCategory> l : charts.get(chartId).getLayers()) {
                            String txt = getTooltipText(chartId, l.getId(), idx);
                            if (txt != null) {
                                if (sb.length() != 0) {
                                    sb.append("\n----------\n");
                                }
                                sb.append(txt);
                            }
                        }
                    }
                    String txt = sb.toString();
                    if(txt.equals("")){
                        tooltipForm.setVisible(false);
                    } else {
                        txt = txt.replace("\n----------\n", "<hr>").replace("\n", "<br>");
                        tooltipForm.setText("<html>" + txt + "</html>");
                        tooltipForm.setVisible(true && showTooltipForm);
                    }
                }
            }
        });
    }

    private String getTooltipText(String chartId, String layerId, int categoryIdx){
        List<String> list = tooltips.get(chartId).get(layerId);
        if(list!=null && categoryIdx<list.size()){
            return list.get(categoryIdx);
        }
        return null;
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

        int categoryIdx = lastCategoryIdx.get();
        if(categoryIdx < 0 || categoryIdx >= viewport.getNumberOfVisibleCategories()){
            tooltipForm.setVisible(false);
        } else {
            updateTooltipText();
            Point point = ((JPanel)e.getSource()).getLocationOnScreen();
            int x = point.x + lastX.get();
            int y = point.y + lastY.get();
            if(x + tooltipForm.getWidth() <= screen.getMaxX()){
                x = x+TOOLTIP_MARGIN;
            } else {
                x = x - tooltipForm.getWidth()-TOOLTIP_MARGIN;
            }
            if(y + tooltipForm.getHeight() <= screen.getMaxY()){
                y = y+TOOLTIP_MARGIN;
            } else {
                y = y - tooltipForm.getHeight()-TOOLTIP_MARGIN;
            }
            tooltipForm.setLocation(x, y);
        }
    }

    protected void updateLabelsConfig(){
        int i=0;
        for(BarChart c: charts.values()){
            c.getTopAxis().setVisible(false);
            c.getBottomAxis().setVisible(false);
            if(i==0){
                c.getTopAxis().setVisible(true);
                c.getBottomAxis().setVisible(false);
            }
            if(i==charts.size()-1){
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
        this.lastX.set(lastX);
    }

    protected void setLastY(int lastY) {
        this.lastY.set(lastY);
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

    private void createRefreshTimer() {
        timerRefresh = new Timer(timerRefreshDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(viewport.isChanged()){
                    int size = categories.getLength();
                    int num = viewport.getNumberOfVisibleCategories();
                    int fst = viewport.getFirstVisibleCategory();
                    if(num<2){
                        num = 2;
                    }

                    viewport.setNumberOfVisibleCategories(num);

                    if(size>0 && fst > size-getNumberOfVisibleCategories()) {
                        fst = size-getNumberOfVisibleCategories();
                    }
                    if(fst < 0){
                        fst = 0;
                    }
                    viewport.setFirstVisibleCategory(fst);


                    for(BarChart<TCategory> c: charts.values()){
                        c.setVisibleArea(viewport.getFirstVisibleCategory(), viewport.getNumberOfVisibleCategories());
                    }
                    updateCategories();
                    paint();
                    updateScrollbarAndSetValue();
                    updateTooltipText();
                    viewport.setChanged(false);
                }
            }
        });
        timerRefresh.start();
    }
}
