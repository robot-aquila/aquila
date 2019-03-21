package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.ObservableSeries;
import ru.prolib.aquila.utils.experimental.chart.*;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverProxyImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.prolib.aquila.utils.experimental.chart.ChartConstants.OTHER_CHARTS_HEIGHT;

/**
 * Created by TiM on 08.09.2017.
 */
public class BarChartPanelImpl implements BarChartPanel, EventListener/*, MouseWheelListener, MouseMotionListener*/ {
	protected Map<String, BarChart> charts = new LinkedHashMap<>();
	protected final CategoryAxisViewport cav;
	protected final CategoryAxisDriver cad;
	protected final CategoryAxisDriverProxyImpl cadProxy;
	private final ScrollBarController scrollBarController;
	private final CursorControllerImpl cursorController;
	private final JCompBarChartPanel rootPanel;
	
	@Deprecated
	protected AtomicInteger lastX, lastY, lastCategoryIdx;
	@Deprecated
	protected Map<String, Map<String, List<String>>> tooltips;
	@Deprecated
	protected TooltipForm tooltipForm;
	//private java.awt.Rectangle screen;
	@Deprecated
	private final HashMap<JPanel, BarChart> chartByPanel = new HashMap<>();
	//private boolean showTooltipForm = false;

    public BarChartPanelImpl(BarChartOrientation orientation) {
		rootPanel = new JCompBarChartPanel();
		rootPanel.onBeforePaintChildren().addListener(this);
		cav = new CategoryAxisViewportImpl();
		cad = new CategoryAxisDriverImpl("CATEGORY", AxisDirection.RIGHT);
		cadProxy = new CategoryAxisDriverProxyImpl(cad);
		scrollBarController = new ScrollBarController();
		cursorController = new CursorControllerImpl(this);
		
		scrollBarController.setRootPanel(rootPanel);
		scrollBarController.setScrollBar(rootPanel.getScrollBar());
		scrollBarController.setAutoScrollButton(rootPanel.getAutoScrollButton());
		scrollBarController.setViewport(cav);
		
		// TODO: to remove
		lastX = new AtomicInteger();
		lastY = new AtomicInteger();
		lastCategoryIdx = new AtomicInteger();
		tooltips = new HashMap<>();
		
		tooltipForm = new TooltipForm();
		//screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    }

    public JCompBarChartPanel getRootPanel() {
        return rootPanel;
    }
    
    @Override
    public SelectedCategoryTracker getCategoryTracker() {
    	return cursorController.getCategoryTracker();
    }
    
	@Override
	public void setCategories(ObservableSeries<?> categories) {
		scrollBarController.setCategories(categories);
		rootPanel.setPaintLockable(categories);
	}

    @Override
    public BarChart addChart(String chartID) {
        if ( charts.containsKey(chartID) ) {
            throw new IllegalArgumentException("Chart already exists: " + chartID);
        }
        BarChartImpl chart = new BarChartImpl(cadProxy);
		MouseAdapter tracker = new MouseAdapter(chartID, cursorController);
		chart.getChartPanel().addMouseListener(tracker);
		chart.getChartPanel().addMouseMotionListener(tracker);
        HashMap<String, List<String>> map = new HashMap<>();
        tooltips.put(chartID, map);
        // TODO: fix me
        //chart.setTooltips(map);
        if ( charts.size() == 0 ) {
            chart.setHeight(rootPanel.getHeight());
        } else {
            chart.setHeight(OTHER_CHARTS_HEIGHT);
        }
        //chart.addLayer(new SWBarHighlighter(lastX), true); // TODO: to remove
        rootPanel.getMainPanel().add(chart.getChartPanel());
        charts.put(chartID, chart);
        // TODO: fix me
        //chart.getRootPanel().addMouseWheelListener(this);
        //chart.getRootPanel().addMouseMotionListener(this);
        //chart.getRootPanel().addMouseListener(new MouseAdapter() {
        //    @Override
        //    public void mouseExited(MouseEvent e) {
        //        tooltipForm.setVisible(false);
        //        showTooltipForm = false;
        //    }
        //
        //    @Override
        //    public void mouseEntered(MouseEvent e) {
        //        tooltipForm.setVisible(true);
        //        showTooltipForm = true;
        //    }
        //});
        chartByPanel.put(chart.getChartPanel(), chart);
        getRootPanel().validate();
        getRootPanel().repaint();
        return chart;
    }

    @Override
    public BarChart getChart(String id) {
        return charts.get(id);
    }

    @Override
    public CategoryAxisViewport getCategoryAxisViewport() {
    	return cav;
    }
    
    @Override
    public CategoryAxisDriver getCategoryAxisDriver() {
    	return cad;
    }
    
	@Override
	public void paint() {
		rootPanel.repaint();
	}
	
	protected void onBeforePaintChildren(PaintEvent e) {
		Graphics2D graphics = e.getGraphics();
		Segment1D displaySpace = new Segment1D(0, e.getComponent().getWidth());
		int rulersMaxSpace = displaySpace.getLength() / 2;
		ChartSpaceLayout bestLayout = null;
		for ( BarChart chart : charts.values() ) {
			ChartSpaceLayout layout = chart.getHorizontalSpaceManager()
					.prepareLayout(displaySpace, rulersMaxSpace, graphics);
			// TODO: make it better
			if ( bestLayout == null
			  || bestLayout.getDataSpace().getLength() > layout.getDataSpace().getLength() )
			{
				bestLayout = layout;
			}
		}
		Segment1D dataSpace = bestLayout == null ? displaySpace : bestLayout.getDataSpace();
		CategoryAxisDisplayMapper cam = cad.createMapper(dataSpace, cav);
		cadProxy.setCurrentMapper(cam);
		scrollBarController.setDisplayMapper(cam);
		cursorController.update(cam);
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(rootPanel.onBeforePaintChildren()) ) {
			if ( cad.getAxisDirection().isVertical() ) {
				throw new UnsupportedOperationException("Orientation not supported");
			}
			onBeforePaintChildren((PaintEvent) event);
		}
	}

	// TODO: refactor me

    /*
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
                        for (BarChartLayer l : charts.get(chartId).getLayers()) {
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

    protected void setLastX(int lastX) {
        this.lastX.set(lastX);
    }

    protected void setLastY(int lastY) {
        this.lastY.set(lastY);
    }

    */
	
}
