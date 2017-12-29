package ru.prolib.aquila.utils.experimental.chart;

public class ChartLayout {
	private Rectangle root, topAxis, bottomAxis, leftAxis, rightAxis, plot;
	
	public ChartLayout(Rectangle root) {
		this.root = root;
	}

	/**
	 * Get whole painting area rectangle.
	 * <p>
	 * @return rectangle of whole painting area. Cannot be null.
	 */
	public Rectangle getRoot() {
		return root;
	}
	
	/**
	 * Get rectangle of top axis painting area.
	 * <p>
	 * @return rectangle or null if top axis area is not yet determined or invisible
	 */
	public Rectangle getTopAxis() {
		return topAxis;
	}
	
	/**
	 * Get rectangle of bottom axis painting area.
	 * <p>
	 * @return rectangle or null if bottom axis is not yet determined or invisible
	 */
	public Rectangle getBottomAxis() {
		return bottomAxis;
	}
	
	/**
	 * Get rectangle of left axis painting area.
	 * <p>
	 * @return rectangle or null if left axis is not yet determined or invisible
	 */
	public Rectangle getLeftAxis() {
		return leftAxis;
	}
	
	/**
	 * Get rectangle of right axis painting area.
	 * <p>
	 * @return rectangle or null if right axis is not yet determined or invisible
	 */
	public Rectangle getRightAxis() {
		return rightAxis;
	}
	
	/**
	 * Get rectangle of the plot area.
	 * <p>
	 * @return rectangle or null if plot area is not yet determined or invisible
	 */
	public Rectangle getPlotArea() {
		return plot;
	}
	
	public void setTopAxis(Rectangle area) {
		this.topAxis = area;
	}
	
	public void setBottomAxis(Rectangle area) {
		this.bottomAxis = area;
	}
	
	public void setLeftAxis(Rectangle area) {
		this.leftAxis = area;
	}
	
	public void setRightAxis(Rectangle area) {
		this.rightAxis = area;
	}
	
	public void setPlotArea(Rectangle area) {
		this.plot = area;
	}
	
	/**
	 * Calculate plot area according to other areas usage.
	 * <p>
	 * This method is for automatic calculation of plot area. It uses
	 * information of axis rectangles to determine how much is available for
	 * plotting the chart.
	 */
	public void autoPlotArea() {
		int leftX = root.getLeftX(),
			upperY = root.getUpperY(),
			rightX = root.getRightX(),
			lowerY = root.getLowerY();
		if ( leftAxis != null ) {
			leftX = leftAxis.getRightX() + 1;
		}
		if ( topAxis != null ) {
			upperY = topAxis.getLowerY() - 1;
		}
		if ( rightAxis != null ) {
			rightX = rightAxis.getLeftX() - 1;
		}
		if ( bottomAxis != null ) {
			lowerY = bottomAxis.getUpperY() + 1;
		}
		plot = new Rectangle(new Point2D(leftX, upperY), rightX - leftX + 1, upperY - lowerY + 1);
	}

}
