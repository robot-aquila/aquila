package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

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
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.ValueAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWPreparedRulerVA;
import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWRendererCallbackVA;

public class SWPreparedRulerVATest {
	private IMocksControl control;
	private SWRendererCallbackVA rendererMock1, rendererMock2;
	private ValueAxisDisplayMapper mapperMock1, mapperMock2;
	private Font fontMock1, fontMock2;
	private Graphics2D graphicsMock;
	private List<RLabel> labels1, labels2;
	private RulerID rulerID;
	private RulerSetup rulerSetup;
	private SWPreparedRulerVA ruler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rendererMock1 = control.createMock(SWRendererCallbackVA.class);
		rendererMock2 = control.createMock(SWRendererCallbackVA.class);
		mapperMock1 = control.createMock(ValueAxisDisplayMapper.class);
		mapperMock2 = control.createMock(ValueAxisDisplayMapper.class);
		fontMock1 = control.createMock(Font.class);
		fontMock2 = control.createMock(Font.class);
		graphicsMock = control.createMock(Graphics2D.class);
		labels1 = new ArrayList<>();
		labels1.add(new RLabel(of("250.31"), "250,31", 10));
		labels1.add(new RLabel(of("495.29"), "495,29", 20));
		labels1.add(new RLabel(of("180.74"), "180,74", 30));
		labels2 = new ArrayList<>();
		labels2.add(new RLabel(of("164.18"), "---,-1", 15));
		labels2.add(new RLabel(of("112.86"), "---,-2", 30));
		rulerID = new RulerID("foo", "bar", false);
		rulerSetup = new RulerSetup(rulerID);
		ruler = new SWPreparedRulerVA(rendererMock1, mapperMock1, labels1, fontMock1);
	}
	
	@Test
	public void testDrawRuler() {
		Rectangle target = new Rectangle(Point2D.ZERO, 40, 50);
		rendererMock1.drawRuler(rulerSetup, target, graphicsMock,mapperMock1, labels1, fontMock1);
		control.replay();
		
		ruler.drawRuler(rulerSetup, target, graphicsMock);
		
		control.verify();
	}
	
	@Test
	public void testDrawGridLines() {
		Rectangle plot = new Rectangle(new Point2D(40, 0), 100, 80);
		rendererMock1.drawGridLines(plot, graphicsMock, mapperMock1, labels1);
		control.replay();
		
		ruler.drawGridLines(plot, graphicsMock);
		
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
		Variant<SWRendererCallbackVA> vCb = new Variant<>(rendererMock1, rendererMock2);
		Variant<ValueAxisDisplayMapper> vMap = new Variant<>(vCb, mapperMock1, mapperMock2);
		Variant<List<RLabel>> vLbl = new Variant<>(vMap, labels1, labels2);
		Variant<Font> vFont = new Variant<>(vLbl, fontMock1, fontMock2);
		Variant<?> iterator = vFont;
		int foundCnt = 0;
		SWPreparedRulerVA x, found = null;
		do {
			x = new SWPreparedRulerVA(vCb.get(), vMap.get(), vLbl.get(), vFont.get());
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
