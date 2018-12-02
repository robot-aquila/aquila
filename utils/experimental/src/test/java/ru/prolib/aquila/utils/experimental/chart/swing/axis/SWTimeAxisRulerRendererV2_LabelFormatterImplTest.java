package ru.prolib.aquila.utils.experimental.chart.swing.axis;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.swing.axis.SWTimeAxisRulerRendererV2.LabelFormatterImpl;

public class SWTimeAxisRulerRendererV2_LabelFormatterImplTest {
	private static Locale LOCALE = Locale.US;
	private static ZoneId MSK = ZoneId.of("Europe/Moscow");

	static Instant MT(String timeString) {
		return LocalDateTime.parse(timeString).atZone(MSK).toInstant();
	}
	
	private LabelFormatterImpl service;
	
	@Before
	public void setUp() throws Exception {
		service = new LabelFormatterImpl(MSK, LOCALE);
	}

	@Test
	public void testGetLabelText() {
		assertEquals("2018", service.getLabelText(0, MT("2018-12-02T22:26:13.113")));
		assertEquals("2018", service.getLabelText(1, MT("2018-12-02T22:26:13.113")));
		assertEquals("2018", service.getLabelText(2, MT("2018-12-02T22:26:13.113")));
		assertEquals("2018", service.getLabelText(3, MT("2018-12-02T22:26:13.113")));
		
		assertEquals("Dec", service.getLabelText(4, MT("2018-12-02T22:26:13.113")));
		assertEquals("May", service.getLabelText(4, MT("2018-05-02T22:26:13.113")));
		
		assertEquals("02", service.getLabelText(5, MT("2018-12-02T22:26:13.113")));
		assertEquals("30", service.getLabelText(6, MT("2018-12-30T22:26:13.113")));
		
		assertEquals("22h", service.getLabelText( 7, MT("2018-12-02T22:26:13.113")));
		assertEquals( "3h", service.getLabelText( 7, MT("2018-12-02T03:26:13.113")));
		assertEquals("10h", service.getLabelText( 8, MT("2018-12-02T10:26:13.113")));
		assertEquals("11h", service.getLabelText( 9, MT("2018-12-02T11:26:13.113")));
		assertEquals("12h", service.getLabelText(10, MT("2018-12-02T12:26:13.113")));
		
		assertEquals(":26", service.getLabelText(11, MT("2018-12-02T22:26:13.113")));
		assertEquals(":10", service.getLabelText(12, MT("2018-12-02T22:10:13.113")));
		assertEquals(":59", service.getLabelText(13, MT("2018-12-02T22:59:13.113")));
		assertEquals(":15", service.getLabelText(14, MT("2018-12-02T22:15:13.113")));
		assertEquals(":11", service.getLabelText(15, MT("2018-12-02T22:11:13.113")));
		
		assertEquals("13\"", service.getLabelText(16, MT("2018-12-02T22:26:13.113")));
		
		assertEquals(".113", service.getLabelText(17, MT("2018-12-02T22:26:13.113")));
	}

}
