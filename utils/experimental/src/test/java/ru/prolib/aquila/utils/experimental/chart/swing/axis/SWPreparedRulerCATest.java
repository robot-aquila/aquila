package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.utils.experimental.chart.Point2D;
import ru.prolib.aquila.utils.experimental.chart.Rectangle;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRendererID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWPreparedRulerCA;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWRendererCallbackCA;

public class SWPreparedRulerCATest {
	private IMocksControl control;
	private SWRendererCallbackCA rendererMock1, rendererMock2;
	private CategoryAxisDisplayMapper mapperMock1, mapperMock2;
	private Font fontMock1, fontMock2;
	private List<RLabel> labels1, labels2;
	private Graphics2D graphicsMock;
	private RulerID rulerID;
	private RulerSetup rulerSetup;
	private SWPreparedRulerCA ruler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rendererMock1 = control.createMock(SWRendererCallbackCA.class);
		rendererMock2 = control.createMock(SWRendererCallbackCA.class);
		mapperMock1 = control.createMock(CategoryAxisDisplayMapper.class);
		mapperMock2 = control.createMock(CategoryAxisDisplayMapper.class);
		fontMock1 = control.createMock(Font.class);
		fontMock2 = control.createMock(Font.class);
		graphicsMock = control.createMock(Graphics2D.class);
		labels1 = new ArrayList<>();
		labels1.add(new RLabel(1, ":05", 10));
		labels1.add(new RLabel(2, ":10", 20));
		labels1.add(new RLabel(3, ":15", 30));
		labels2 = new ArrayList<>();
		labels2.add(new RLabel(0, "12h", 15));
		labels2.add(new RLabel(1, "13h", 30));
		rulerID = new RulerID("foo", "bar", true);
		rulerSetup = new RulerSetup(rulerID);
		ruler = new SWPreparedRulerCA(rendererMock1,
									mapperMock1,
									labels1,
									fontMock1);
	}
	
	@Test
	public void testDrawRuler() {
		Rectangle target = new Rectangle(Point2D.ZERO, 40, 50);
		rendererMock1.drawRuler(rulerSetup,
								target,
								graphicsMock,
								mapperMock1,
								labels1,
								fontMock1);
		control.replay();
		
		ruler.drawRuler(rulerSetup, target, graphicsMock);
		
		control.verify();
	}
	
	@Test
	public void testDrawGridLines() {
		GridLinesSetup setup = new GridLinesSetup(new RulerRendererID("foo", "bar"));
		Rectangle plot = new Rectangle(new Point2D(40, 0), 100, 80);
		rendererMock1.drawGridLines(setup, plot, graphicsMock, mapperMock1, labels1);
		control.replay();
		
		ruler.drawGridLines(setup, plot, graphicsMock);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(ruler.equals(ruler));
		assertFalse(ruler.equals(null));
		assertFalse(ruler.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<SWRendererCallbackCA> vCb = new Variant<>(rendererMock1, rendererMock2);
		Variant<CategoryAxisDisplayMapper> vMap = new Variant<>(vCb, mapperMock1, mapperMock2);
		Variant<List<RLabel>> vLbl = new Variant<>(vMap, labels1, labels2);
		Variant<Font> vFnt = new Variant<>(vLbl, fontMock1, fontMock2);
		Variant<?> iterator = vFnt;
		int foundCnt = 0;
		SWPreparedRulerCA x, found = null;
		do {
			x = new SWPreparedRulerCA(vCb.get(), vMap.get(), vLbl.get(), vFnt.get());
			if ( ruler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(rendererMock1, found.getRenderer());
		assertEquals(mapperMock1, found.getDisplayMapper());
		assertEquals(labels1, found.getLabels());
		assertEquals(fontMock1, found.getLabelFont());
	}

}
