package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.core.data.Series;

/**
 * Created by TiM on 08.09.2017.
 */
public interface BarChartPanel<TCategory> {
	
	ChartOrientation getOrientation();
	
    BarChart<TCategory> addChart(String id);
    BarChart<TCategory> getChart(String id);
    
    /**
     * Set visible area indices.
     * <p>
     * @param first - first visible category index
     * @param number - number of visible categories from start. It may be greater
     * than actual size of category series and it is OK.
     */
    void setVisibleArea(int first, int number);
    
    /**
     * Get index of first visible category.
     * <p>
     * @return index of first visible category
     */
    int getFirstVisibleCategory();
    
    /**
     * Get number of visible categories.
     * <p>
     * @return number of visible categories
     */
    int getNumberOfVisibleCategories();
    
    void setCategories(Series<TCategory> categories);

    void paint();
}
