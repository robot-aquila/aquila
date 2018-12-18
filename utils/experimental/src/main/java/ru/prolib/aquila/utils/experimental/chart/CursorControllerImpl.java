package ru.prolib.aquila.utils.experimental.chart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;

public class CursorControllerImpl implements CursorController {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CursorControllerImpl.class);
	}
	
	private final BarChartPanel panel;
	private final SelectedCategoryTrackerImpl sct;
	private String currentChartID;
	private int currentX, currentY;
	
	public CursorControllerImpl(BarChartPanel panel) {
		this.panel = panel;
		sct = new SelectedCategoryTrackerImpl();
	}
	
	private void updateCategoryTracker(CategoryAxisDisplayMapper cam) {
		sct.makeDeselected();
		if ( currentChartID == null ) {
			return;
		}
		Segment1D plot = cam.getPlot();
		if ( cam.getAxisDirection().isHorizontal() ) {
			if ( plot.contains(currentX) ) {
				try {
					int absoluteIndex = cam.toCategory(currentX);
					if ( absoluteIndex >= 0 ) {
						sct.makeSelected(absoluteIndex, absoluteIndex - cam.getFirstVisibleCategory());
					}
				} catch ( IllegalArgumentException e ) {
					logger.error("Unexpected exception: ", e);
				}
			}
		}
	}
	
	public SelectedCategoryTracker getCategoryTracker() {
		return sct;
	}
	
	public void update(CategoryAxisDisplayMapper cad) {
		updateCategoryTracker(cad);
	}

	@Override
	public void cursorEntered(String chartID, int x, int y) {
		currentChartID = chartID;
		currentX = x;
		currentY = y;
		panel.paint();
	}

	@Override
	public void cursorMoved(String chartID, int x, int y) {
		currentChartID = chartID;
		currentX = x;
		currentY = y;
		panel.paint();
	}

	@Override
	public void cursorExited(String chartID, int x, int y) {
		currentChartID = null;
		panel.paint();
	}

}
