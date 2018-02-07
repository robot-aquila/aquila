package ru.prolib.aquila.utils.experimental.chart;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;

public class BCDisplayContextImplTest {
	private IMocksControl control;
	private CategoryAxisDisplayMapper categoryAxisMapperMock;
	private ValueAxisDisplayMapper valueAxisMapperMock;
	private ChartLayout layoutMock;
	private BCDisplayContextImpl context;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		categoryAxisMapperMock = control.createMock(CategoryAxisDisplayMapper.class);
		valueAxisMapperMock = control.createMock(ValueAxisDisplayMapper.class);
		layoutMock = control.createMock(ChartLayout.class);
		context = new BCDisplayContextImpl(categoryAxisMapperMock,
				valueAxisMapperMock, layoutMock);
	}

	@Test
	public void testGetters() {
		assertSame(categoryAxisMapperMock, context.getCategoryAxisMapper());
		assertSame(valueAxisMapperMock, context.getValueAxisMapper());
		assertSame(layoutMock, context.getChartLayout());
	}

}
