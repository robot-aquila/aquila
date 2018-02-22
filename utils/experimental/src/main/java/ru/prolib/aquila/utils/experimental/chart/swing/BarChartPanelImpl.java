package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.core.data.ObservableTSeries;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.utils.experimental.chart.*;
import ru.prolib.aquila.utils.experimental.chart.TooltipForm;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverProxyImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.BarChartCursorLayer;

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
public class BarChartPanelImpl implements BarChartPanel, MouseWheelListener, MouseMotionListener {
	private final BarChartOrientation orientation;
	protected Map<String, BarChart> charts = new LinkedHashMap<>();
	protected final CategoryAxisViewport cav;
	protected final CategoryAxisDriver cad;
	protected final CategoryAxisDriverProxyImpl cadProxy;
	private final ScrollBarController scrollBarController;

	protected JPanel mainPanel;
	protected JScrollBar scrollBar;
	protected AdjustmentListener scrollBarListener;
	protected AtomicInteger lastX, lastY, lastCategoryIdx;
	protected Map<String, Map<String, List<String>>> tooltips;
	protected TooltipForm tooltipForm;
	private java.awt.Rectangle screen;
	//private final Timer updateTooltipTextTimer;
	private final HashMap<JPanel, BarChart> chartByPanel = new HashMap<>();
	private final JPanel rootPanel;
	private Timer timerRefresh;
	private int timerRefreshDelay = 50;
	
	private boolean showTooltipForm = false;

    public BarChartPanelImpl(BarChartOrientation orientation) {
        cav = new CategoryAxisViewportImpl();
        cad = new CategoryAxisDriverImpl("CATEGORY", AxisDirection.RIGHT);
        cadProxy = new CategoryAxisDriverProxyImpl(cad);
        scrollBarController = new ScrollBarController();
        
        lastX = new AtomicInteger();
        lastY = new AtomicInteger();
        lastCategoryIdx = new AtomicInteger();
        tooltips = new HashMap<>();
        this.orientation = orientation;
        rootPanel = new JPanel(new BorderLayout()) {
        	private boolean init = false;
        	
        	@Override
        	protected void paintComponent(Graphics g) {
        		//System.out.println("DBG: BarChartPanelImpl#paintComponent called");
        		super.paintComponent(g);
        		BarChartPanelImpl.this.paintComponent(g);
        	}
        	
        	@Override
        	protected void paintChildren(Graphics g) {
        		//System.out.println("DBG: BarChartPanelImpl#paintChildren called");
        		BarChartPanelImpl.this.beforePaintChildren(g);
        		super.paintChildren(g);
        		//System.out.println("DBG: BarChartPanelImpl#paintChildren exiting");
        		if ( init == false ) {
        			//System.out.println("DBG: BarChartPanelImpl#paintChildren initialization done. Repaint.");
        			init = true;
        			repaint();
        		}
        	}
        };
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS) );
        rootPanel.add(mainPanel, BorderLayout.CENTER);

        // TODO: remove me
        //createRefreshTimer();

        AutoScrollButton autoScrollButton = new AutoScrollButton();
        scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        JPanel scrollBarPanel = new JPanel(new BorderLayout());
        scrollBarPanel.add(scrollBar, BorderLayout.CENTER);
        scrollBarPanel.add(autoScrollButton, BorderLayout.EAST);
        rootPanel.add(scrollBarPanel, BorderLayout.SOUTH);
        scrollBarController.setScrollBar(scrollBar);
        scrollBarController.setAutoScrollButton(autoScrollButton);
        scrollBarController.setViewport(cav);
        scrollBarController.setRootPanel(rootPanel);

        tooltipForm = new TooltipForm();
        screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

        // TODO: Выпили меня
        //updateTooltipTextTimer = new Timer(500, new ActionListener() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        updateTooltipText_();
        //    }
        //});
        //updateTooltipTextTimer.setRepeats(false);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
    
	@Override
	public void setCategories(ObservableTSeries<?> categories) {
		scrollBarController.setCategories(categories);
	}

    @Override
    public BarChartOrientation getOrientation() {
        return orientation;
    }

    @Override
    public BarChart addChart(String id) {
        if(charts.containsKey(id)){
            throw new IllegalArgumentException("Chart with id='"+id+"' already added");
        }
        BarChartImpl chart = new BarChartImpl(cadProxy);
        chart.setMouseVariables(lastX, lastY, lastCategoryIdx);
        HashMap<String, List<String>> map = new HashMap<>();
        tooltips.put(id, map);
        chart.setTooltips(map);
        if(charts.size()==0) {
            chart.setHeight(rootPanel.getHeight());
        } else {
            chart.setHeight(OTHER_CHARTS_HEIGHT);
        }
        chart.addLayer(new BarChartCursorLayer(lastX), true);
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
		getRootPanel().repaint();
	}

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
    */

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    	/*
        int first = getFirstVisibleCategory();
        int number = getNumberOfVisibleCategories();
        if(e.isControlDown()){
            number += e.getWheelRotation();
        } else {
            first += e.getWheelRotation();
        }
        setVisibleArea(first, number);
        */
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    	/*
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
        */
    }

    protected void updateLabelsConfig(){
    	/*
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
            //if(c.getValuesLabelFormatter().getClass().equals(DefaultLabelFormatter.class)){
            //    c.setValuesLabelFormatter(valueLabelFormatter);
            //}
            i++;
        }
        */
    }

    protected void setLastX(int lastX) {
        this.lastX.set(lastX);
    }

    protected void setLastY(int lastY) {
        this.lastY.set(lastY);
    }
	
	protected void paintComponent(Graphics g) {
		//System.out.println("DBG: BarChartPanelImpl width " + mainPanel.getWidth());
	}
	
	protected void beforePaintChildren(Graphics graphics) {
		//System.out.println("DBG: BarChartPanelImpl#beforePaintChildren");
		if ( cad.getAxisDirection().isVertical() ) {
			throw new UnsupportedOperationException("Orientation not supported");
		}
		
		Segment1D displaySpace = new Segment1D(0, getRootPanel().getWidth());
		int rulersMaxSpace = displaySpace.getLength() / 2;
		ChartSpaceLayout bestLayout = null;
		for ( BarChart chart : charts.values() ) {
			ChartSpaceLayout layout = chart.getHorizontalSpaceManager()
					.prepareLayout(displaySpace, rulersMaxSpace, graphics);
			// TODO: To make a better approach
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
		//System.out.println("DBG: ParChartPanelImpl#beforePaintChildren dataSpace=" + dataSpace);
	}

}
