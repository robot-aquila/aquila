package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;

public class TimeCategoryDataProviderImplTest {
	private TSeries<Instant> series;
	private TimeCategoryDataProviderImpl dataProvider;

	@Before
	public void setUp() throws Exception {
		series = new TSeriesImpl<>(ZTFrame.M1);
		dataProvider = new TimeCategoryDataProviderImpl();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetCategories_ThrowIfNotDefined() {
		dataProvider.getCategories();
	}
	
	@Test
	public void testCtor1() {
		dataProvider = new TimeCategoryDataProviderImpl(series);
		assertSame(series, dataProvider.getCategories());
	}
	
	@Test
	public void testSetCategories() {
		dataProvider.setCategories(series);
		assertSame(series, dataProvider.getCategories());
	}

}
