package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.ChartOverlay;

/**
 * Created by TiM on 05.09.2017.
 */
public class StaticOverlay implements ChartOverlay {
	private boolean visible = true;
    private String text;
    private int y = 0;

    public StaticOverlay(String text, int y) {
        this.text = text;
        this.y = y;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    @Override
    public void setVisible(boolean visible) {
    	this.visible = visible;
    }
    
    @Override
    public boolean isVisible() {
    	return visible;
    }
    
}
