package ru.prolib.aquila.utils.experimental.chart.swing;

import static java.awt.RenderingHints.*;

import java.awt.Dimension;
import java.awt.Graphics2D;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.*;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWValueAxisRulerRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWHistogramLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layer.SWIndicatorLayer;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverProxy;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSpace;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRulerFactoryCADProxy;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRulerFactoryVAD;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRulerRegistry;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRulerRegistryImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisViewportImpl;
import ru.prolib.aquila.utils.experimental.chart.interpolator.PolyLineRenderer;
import ru.prolib.aquila.utils.experimental.chart.interpolator.SmoothLineRenderer;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettings;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettingsButton;
import ru.prolib.aquila.utils.experimental.chart.swing.settings.ChartSettingsPopup;

import javax.swing.JComponent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Created by TiM on 09.09.2017.
 */
public class BarChartImpl implements BarChart, EventListener {
	private final JCompBarChart chartPanel;
	private final CategoryAxisDriverProxy cadProxy;
	private final ValueAxisViewport vav;
	private final ValueAxisDriver vad;
	private final ChartSpaceManager horizontalSpaceManager, verticalSpaceManager;
	private boolean zeroAtCenter;
	
	// TODO: refactor me
	private final List<BarChartLayer> layers = new Vector<>();
	private List<ChartOverlay> overlays = new Vector<>();
	private int height;
	private Map<String, List<String>> tooltips;
	private Set<String> systemLayers = new HashSet<>();
	private ChartSettingsButton chartSettingsButton;
	private final ChartSettings settings;
	private final ChartSettingsPopup settingsPopup;

