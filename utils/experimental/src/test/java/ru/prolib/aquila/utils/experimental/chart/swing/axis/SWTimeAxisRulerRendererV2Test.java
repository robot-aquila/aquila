package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapper;
import ru.prolib.aquila.utils.experimental.chart.axis.CategoryAxisDisplayMapperHR;
import ru.prolib.aquila.utils.experimental.chart.axis.GridLinesSetup;
import ru.prolib.aquila.utils.experimental.chart.axis.PreparedRuler;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerID;
import ru.prolib.aquila.utils.experimental.chart.axis.RulerRendererID;
import ru.prolib.aquila.utils.experimental.chart.axis.utils.RLabel;

public class SWTimeAxisRulerRendererV2Test {
	private static ZoneId MSK = ZoneId.of("Europe/Moscow");
	
	static Instant atMSK(String timeString) {
		return LocalDateTime.parse(timeString).atZone(MSK).toInstant();
	}
	
	static class FontMetricsStub extends FontMetrics {
		private static final long serialVersionUID = 1L;

		public FontMetricsStub() {
			super(null);
		}
		
		@Override
		public int stringWidth(String text) {
			if ( text.startsWith(":") ) {
				return 23;
			} else if ( text.endsWith("h") ) {
				return 26;
			} else {
				throw new IllegalArgumentException("Unidentified text: " + text);
			}
		}
		
		@Override
		public int getHeight() {
			return 10;
		}
		
	}


