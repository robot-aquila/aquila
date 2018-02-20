package ru.prolib.aquila.utils.experimental.chart.swing;

import static java.awt.RenderingHints.*;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.*;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.BCHistogramLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.BarChartIndicatorLayer;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverProxy;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.ChartRulerSpace;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.chart.interpolator.SmoothLineRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettings;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettingsButton;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettingsPopup;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by TiM on 09.09.2017.
 */
public class BarChartImpl implements BarChart {
	private final JPanel rootPanel;
	private final List<BarChartLayer> layers = new Vector<>();
	private List<ChartOverlay> overlays = new Vector<>();
	private int height;
	private AtomicInteger lastX, lastY, lastCategoryIdx;
	private Map<String, List<String>> tooltips;
	private Set<String> systemLayers = new HashSet<>();
	private ChartSettingsButton chartSettingsButton;
	private final ChartSettings settings;
	private final ChartSettingsPopup settingsPopup;
	
	private BarChartOrientation chartOrientation;
	private final CategoryAxisDriverProxy cadProxy;
	private final ValueAxisViewport vav;
	private final ValueAxisDriver vad;
	private final ChartSpaceManager horizontalSpaceManager, verticalSpaceManager;

	public BarChartImpl(CategoryAxisDriverProxy driverProxy) {
		rootPanel = new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				System.out.println("DBG: BarChartImpl#paintComponent called");
			    super.paintComponent(g);
			    BarChartImpl.this.paintComponent(g);
			}
			
		};
		verticalSpaceManager = ChartSpaceManagerImpl.ofVerticalSpace();
		cadProxy = driverProxy;
		cadProxy.registerForRulers(verticalSpaceManager);
		
		vav = new ValueAxisViewportImpl();
		vad = new ValueAxisDriverImpl("VALUE", AxisDirection.UP);
		vad.registerRenderer(new SWValueAxisRulerRenderer("LABEL"));
		horizontalSpaceManager = ChartSpaceManagerImpl.ofHorizontalSpace();
		horizontalSpaceManager.registerAxis(vad);
		
		settings = new ChartSettings(layers);
		settingsPopup = new ChartSettingsPopup(settings);
		settingsPopup.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				paint();
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			
			}
		});
		chartSettingsButton = new ChartSettingsButton(getRootPanel(), settingsPopup);
	}
	
	public JPanel getRootPanel() {
		return rootPanel;
	}
	
	@Override
	public ChartSpaceManager getHorizontalSpaceManager() {
		return horizontalSpaceManager;
	}
	
	@Override
	public ChartSpaceManager getVerticalSpaceManager() {
		return verticalSpaceManager;
	}
	
	@Override
	public BarChartOrientation getOrientation() {
		return chartOrientation;
	}
	
	@Override
	public ValueAxisDriver getValueAxisDriver() {
		return vad;
	}
	
	@Override
	public CategoryAxisDriverProxy getCategoryAxisDriver() {
		return cadProxy;
	}

	@Override
	public BarChart setHeight(int points) {
		rootPanel.setPreferredSize(new Dimension(rootPanel.getWidth(), points));
		this.height = points;
		return this;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	@Override
	public List<BarChartLayer> getLayers() {
		return layers;
	}
	
	@Override
	public BarChartLayer getLayer(String id) {
		List<BarChartLayer> list = getLayers();
		for(BarChartLayer l: list){
			if(l.getId().equals(id)){
				return l;
			}
		}
		return null;
	}

	@Override
	public BarChartLayer addLayer(BarChartLayer layer) {
		return addLayer(layer, false);
	}
	
	public BarChartLayer addLayer(BarChartLayer layer, boolean isSystem) {
		// TODO: do something with tooltips
		//List<String> list = new Vector<String>();
		//tooltips.put(layer.getId(), list);
		//((AbstractBarChartLayer) layer).setTooltips(list);
		layers.add(layer);
		if ( isSystem ) {
			systemLayers.add(layer.getId());
		}
		return layer;
	}
	
	@Override
	public BarChartLayer addSmoothLine(Series<CDecimal> series) {
		BarChartIndicatorLayer layer = new BarChartIndicatorLayer(series, new SmoothLineRenderer());
		addLayer(layer);
		return layer;
	}
	
	@Override
	public BarChartLayer addPolyLine(Series<CDecimal> series) {
		BarChartIndicatorLayer layer = new BarChartIndicatorLayer(series, new PolyLineRenderer());
		addLayer(layer);
		return layer;
	}
	
	@Override
	public BarChartLayer addHistogram(Series<CDecimal> series) {
		BCHistogramLayer layer = new BCHistogramLayer(series);
		addLayer(layer);
		return layer;
	}
	
	@Override
	public BarChart dropLayer(String id) {
		if(!systemLayers.contains(id)){
			BarChartLayer layer = getLayer(id);
			if(layer != null) {
				layers.remove(layer);
			}
		}
		return this;
	}
	
	@Override
	public List<ChartOverlay> getOverlays() {
		return overlays;
	}
	
	@Override
	public BarChart addStaticOverlay(String text, int y) {
		overlays.add(new StaticOverlay(text, y));
		return this;
	}
	
	@Override
	public BarChart addOverlay(ChartOverlay overlay) {
		overlays.add(overlay);
		return this;
	}
	
	@Override
	public void paint() {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					paint();
				}
			});
		}
		rootPanel.repaint();
	}

	public void setMouseVariables(AtomicInteger lastX, AtomicInteger lastY, AtomicInteger lastCategoryIdx) {
		this.lastX = lastX;
		this.lastY = lastY;
		this.lastCategoryIdx = lastCategoryIdx;
	}
	
	public void setTooltips(HashMap<String, List<String>> tooltips) {
		this.tooltips = tooltips;
	}
	
	protected void paintComponent(Graphics g) {
		Point2D point;
		System.out.println("DBG: BarChartImpl width " + getRootPanel().getWidth());
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHints(new RenderingHints(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON));
		
		// Setup layout
		Rectangle rootRect = new Rectangle(Point2D.ZERO, getRootPanel().getWidth(), getRootPanel().getHeight());
		ChartLayout layout = new ChartLayout(rootRect);

		CategoryAxisDisplayMapper cam = cadProxy.getCurrentMapper();
		int dh = getRootPanel().getHeight(),
			dw = getRootPanel().getWidth();
		ChartSpaceLayout xLayout, yLayout;
		xLayout = horizontalSpaceManager.prepareLayout(new Segment1D(0, dw), cam.getPlot(), graphics);
		// no more than 50% of data space width for rulers
		yLayout = verticalSpaceManager.prepareLayout(new Segment1D(0, dh), dh / 2, graphics);
		Segment1D dsX = xLayout.getDataSpace(), dsY = yLayout.getDataSpace();
		point = new Point2D(dsX.getStart(), dsY.getStart());
		layout.setPlotArea(new Rectangle(point, dsX.getLength(), dsY.getLength()));

		Range<CDecimal> vr = getValueRange(cam.getFirstVisibleCategory(), cam.getNumberOfVisibleCategories());
		vav.setValueRange(vr);
		ValueAxisDisplayMapper vam = vad.createMapper(dsY, vav);
		BCDisplayContext context = new BCDisplayContextImpl(cam, vam, layout);

		RulerPosition rpos;
		// TODO: replace string to special axisID+rendererID key
		ValueAxisRulerRenderer var;
		Map<String, PreparedRuler> preparedRulers = new HashMap<>();
		for ( ChartRulerSpace dummy : xLayout.getRulers() ) {
			ChartRulerID rulerID = dummy.getRulerID();
			Segment1D rulerSpace = dummy.getSpace();
			PreparedRuler preparedRuler = preparedRulers.get(rulerID.getRendererID());
			if ( preparedRuler == null ) {
				var = (ValueAxisRulerRenderer) vad.getRenderer(rulerID.getRendererID());
				preparedRuler = var.prepareRuler(vam, graphics);
				preparedRulers.put(rulerID.getRendererID(), preparedRuler);
			}
			rpos = rulerID.isLowerPosition() ? RulerPosition.LEFT : RulerPosition.RIGHT;
			point = new Point2D(rulerSpace.getStart(), vam.getPlotStart());
			preparedRuler.drawRuler(rpos, new Rectangle(point, rulerSpace.getLength(), vam.getPlotSize()), graphics);
		}
		
		CategoryAxisRulerRenderer car;
		preparedRulers.clear();
		for ( ChartRulerSpace dummy : yLayout.getRulers() ) {
			ChartRulerID rulerID = dummy.getRulerID();
			Segment1D rulerSpace = dummy.getSpace();
			PreparedRuler preparedRuler = preparedRulers.get(rulerID.getRendererID());
			if ( preparedRuler == null ) {
				 car = cadProxy.getRulerRenderer(rulerID.getRendererID());
				 preparedRuler = car.prepareRuler(cam, graphics);
				 preparedRulers.put(rulerID.getRendererID(), preparedRuler);
			}
			rpos = rulerID.isLowerPosition() ? RulerPosition.TOP : RulerPosition.BOTTOM;
			point = new Point2D(cam.getPlotStart(), rulerSpace.getStart());
			preparedRuler.drawRuler(rpos, new Rectangle(point, cam.getPlotSize(), rulerSpace.getLength()), graphics);
		}
		
		// TODO: draw grid lines
		//valueAxisRuler.drawGrid(plot, graphics);

		drawGridLines();
		// TODO: set clip???
		//g2.clip(new Rectangle2D.Double(g2.getClipBounds().getMinX(),
		//		vc.getPlotBounds().getUpperLeftY(),
		//		g2.getClipBounds().getWidth(),
		//		vc.getPlotBounds().getHeight()));
		for ( BarChartLayer layer: layers ) {
			layer.paint(context, graphics);
		}
		drawOverlays();
		
		// what's this?
		//lastCategoryIdx.set(vc.toCategoryIdx(lastX.get(), lastY.get()));
		
		graphics.setClip(0, 0, getRootPanel().getWidth(), getRootPanel().getHeight());
		chartSettingsButton.paint(graphics, getRootPanel().getWidth());
	}
	
	private Range<CDecimal> getValueRange(int first, int num) {
		Range <CDecimal> x, range = null;
		for ( BarChartLayer layer : layers ) {
			x = layer.getValueRange(first, num);
			if ( range == null ) {
				range = x;
			} else {
				range = range.extend(x);
			}
		}
		if ( range != null ) {
			return range;
		}
		return new Range<>(CDecimalBD.ZERO, CDecimalBD.of(1000000L));
	}
	
	private void drawGridLines(){
		// TODO: fixme
		/*
		Graphics2D g = (Graphics2D) vc.getGraphics().create();
		try {
			g.setColor(GRID_LINES_COLOR);
			Rectangle2D plotBounds = new Rectangle2D.Double(vc.getPlotBounds().getX(), vc.getPlotBounds().getY(), vc.getPlotBounds().getWidth(), vc.getPlotBounds().getHeight());
			g.draw(plotBounds);
			for(int i=0; i<numberOfVisibleCategories; i++){
				int x = vc.toCanvasX(i);
				g.draw(new Line2D.Double(x, plotBounds.getMinY(), x, plotBounds.getMaxY()));
			}
			RangeInfo ri = vc.getRangeInfo();
			for(double yVal=ri.getFirstValue(); yVal<=ri.getLastValue()+(1e-6); yVal+=ri.getStepValue()){
				int y = vc.toCanvasY(yVal);
				g.draw(new Line2D.Double(plotBounds.getMinX(), y, plotBounds.getMaxX(), y));
			}
		} finally {
			g.dispose();
		}
		*/
	}
	
	private void drawOverlays(){
		/*
		Graphics2D g2 = (Graphics2D) vc.getGraphics().create();
		g2.setColor(CHART_OVERLAY_COLOR);
		g2.setFont(new Font("default", Font.BOLD, CHART_OVERLAY_FONT_SIZE));
		int height = Math.round(g2.getFontMetrics().getHeight());
		for(ChartOverlay o: overlays){
			int x = vc.getPlotBounds().getUpperLeftX() + LABEL_INDENT;
			int y;
			if(o.getY()>=0){
				y = vc.getPlotBounds().getUpperLeftY() + LABEL_INDENT + height;
			} else {
				y = vc.getPlotBounds().getUpperLeftY()+vc.getPlotBounds().getHeight() - LABEL_INDENT;
			}
			g2.drawString(o.getText(), x, y + o.getY());
		}
		*/
	}

}