	public BarChartImpl(CategoryAxisDriverProxy driverProxy) {
		chartPanel = new JCompBarChart();
		chartPanel.onPaint().addListener(this);
		
		verticalSpaceManager = ChartSpaceManagerImpl.ofVerticalSpace();
		cadProxy = driverProxy;
		cadProxy.registerForRulers(verticalSpaceManager);
		
		vav = new ValueAxisViewportImpl();
		vad = new ValueAxisDriverImpl("VALUE", AxisDirection.UP);
		vad.registerRenderer(new SWValueAxisRulerRenderer("LABEL"));
		horizontalSpaceManager = ChartSpaceManagerImpl.ofHorizontalSpace();
		horizontalSpaceManager.registerAxis(vad);
	
		// TODO: fix me
		settings = new ChartSettings(layers);
		settingsPopup = new ChartSettingsPopup(settings);
		settingsPopup.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO: wtf?
				//paint();
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			
			}
		});
		chartSettingsButton = new ChartSettingsButton(chartPanel, settingsPopup);
	}
	
	public JCompBarChart getChartPanel() {
		return chartPanel;
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
	public ValueAxisDriver getValueAxisDriver() {
		return vad;
	}
	
	@Override
	public CategoryAxisDriverProxy getCategoryAxisDriver() {
		return cadProxy;
	}

	@Override
	public BarChart setHeight(int points) {
		chartPanel.setPreferredSize(new Dimension(chartPanel.getWidth(), points));
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
		SWIndicatorLayer layer = new SWIndicatorLayer(series, new SmoothLineRenderer());
		addLayer(layer);
		return layer;
	}
	
	@Override
	public BarChartLayer addPolyLine(Series<CDecimal> series) {
		SWIndicatorLayer layer = new SWIndicatorLayer(series, new PolyLineRenderer());
		addLayer(layer);
		return layer;
	}
	
	@Override
	public BarChartLayer addHistogram(Series<CDecimal> series) {
		SWHistogramLayer layer = new SWHistogramLayer(series);
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
	public BarChart dropLayer(BarChartLayer layer) {
		return dropLayer(layer.getId());
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
	
	public void setTooltips(HashMap<String, List<String>> tooltips) {
		this.tooltips = tooltips;
	}
	
	@Override
	public void onEvent(Event event) {
		if ( ! event.isType(chartPanel.onPaint()) ) {
			return;
		}
		PaintEvent e = (PaintEvent) event;
		Graphics2D graphics = e.getGraphics();
		JComponent component = e.getComponent();
		//Point2D point;
		graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		
		Rectangle rootRect = new Rectangle(Point2D.ZERO, component.getWidth(), component.getHeight());
		CategoryAxisDisplayMapper cam = cadProxy.getCurrentMapper();
		int dh = rootRect.getHeight(), dw = rootRect.getWidth();
		ChartSpaceLayout xLayout, yLayout;
		xLayout = horizontalSpaceManager.prepareLayout(new Segment1D(0, dw), cam.getPlot(), graphics);
		// no more than 50% of data space width for rulers
		yLayout = verticalSpaceManager.prepareLayout(new Segment1D(0, dh), dh / 2, graphics);
		Segment1D dsX = xLayout.getDataSpace(), dsY = yLayout.getDataSpace();
		Rectangle plot = new Rectangle(new Point2D(dsX.getStart(), dsY.getStart()), dsX.getLength(), dsY.getLength());
		Rectangle canvas = new Rectangle(Point2D.ZERO, dw, dh);

		Range<CDecimal> vr = getValueRange(cam.getFirstVisibleCategory(), cam.getNumberOfVisibleCategories());
		vav.setValueRange(vr);
		ValueAxisDisplayMapper vam = vad.createMapper(dsY, vav);
		BCDisplayContext context = new BCDisplayContextImpl(cam, vam, plot, canvas);

		Rectangle rect;
		PreparedRulerRegistry prcache = new PreparedRulerRegistryImpl(new PreparedRulerFactoryVAD(vad, vam, graphics));
		for ( RulerSpace dummy : xLayout.getRulersToDisplay() ) {
			RulerID rulerID = dummy.getRulerID();
			Segment1D rs = dummy.getSpace();
			rect = new Rectangle(new Point2D(rs.getStart(), vam.getPlotStart()), rs.getLength(), vam.getPlotSize());
			prcache.getPreparedRuler(rulerID)
				.drawRuler(horizontalSpaceManager.getRulerSetup(rulerID), rect, graphics);
		}
		for ( GridLinesSetup setup : xLayout.getGridLinesToDisplay() ) {
			prcache.getPreparedRuler(setup.getRendererID()).drawGridLines(setup, plot, graphics);
		}
		
		prcache = new PreparedRulerRegistryImpl(new PreparedRulerFactoryCADProxy(cadProxy, cam, graphics));
		for ( RulerSpace dummy : yLayout.getRulersToDisplay() ) {
			RulerID rulerID = dummy.getRulerID();
			Segment1D rs = dummy.getSpace();
			rect = new Rectangle(new Point2D(cam.getPlotStart(), rs.getStart()), cam.getPlotSize(), rs.getLength());
			prcache.getPreparedRuler(rulerID)
				.drawRuler(verticalSpaceManager.getRulerSetup(rulerID), rect, graphics);
		}
		for ( GridLinesSetup setup : yLayout.getGridLinesToDisplay() ) {
			prcache.getPreparedRuler(setup.getRendererID()).drawGridLines(setup, plot, graphics);
		}

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
		
		// TODO: fix me
		graphics.setClip(0, 0, rootRect.getWidth(), rootRect.getHeight());
		chartSettingsButton.paint(graphics, rootRect.getWidth());
	}
	
	@Override
	public synchronized BarChart setZeroAtCenter(boolean zeroAtCenter) {
		this.zeroAtCenter = zeroAtCenter;
		return this;
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
		if ( range == null ) {
			range = new Range<>(CDecimalBD.ZERO, CDecimalBD.of(1000000L));
		}
		synchronized ( this ) {
			if ( zeroAtCenter ) {
				CDecimal absMax = range.getMin().abs().max(range.getMax().abs());
				range = new Range<>(absMax.negate(), absMax);
			}
		}
		return range;
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
