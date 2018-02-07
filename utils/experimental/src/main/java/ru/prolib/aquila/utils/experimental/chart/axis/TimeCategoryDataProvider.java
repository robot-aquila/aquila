package ru.prolib.aquila.utils.experimental.chart.axis;

import java.time.Instant;

import ru.prolib.aquila.core.data.TSeries;

public interface TimeCategoryDataProvider {

	TSeries<Instant> getCategories();
	void setCategories(TSeries<Instant> categories);

}
