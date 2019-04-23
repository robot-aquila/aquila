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
	public void testCtor4() {
		driver = new CategoryAxisDriverImpl("TIME", AxisDirection.RIGHT, rulerRenderersMock, true);
		
		assertEquals("TIME", driver.getID());
		assertEquals(AxisDirection.RIGHT, driver.getAxisDirection());
		assertTrue(driver.isBarWidthLtOneAllowed());
	}
	
	@Test
	public void testCtor3_RRR() {
		assertEquals("TIME", driver.getID());
		assertEquals(AxisDirection.RIGHT, driver.getAxisDirection());
		assertFalse(driver.isBarWidthLtOneAllowed());
	}
	
	@Test
	public void testCtor3_BW() {
		driver = new CategoryAxisDriverImpl("TIME", AxisDirection.RIGHT, true);
		
		assertEquals("TIME", driver.getID());
		assertEquals(AxisDirection.RIGHT, driver.getAxisDirection());
		assertTrue(driver.isBarWidthLtOneAllowed());
	}
	
	@Test
	public void testCtor2() {
		driver = new CategoryAxisDriverImpl("TIME", AxisDirection.RIGHT);
		
		assertEquals("TIME", driver.getID());
		assertEquals(AxisDirection.RIGHT, driver.getAxisDirection());
		assertFalse(driver.isBarWidthLtOneAllowed());		
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor4_WhenDirectionIsUp() {
		driver = new CategoryAxisDriverImpl("TIME", AxisDirection.UP, rulerRenderersMock, true);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor2_WhenDirectionIsUp() {
		new CategoryAxisDriverImpl("TIME", AxisDirection.UP);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor3_RRR_WhenDirectionIsUp() {
		new CategoryAxisDriverImpl("TIME", AxisDirection.UP, rulerRenderersMock);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testCtor3_BW_WhenDirectionIsUp() {
		new CategoryAxisDriverImpl("TIME", AxisDirection.UP, true);
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
	public void testCreateMapper_Right_NumOfCategoriesGtNumOfPixels_BWlt1() {
		// The case wants to show up to 30 bars but has space only for 20 bars
		// BUT bar width < 1 allowed
		driver = new CategoryAxisDriverImpl("TIME", AxisDirection.RIGHT, rulerRenderersMock, true);
		viewport.setCategoryRangeByFirstAndNumber(50, 30);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 20), viewport);
		
		CategoryAxisDisplayMapper expected = new CategoryAxisDisplayMapperHR(0,
				50, 30, CDecimalBD.of("0.66667"), true);
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
	
	@Test
	public void testCreateMapper_Right_SpecialCase1() {
		// Если рассчитывать вьюпорт по последней категории и указывать количество видимых элементов,
		// равное количеству элементов последовательности, при наличии настройки предпочитаемого
		// количества видимых баров, график уезжает влево, а справа остается пустота, когда
		// количество элементов последовательности превышает предпочитаемое количество видимых
		// баров. Проверим, что причина не здесь.
		viewport.setCategoryRangeByLastAndNumber(54, 55);
		viewport.setPreferredNumberOfBars(50);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(77, 615), viewport);
		
		assertEquals( 5, actual.getFirstVisibleCategory());
		assertEquals(54, actual.getLastVisibleCategory());
		assertEquals(50, actual.getNumberOfVisibleCategories());
		assertEquals(50, actual.getNumberOfVisibleBars());
		assertEquals(new Segment1D( 77, 12), actual.toDisplay( 5));
		assertEquals(new Segment1D(680, 12), actual.toDisplay(54));
	}
	
	@Test
	public void testCreateMapper_Right_ZeroBarsBugfix() {
		viewport.setCategoryRangeByFirstAndNumber(0, 0);
		viewport.setPreferredNumberOfBars(null);
		
		CategoryAxisDisplayMapper actual = driver.createMapper(new Segment1D(15, 200), viewport);
		
		assertEquals( 0, actual.getFirstVisibleCategory());
		assertEquals(-1, actual.getLastVisibleCategory());
		assertEquals( 0, actual.getNumberOfVisibleCategories());
		assertEquals( 0, actual.getNumberOfVisibleBars());
	}
	
}
