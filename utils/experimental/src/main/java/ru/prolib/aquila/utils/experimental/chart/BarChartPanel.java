package ru.prolib.aquila.utils.experimental.chart;

import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDriver;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisViewport;

/**
 * Created by TiM on 08.09.2017.
 */
public interface BarChartPanel {
	
	BarChartOrientation getOrientation();
	
    BarChart addChart(String id);
    BarChart getChart(String id);
    
    CategoryAxisViewport getCategoryAxisViewport();
    CategoryAxisDriver getCategoryAxisDriver();

    void paint();
}
