package ru.prolib.aquila.utils.experimental.chart.axis;

public interface CategoryAxisViewport {
	
	int getFirstCategory();
	
	int getLastCategory();
	
	int getNumberOfCategories();
	
	void setCategoryRangeByFirstAndNumber(int first, int number);
	
	void setCategoryRangeByLastAndNumber(int last, int number);
	
	void setCategoryRangeByFirstAndLast(int first, int last);
	
	Integer getPreferredNumberOfBars();
	
	void setPreferredNumberOfBars(Integer number);

}
