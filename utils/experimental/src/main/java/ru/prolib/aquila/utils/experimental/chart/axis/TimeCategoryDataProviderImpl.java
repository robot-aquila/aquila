package ru.prolib.aquila.utils.experimental.chart.axis;

import java.time.Instant;

import ru.prolib.aquila.core.data.TSeries;

public class TimeCategoryDataProviderImpl implements TimeCategoryDataProvider {
	private TSeries<Instant> categories;

	public TimeCategoryDataProviderImpl(TSeries<Instant> categories) {
		this.categories = categories;
	}

	public TimeCategoryDataProviderImpl() {
		
	}
	
	@Override
	public TSeries<Instant> getCategories() {
		if ( categories == null ) {
			throw new IllegalStateException("Categories were not defined");
		}
		return categories;
	}

	@Override
	public void setCategories(TSeries<Instant> categories) {
		this.categories = categories;
	}

}
