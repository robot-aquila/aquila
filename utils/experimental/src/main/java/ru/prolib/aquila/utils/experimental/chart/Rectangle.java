package ru.prolib.aquila.utils.experimental.chart;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Created by TiM on 11.09.2017.
 */
public class Rectangle {
	private final Point2D upperLeft, lowerRight;
    private final int width, height;
    private final Rectangle parent;

    public Rectangle(Point2D upperLeft, int width, int height, Rectangle parent) {
    	if ( width <= 0 || height <= 0 ) {
    		throw new IllegalArgumentException();
    	}
    	this.upperLeft = upperLeft;
    	this.lowerRight = new Point2D(upperLeft.getX() + width - 1, upperLeft.getY() - height + 1);
        this.width = width;
        this.height = height;
        this.parent = parent;
    }
    
    public Rectangle(Point2D upperLeft, int width, int height) {
    	this(upperLeft, width, height, null);
    }
    
    public Rectangle getParent() {
    	return parent;
    }
    
    public Point2D getUpperLeft() {
    	return upperLeft;
    }

    public int getLeftX() {
        return upperLeft.getX();
    }

    public int getUpperY() {
        return upperLeft.getY();
    }
    
    public Point2D getLowerRight() {
    	return lowerRight;
    }
    
    public int getRightX() {
    	return lowerRight.getX();
    }
    
    public int getLowerY() {
    	return lowerRight.getY();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    @Override
    public boolean equals(Object other) {
    	if ( other == this ) {
    		return true;
    	}
    	if ( other == null || other.getClass() != Rectangle.class ) {
    		return false;
    	}
    	Rectangle o = (Rectangle) other;
    	return new EqualsBuilder()
    			.append(o.upperLeft, upperLeft)
    			.append(o.width, width)
    			.append(o.height, height)
    			.append(o.parent, parent)
    			.isEquals();
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[x=" + upperLeft.getX()
    		+ ",y=" + upperLeft.getY()
    		+ ",w=" + width
    		+ ",h=" + height
    		+ (parent != null ? ",w/parent" : "")
    		+ "]";
    }

}
