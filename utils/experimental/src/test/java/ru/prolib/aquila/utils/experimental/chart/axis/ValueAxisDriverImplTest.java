package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Range;
import ru.prolib.aquila.utils.experimental.chart.Segment1D;

public class ValueAxisDriverImplTest {
	private IMocksControl control;
	private RulerRendererRegistry rulerRenderersMock;
	private RulerRenderer rendererMock1, rendererMock2;
	private ValueAxisDriverImpl driver;
	private ValueAxisViewport viewport;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rulerRenderersMock = control.createMock(RulerRendererRegistry.class);
		rendererMock1 = control.createMock(RulerRenderer.class);
		rendererMock2 = control.createMock(RulerRenderer.class);
		driver = new ValueAxisDriverImpl("VALUE", AxisDirection.UP, rulerRenderersMock);
		viewport = new ValueAxisViewportImpl();
		viewport.setValueRange(new Range<>(of(1000L), of(2000L)));
	}
	
	@Test
	public void testCtor() {
		assertEquals("VALUE", driver.getID());
		assertEquals(AxisDirection.UP, driver.getAxisDirection());
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testCtor_WhenDirectionIsRight() {
		new ValueAxisDriverImpl("VALUE", AxisDirection.RIGHT);
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
	public void testCreateMapper_Up_ValueAreaGtDisplay() {
		ValueAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 100), viewport);

		ValueAxisDisplayMapper expected = new ValueAxisDisplayMapperVUV(0, 100,
				new Range<>(of(1000L), of(2000L)));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdaterImpl_Up_DisplayGtValueArea() {
		viewport.setValueRange(new Range<>(of(10L), of(20L)));

		ValueAxisDisplayMapper actual = driver.createMapper(new Segment1D(10, 80), viewport);
		
		ValueAxisDisplayMapper expected = new ValueAxisDisplayMapperVUD(10, 80, new Range<>(of(10L), of(20L)));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateMapper_BugTest1_WhenDisplayHeightEqToRange() {
		viewport.setValueRange(new Range<>(of(64615L), of(64916L)));
		
		ValueAxisDisplayMapper actual = driver.createMapper(new Segment1D(0, 301), viewport);
		
		ValueAxisDisplayMapper expected = new ValueAxisDisplayMapperVUV(0, 301, new Range<>(of(64615L), of(64916L)));
		assertEquals(expected, actual);
	}
	
}
