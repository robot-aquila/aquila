package ru.prolib.aquila.utils.experimental.chart.swing;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.Series;
import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.*;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.ValueAxisRendererImpl;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.AxisRendererStub;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.BarChartHistogramLayer;
import ru.prolib.aquila.utils.experimental.chart.swing.layers.BarChartIndicatorLayer;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisDirection;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisPosition;
import ru.prolib.aquila.utils.experimental.chart.axis.AxisRenderer;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriverImpl;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewportImpl;
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
	private final CategoryAxisViewport categoryAxisViewport;
	private final CategoryAxisDriver categoryAxisDriver;
	private AxisRenderer categoryAxisRendererLT, categoryAxisRendererRB;
	private final ValueAxisViewport valueAxisViewport;
	private final ValueAxisDriver valueAxisDriver;
	private AxisRenderer valueAxisRendererLT, valueAxisRendererRB;


	public BarChartImpl(CategoryAxisViewport categoryAxisViewport) {
		rootPanel = new JPanel(){
			@Override
			protected void paintComponent(Graphics g) {
				System.out.println("DBG: BarChartImpl#paintComponent called");
			    super.paintComponent(g);
			    BarChartImpl.this.paintComponent(g);
			}
			
		};
		this.categoryAxisViewport = categoryAxisViewport;
		categoryAxisDriver = new CategoryAxisDriverImpl(AxisDirection.RIGHT);
		setCategoryAxisRenderer(new AxisRendererStub(AxisPosition.TOP));
		setCategoryAxisRenderer(new AxisRendererStub(AxisPosition.BOTTOM));
		valueAxisViewport = new ValueAxisViewportImpl();
		valueAxisDriver = new ValueAxisDriverImpl(AxisDirection.UP);
		setValueAxisRenderer(new ValueAxisRendererImpl(AxisPosition.LEFT));
		setValueAxisRenderer(new ValueAxisRendererImpl(AxisPosition.RIGHT));
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
	
	public BarChartImpl() {
		this(new CategoryAxisViewportImpl());
	}
	
	public JPanel getRootPanel() {
		return rootPanel;
	}
	
	@Override
	public BarChartOrientation getOrientation() {
		return chartOrientation;
	}
	
	@Override
	public ValueAxisDriver getValueAxisDriver() {
		return valueAxisDriver;
	}
	
	@Override
	public CategoryAxisDriver getCategoryAxisDriver() {
		return categoryAxisDriver;
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
	public ChartElement getTopAxis() {
		return categoryAxisRendererLT;
	}
	
	@Override
	public ChartElement getLeftAxis() {
		return valueAxisRendererLT;
	}
	
	@Override
	public ChartElement getRightAxis() {
		return valueAxisRendererRB;
	}
	
	@Override
	public ChartElement getBottomAxis() {
		return categoryAxisRendererRB;
	}
	
	@Override
	public BarChart setCategoryAxisRenderer(AxisRenderer renderer) {
		switch ( renderer.getAxisPosition() ) {
		case TOP:
			categoryAxisRendererLT = renderer;
			break;
		case BOTTOM:
			categoryAxisRendererRB = renderer;
			break;
		case LEFT:
		case RIGHT:
			throw new IllegalStateException("Unsupported axis position: " + renderer.getAxisPosition());
		}
		return this;
	}
	
	@Override
	public BarChart setValueAxisRenderer(AxisRenderer renderer) {
		switch ( renderer.getAxisPosition() ) {
		case LEFT:
			valueAxisRendererLT = renderer;
			break;
		case RIGHT:
			valueAxisRendererRB = renderer;
			break;
		case TOP:
		case BOTTOM:
			throw new IllegalStateException("Unsupported axis position: " + renderer.getAxisPosition());
		}
		return this;
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
		BarChartHistogramLayer layer = new BarChartHistogramLayer(series);
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
	public BarChart setVisibleArea(int first, int number) {
		categoryAxisViewport.setCategoryRangeByFirstAndNumber(first, number);
		return this;
	}
	
	@Override
	public BarChart setValuesInterval(CDecimal minValue, CDecimal maxValue) {
		// TODO: set preferred value
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
		System.out.println("DBG: BarChartImpl width " + getRootPanel().getWidth());
		Graphics2D graphics = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHints(rh);
		
		// Setup layout
		Rectangle rootRect = new Rectangle(Point2D.ZERO,
				getRootPanel().getWidth(), getRootPanel().getHeight());
		ChartLayout layout = new ChartLayout(rootRect);
		
		// TODO: primary axis is horizontal, make it possible adjust automatically
		layout.setTopAxis(categoryAxisRendererLT.getPaintArea(layout, graphics));
		layout.setBottomAxis(categoryAxisRendererRB.getPaintArea(layout, graphics));
		layout.setLeftAxis(valueAxisRendererLT.getPaintArea(layout, graphics));
		layout.setRightAxis(valueAxisRendererRB.getPaintArea(layout, graphics));
		layout.autoPlotArea();

		Rectangle plot = layout.getPlotArea();
		CategoryAxisDisplayMapper categoryAxisMapper = categoryAxisDriver.createMapper(
				new Segment1D(plot.getLeftX(), plot.getWidth()), categoryAxisViewport);
		valueAxisViewport.setValueRange(getValueRange(
				categoryAxisMapper.getFirstVisibleCategory(),
				categoryAxisMapper.getNumberOfVisibleCategories()));
		ValueAxisDisplayMapper valueAxisMapper = valueAxisDriver.createMapper(
				new Segment1D(plot.getUpperY(), plot.getHeight()), valueAxisViewport);
		BCDisplayContext context = new BCDisplayContextImpl(categoryAxisMapper,
				valueAxisMapper, layout);
		
		if ( layout.getTopAxis() != null ) {
			categoryAxisRendererLT.paint(context, graphics);
		}
		if ( layout.getBottomAxis() != null ) {
			categoryAxisRendererRB.paint(context, graphics);
		}
		if ( layout.getLeftAxis() != null ) {
			valueAxisRendererLT.paint(context, graphics);
		}
		if ( layout.getRightAxis() != null ) {
			valueAxisRendererRB.paint(context, graphics);
		}

		drawGridLines();
		// TODO: set clip
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
