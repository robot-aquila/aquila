package ru.prolib.aquila.utils.experimental.chart;

import java.util.List;

import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSpace;

/**
 * Разметка пространства графика.
 * <p>
 * Описывает макет график в одной из размерностей (например, в вертикальной
 * или горизонтальной).
 */
public interface ChartSpaceLayout {
	
	/**
	 * Получить пространство всех линеек в области меньших значений.
	 * <p>
	 * @return сегмент, описывающий пространство, занимаемое всеми линейками
	 * в области меньших значений (lower position относительно графика).
	 */
	Segment1D getLowerRulersTotalSpace();
	
	/**
	 * Получить пространство всех линеек в области больших значений.
	 * <p>
	 * @return сегмент, описывающий пространство, занимаемое всеми линейками
	 * в области больших значений (upper position относительно графика).
	 */
	Segment1D getUpperRulersTotalSpace();
	
	/**
	 * Получить пространство области отображения данных графика.
	 * <p>
	 * @return сегмент, описывающий пространство области данных графика.
	 */
	Segment1D getDataSpace();
	
	/**
	 * Получить параметры отображения линеек.
	 * <p>
	 * @return параметры всех линеек, подлежащих отображению
	 */
	List<RulerSpace> getRulersToDisplay();
	
	/**
	 * Get sorted list of grid lines to display.
	 * <p>
	 * @return list of grid lines
	 */
	List<GridLinesSetup> getGridLinesToDisplay();

}
