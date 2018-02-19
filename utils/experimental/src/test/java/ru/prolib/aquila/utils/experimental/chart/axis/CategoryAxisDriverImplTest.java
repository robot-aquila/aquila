package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class CategoryAxisDriverImplTest {
	private IMocksControl control;
	private RulerRendererRegistry rulerRenderersMock;
	private RulerRenderer rendererMock1, rendererMock2;
	private CategoryAxisDriverImpl driver;
	private CategoryAxisViewport viewport;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rulerRenderersMock = control.createMock(RulerRendererRegistry.class);
		rendererMock1 = control.createMock(RulerRenderer.class);
		rendererMock2 = control.createMock(RulerRenderer.class);
		driver = new CategoryAxisDriverImpl("TIME", AxisDirection.RIGHT, rulerRenderersMock);
		viewport = new CategoryAxisViewportImpl();
		viewport.setCategoryRangeByFirstAndNumber(4, 3);
	}
	
	@Test
	public void testCtor() {
		assertEquals("TIME", driver.getID());
		assertEquals(AxisDirection.RIGHT, driver.getAxisDirection());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_WhenDirectionIsUp() {
		new CategoryAxisDriverImpl("TIME", AxisDirection.UP);
	}
	
	@Test
	public void testRegisterRenderer() {
		rulerRenderersMock.registerRenderer(rendererMock1);
		control.replay();
		
		driver.registerRenderer(rendererMock1);
		
		control.verify();
	}
	
	@Test
	public void testGetRenderer() {
		expect(rulerRenderersMock.getRenderer("foo")).andReturn(rendererMock1);
		expect(rulerRenderersMock.getRenderer("bar")).andReturn(rendererMock2);
		control.replay();
		
		assertSame(rendererMock1, driver.getRenderer("foo"));
		assertSame(rendererMock2, driver.getRenderer("bar"));
		
		control.verify();
	}
	
	@Test
	public void testGetRendererIDs() {
		List<String> result = new ArrayList<>();
		result.add("foo");
		result.add("bar");
		result.add("buz");
		expect(rulerRenderersMock.getRendererIDs()).andReturn(result);
		control.replay();
		
		List<String> actual = driver.getRendererIDs();
		
		control.verify();
		List<String> expected = new ArrayList<>();
		expected.add("foo");
		expected.add("bar");
		expected.add("buz");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateMapper_Right_NumOfCategoriesLtNumOfBars() {
		viewport.setCategoryRangeByFirstAndNumber(0, 5);
		viewport.setPreferredNumberOfBars(10);

		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				-5, 10, CDecimalBD.of("2.00000"));
		assertEquals(expected, actual);
		assertEquals(4, expected.getLastVisibleCategory());
	}
	
	@Test
	public void testCreateMapper_Right_WhenPlotAreaHasAnOffset() {
		viewport.setCategoryRangeByFirstAndNumber(0, 5);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(10, 12), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(10,
				0, 5, CDecimalBD.of("2.40000"));
		assertEquals(expected, actual);
	}

	@Test
	public void testCreateMapper_Right_NumOfCategoriesEqNumOfBars() {
		viewport.setCategoryRangeByFirstAndNumber(9, 10);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				9, 10, CDecimalBD.of("2.00000"));
		assertEquals(expected, actual);
		assertEquals(18, expected.getLastVisibleCategory());
	}
	
	@Test
	public void testCreateMapper_Right_NumOfCategoriesGtNumOfBars() {
		// The case wants to show up to 30 bars but has space only for 20 bars
		viewport.setCategoryRangeByFirstAndNumber(50, 30);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);

		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				60, 20, CDecimalBD.of("1.00000"));
		assertEquals(expected, actual);
		assertEquals(79, expected.getLastVisibleCategory());
	}

	@Test
	public void testCreateMapper_Right_PreferredNumberOfBarsHasMorePriorityThanNumOfCategories() {
		viewport.setCategoryRangeByFirstAndNumber(50, 20);
		viewport.setPreferredNumberOfBars(7);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				63, 7, CDecimalBD.of("2.85714"));
		assertEquals(expected, actual);
		assertEquals(69, expected.getLastVisibleCategory());
	}
}
