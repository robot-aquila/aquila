package ru.prolib.aquila.ta.ds.quik;

import java.util.Date;

/**
 * Точка актуальности.
 */
public class ExportQuikActualityPoint {
	protected final Date time;
	protected final long number;
	
	public ExportQuikActualityPoint(Date time, long number) {
		this.time = time;
		this.number = number;
	}
	
	/**
	 * Проверка условия что объект старше указанной точки актуальности
	 * @param obj - точка актуальности
	 * @return
	 */
	public boolean before(ExportQuikActualityPoint obj) {
		return obj != null && (time.before(obj.time) || number < obj.number);
	}
	
}