	private IMocksControl control;
	private Font fontMock;
	private Graphics2D graphicsMock;
	private SWTimeAxisRulerRendererV2 service;
	private TSeriesImpl<Instant> categories;
	private FontMetrics fontMetricsStub;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		fontMock = control.createMock(Font.class);
		graphicsMock = control.createMock(Graphics2D.class);
		service = new SWTimeAxisRulerRendererV2("foo", fontMock);
		categories = new TSeriesImpl<>(ZTFrame.M5MSK);
		fontMetricsStub = new FontMetricsStub();
	}
	
	private void addCategories(String... timeString) {
		for ( String x : timeString ) {
			Instant t = Instant.parse(x);
			categories.set(t, t);
		}
	}
	
	@Test
	public void testPrepareRuler_SpecialTestCase() {
		// JustHelpDetermineCoords
		int firstBarX = 89;
		int firstBarCategory = 0;
		int numberOfBars = 140;
		CDecimal barWidth = CDecimalBD.of("5.52857");
		CategoryAxisDisplayMapper mapper =  new CategoryAxisDisplayMapperHR(firstBarX,
				firstBarCategory, numberOfBars, barWidth);
		//for ( int i = 0; i < numberOfBars; i ++ ) {
		//	System.out.println("DBG: ROW=" + i + " LBL-SEG=" + mapper.toDisplay(i));
		//}
		addCategories(
				"2017-01-03T08:50:00Z",
				"2017-01-03T08:55:00Z",
				"2017-01-03T09:00:00Z",
				"2017-01-03T09:05:00Z",
				"2017-01-03T09:10:00Z",
				"2017-01-03T09:15:00Z",
				"2017-01-03T09:20:00Z",
				"2017-01-03T09:25:00Z",
				"2017-01-03T09:30:00Z",
				"2017-01-03T09:35:00Z",
				"2017-01-03T09:40:00Z",
				"2017-01-03T09:45:00Z",
				"2017-01-03T09:50:00Z",
				"2017-01-03T09:55:00Z",
				"2017-01-03T10:00:00Z",
				"2017-01-03T10:05:00Z",
				"2017-01-03T10:10:00Z",
				"2017-01-03T10:15:00Z",
				"2017-01-03T10:20:00Z",
				"2017-01-03T10:25:00Z",
				"2017-01-03T10:30:00Z",
				"2017-01-03T10:35:00Z",
				"2017-01-03T10:40:00Z",
				"2017-01-03T10:45:00Z",
				"2017-01-03T10:50:00Z",
				"2017-01-03T10:55:00Z",
				"2017-01-03T11:05:00Z",
				"2017-01-03T11:10:00Z",
				"2017-01-03T11:15:00Z",
				"2017-01-03T11:20:00Z",
				"2017-01-03T11:25:00Z",
				"2017-01-03T11:30:00Z",
				"2017-01-03T11:35:00Z",
				"2017-01-03T11:40:00Z",
				"2017-01-03T11:45:00Z",
				"2017-01-03T11:50:00Z",
				"2017-01-03T11:55:00Z",
				"2017-01-03T12:00:00Z",
				"2017-01-03T12:05:00Z",
				"2017-01-03T12:10:00Z",
				"2017-01-03T12:15:00Z",
				"2017-01-03T12:20:00Z",
				"2017-01-03T12:25:00Z",
				"2017-01-03T12:30:00Z",
				"2017-01-03T12:35:00Z",
				"2017-01-03T12:40:00Z",
				"2017-01-03T12:45:00Z",
				"2017-01-03T12:50:00Z",
				"2017-01-03T12:55:00Z",
				"2017-01-03T13:00:00Z",
				"2017-01-03T13:05:00Z",
				"2017-01-03T13:10:00Z",
				"2017-01-03T13:15:00Z",
				"2017-01-03T13:20:00Z",
				"2017-01-03T13:25:00Z",
				"2017-01-03T13:30:00Z",
				"2017-01-03T13:35:00Z",
				"2017-01-03T13:40:00Z",
				"2017-01-03T13:45:00Z",
				"2017-01-03T13:50:00Z",
				"2017-01-03T13:55:00Z",
				"2017-01-03T14:00:00Z",
				"2017-01-03T14:05:00Z",
				"2017-01-03T14:10:00Z",
				"2017-01-03T14:15:00Z",
				"2017-01-03T14:20:00Z",
				"2017-01-03T14:25:00Z",
				"2017-01-03T14:30:00Z",
				"2017-01-03T14:35:00Z",
				"2017-01-03T14:40:00Z",
				"2017-01-03T14:45:00Z",
				"2017-01-03T14:50:00Z",
				"2017-01-03T14:55:00Z",
				"2017-01-03T15:00:00Z",
				"2017-01-03T15:05:00Z",
				"2017-01-03T15:10:00Z",
				"2017-01-03T15:15:00Z",
				"2017-01-03T15:20:00Z",
				"2017-01-03T15:25:00Z",
				"2017-01-03T15:30:00Z",
				"2017-01-03T15:35:00Z",
				"2017-01-03T15:40:00Z",
				"2017-01-03T16:00:00Z",
				"2017-01-03T16:05:00Z",
				"2017-01-03T16:10:00Z",
				"2017-01-03T16:15:00Z",
				"2017-01-03T16:20:00Z",
				"2017-01-03T16:25:00Z",
				"2017-01-03T16:30:00Z",
				"2017-01-03T16:35:00Z",
				"2017-01-03T16:40:00Z",
				"2017-01-03T16:45:00Z",
				"2017-01-03T16:50:00Z",
				"2017-01-03T16:55:00Z",
				"2017-01-03T17:00:00Z",
				"2017-01-03T17:05:00Z",
				"2017-01-03T17:10:00Z",
				"2017-01-03T17:15:00Z",
				"2017-01-03T17:20:00Z",
				"2017-01-03T17:25:00Z",
				"2017-01-03T17:30:00Z",
				"2017-01-03T17:35:00Z",
				"2017-01-03T17:40:00Z",
				"2017-01-03T17:45:00Z",
				"2017-01-03T17:50:00Z",
				"2017-01-03T17:55:00Z",
				"2017-01-03T18:00:00Z",
				"2017-01-03T18:05:00Z",
				"2017-01-03T18:10:00Z",
				"2017-01-03T18:15:00Z",
				"2017-01-03T18:20:00Z",
				"2017-01-03T18:25:00Z",
				"2017-01-03T18:30:00Z",
				"2017-01-03T18:35:00Z",
				"2017-01-03T18:40:00Z",
				"2017-01-03T18:45:00Z",
				"2017-01-03T18:50:00Z",
				"2017-01-03T18:55:00Z",
				"2017-01-03T19:00:00Z",
				"2017-01-03T19:05:00Z",
				"2017-01-03T19:10:00Z",
				"2017-01-03T19:15:00Z",
				"2017-01-03T19:20:00Z",
				"2017-01-03T19:25:00Z",
				"2017-01-03T19:30:00Z",
				"2017-01-03T19:35:00Z",
				"2017-01-03T19:40:00Z",
				"2017-01-03T19:45:00Z",
				"2017-01-03T19:50:00Z",
				"2017-01-03T19:55:00Z",
				"2017-01-03T20:00:00Z",
				"2017-01-03T20:05:00Z",
				"2017-01-03T20:10:00Z",
				"2017-01-03T20:15:00Z",
				"2017-01-03T20:20:00Z",
				"2017-01-03T20:25:00Z",
				"2017-01-03T20:30:00Z",
				"2017-01-03T20:35:00Z",
				"2017-01-03T20:40:00Z",
				"2017-01-03T20:45:00Z"
			);
		service.setCategories(categories);
		expect(graphicsMock.getFontMetrics(fontMock)).andStubReturn(fontMetricsStub);
		control.replay();
		
		PreparedRuler actual = service.prepareRuler(mapper, graphicsMock);
		
		control.verify();
		assertNotNull(actual);
		List<RLabel> expectedLabels = new ArrayList<>();
		expectedLabels.add(new RLabel(  2, "12h", 103));
		expectedLabels.add(new RLabel(  8, ":30", 136));
		expectedLabels.add(new RLabel( 14, "13h", 169));
		expectedLabels.add(new RLabel( 20, ":30", 202));
		expectedLabels.add(new RLabel( 26, "14h", 235));
		expectedLabels.add(new RLabel( 37, "15h", 296));
		expectedLabels.add(new RLabel( 43, ":30", 329));
		expectedLabels.add(new RLabel( 49, "16h", 362));
		expectedLabels.add(new RLabel( 55, ":30", 396));
		expectedLabels.add(new RLabel( 61, "17h", 429));
		expectedLabels.add(new RLabel( 67, ":30", 462));
		expectedLabels.add(new RLabel( 73, "18h", 495));
		expectedLabels.add(new RLabel( 82, "19h", 545));
		expectedLabels.add(new RLabel( 88, ":30", 578));
		expectedLabels.add(new RLabel( 94, "20h", 611));
		expectedLabels.add(new RLabel(100, ":30", 644));
		expectedLabels.add(new RLabel(106, "21h", 678));
		expectedLabels.add(new RLabel(112, ":30", 711));
		expectedLabels.add(new RLabel(118, "22h", 744));
		expectedLabels.add(new RLabel(124, ":30", 777));
		expectedLabels.add(new RLabel(130, "23h", 810));
		expectedLabels.add(new RLabel(136, ":30", 843));
		PreparedRuler expected = new SWPreparedRulerCA(
				SWTimeAxisRulerRendererCallback.getInstance(),
				mapper,
				expectedLabels,
				fontMock
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCreateRulerSetup() {
		RulerID rulerID = new RulerID("foo", "bar", true);
		SWTimeAxisRulerSetup expected = new SWTimeAxisRulerSetup(rulerID);
		
		assertEquals(expected, service.createRulerSetup(rulerID));
	}
	
	@Test
	public void testCreateGridLinesSetup() {
		RulerRendererID rendererID = new RulerRendererID("zulu", "charlie");
		GridLinesSetup expected = new GridLinesSetup(rendererID);
		
		assertEquals(expected, service.createGridLinesSetup(rendererID));
	}

}
