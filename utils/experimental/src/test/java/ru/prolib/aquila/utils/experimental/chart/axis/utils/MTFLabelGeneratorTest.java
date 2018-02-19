package ru.prolib.aquila.utils.experimental.chart.axis.utils;

import static org.junit.Assert.*;
import static java.time.LocalTime.of;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MTFLabelGeneratorTest {
	
	static class LabelSizeStub implements MTFLabelSize {
		private final int charSize;
		
		public LabelSizeStub(int charSize) {
			this.charSize = charSize;
		}

		@Override
		public int getVisibleSize(String labelText) {
			return labelText.length() * charSize;
		}
		
	}
	
	private MTFLabelGenerator service;
	private LabelSizeStub labelSizeStub;

	@Before
	public void setUp() throws Exception {
		service = MTFLabelGenerator.getInstance();
		labelSizeStub = new LabelSizeStub(5);
	}
	
	protected void dump(List<MTFLabel> labels) {
		for ( MTFLabel label : labels ) {
			System.out.println((label.isHourBoundary() ? "H " : "  ") + label.getText());
		}		
	}
	
	protected void assertEqualsOneByOne(List<MTFLabel> expected, List<MTFLabel> actual) {
		assertEquals("Size mismatch", expected.size(), actual.size());
		for ( int i = 0; i < expected.size(); i ++ ) {
			assertEquals("At#" + i, expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void testGetLargestLabelTemplate() {
		assertEquals("00000", MTFLabelGenerator.getLargestLabelTemplate());
	}

	@Test
	public void testGetIntradayLabels1_M29() {
		List<MTFLabel> actual = service.getIntradayLabels(29);
		
		List<MTFLabel> expected = new ArrayList<>();
		expected.add(new MTFLabel(of( 0,  0), "00:00", true));
		expected.add(new MTFLabel(of( 0, 29),   ":29", false));
		expected.add(new MTFLabel(of( 0, 58),   ":58", false));
		expected.add(new MTFLabel(of( 1, 27), "01:27", true));
		expected.add(new MTFLabel(of( 1, 56),   ":56", false));
		expected.add(new MTFLabel(of( 2, 25), "02:25", true));
		expected.add(new MTFLabel(of( 2, 54),   ":54", false));
		expected.add(new MTFLabel(of( 3, 23), "03:23", true));
		expected.add(new MTFLabel(of( 3, 52),   ":52", false));
		expected.add(new MTFLabel(of( 4, 21), "04:21", true));
		expected.add(new MTFLabel(of( 4, 50),   ":50", false));
		expected.add(new MTFLabel(of( 5, 19), "05:19", true));
		expected.add(new MTFLabel(of( 5, 48),   ":48", false));
		expected.add(new MTFLabel(of( 6, 17), "06:17", true));
		expected.add(new MTFLabel(of( 6, 46),   ":46", false));
		expected.add(new MTFLabel(of( 7, 15), "07:15", true));
		expected.add(new MTFLabel(of( 7, 44),   ":44", false));
		expected.add(new MTFLabel(of( 8, 13), "08:13", true));
		expected.add(new MTFLabel(of( 8, 42),   ":42", false));
		expected.add(new MTFLabel(of( 9, 11), "09:11", true));
		expected.add(new MTFLabel(of( 9, 40),   ":40", false));
		expected.add(new MTFLabel(of(10,  9), "10:09", true));
		expected.add(new MTFLabel(of(10, 38),   ":38", false));
		expected.add(new MTFLabel(of(11,  7), "11:07", true));
		expected.add(new MTFLabel(of(11, 36),   ":36", false));
		expected.add(new MTFLabel(of(12,  5), "12:05", true));
		expected.add(new MTFLabel(of(12, 34),   ":34", false));
		expected.add(new MTFLabel(of(13,  3), "13:03", true));
		expected.add(new MTFLabel(of(13, 32),   ":32", false));
		expected.add(new MTFLabel(of(14,  1), "14:01", true));
		expected.add(new MTFLabel(of(14, 30),   ":30", false));
		expected.add(new MTFLabel(of(14, 59),   ":59", false));
		expected.add(new MTFLabel(of(15, 28), "15:28", true));
		expected.add(new MTFLabel(of(15, 57),   ":57", false));
		expected.add(new MTFLabel(of(16, 26), "16:26", true));
		expected.add(new MTFLabel(of(16, 55),   ":55", false));
		expected.add(new MTFLabel(of(17, 24), "17:24", true));
		expected.add(new MTFLabel(of(17, 53),   ":53", false));
		expected.add(new MTFLabel(of(18, 22), "18:22", true));
		expected.add(new MTFLabel(of(18, 51),   ":51", false));
		expected.add(new MTFLabel(of(19, 20), "19:20", true));
		expected.add(new MTFLabel(of(19, 49),   ":49", false));
		expected.add(new MTFLabel(of(20, 18), "20:18", true));
		expected.add(new MTFLabel(of(20, 47),   ":47", false));
		expected.add(new MTFLabel(of(21, 16), "21:16", true));
		expected.add(new MTFLabel(of(21, 45),   ":45", false));
		expected.add(new MTFLabel(of(22, 14), "22:14", true));
		expected.add(new MTFLabel(of(22, 43),   ":43", false));
		expected.add(new MTFLabel(of(23, 12), "23:12", true));
		expected.add(new MTFLabel(of(23, 41),   ":41", false));
		assertEqualsOneByOne(expected, actual);
	}
	
	@Test
	public void testGetIntradayLabels1_M1() {
		List<MTFLabel> actual = service.getIntradayLabels(1);
		
		List<MTFLabel> expected = new ArrayList<>();
		for ( int i = 0; i < 1440; i ++ ) {
			LocalTime time = LocalTime.MIDNIGHT.plusMinutes(i);
			if ( time.getMinute() == 0 ) {
				expected.add(new MTFLabel(time,
						time.getHour() + "h",
						true));
			} else {
				expected.add(new MTFLabel(time,
						String.format(":%02d", time.getMinute()),
						false));
			}
		}
		assertEqualsOneByOne(expected, actual);
	}
	
	@Test
	public void testGetIntradayLabels1_M15() {
		List<MTFLabel> actual = service.getIntradayLabels(15);
		
		List<MTFLabel> expected = new ArrayList<>();
		for ( int h = 0; h < 24; h ++ ) {
			expected.add(new MTFLabel(of( h,  0), h + "h", true));
			expected.add(new MTFLabel(of( h, 15), ":15", false));
			expected.add(new MTFLabel(of( h, 30), ":30", false));
			expected.add(new MTFLabel(of( h, 45), ":45", false));
		}
		assertEqualsOneByOne(expected, actual);
	}
	
	@Test
	public void GetIntradayLabels3_M30_BS10() {
		List<MTFLabel> actual = service.getIntradayLabels(10, 30, labelSizeStub);
		
		List<MTFLabel> expected = new ArrayList<>();
		expected.add(new MTFLabel(of( 0,  0),  "0h", true));
		expected.add(new MTFLabel(of( 0, 30), ":30", false));
		expected.add(new MTFLabel(of( 1,  0),  "1h", true));
		expected.add(new MTFLabel(of( 1, 30), ":30", false));
		expected.add(new MTFLabel(of( 2,  0),  "2h", true));
		expected.add(new MTFLabel(of( 2, 30), ":30", false));
		expected.add(new MTFLabel(of( 3,  0),  "3h", true));
		expected.add(new MTFLabel(of( 3, 30), ":30", false));
		expected.add(new MTFLabel(of( 4,  0),  "4h", true));
		expected.add(new MTFLabel(of( 4, 30), ":30", false));
		expected.add(new MTFLabel(of( 5,  0),  "5h", true));
		expected.add(new MTFLabel(of( 5, 30), ":30", false));
		expected.add(new MTFLabel(of( 6,  0),  "6h", true));
		expected.add(new MTFLabel(of( 6, 30), ":30", false));
		expected.add(new MTFLabel(of( 7,  0),  "7h", true));
		expected.add(new MTFLabel(of( 7, 30), ":30", false));
		expected.add(new MTFLabel(of( 8,  0),  "8h", true));
		expected.add(new MTFLabel(of( 8, 30), ":30", false));
		expected.add(new MTFLabel(of( 9,  0),  "9h", true));
		expected.add(new MTFLabel(of( 9, 30), ":30", false));
		expected.add(new MTFLabel(of(10,  0), "10h", true));
		expected.add(new MTFLabel(of(11,  0), "11h", true));
		expected.add(new MTFLabel(of(12,  0), "12h", true));
		expected.add(new MTFLabel(of(13,  0), "13h", true));
		expected.add(new MTFLabel(of(14,  0), "14h", true));
		expected.add(new MTFLabel(of(15,  0), "15h", true));
		expected.add(new MTFLabel(of(16,  0), "16h", true));
		expected.add(new MTFLabel(of(17,  0), "17h", true));
		expected.add(new MTFLabel(of(18,  0), "18h", true));
		expected.add(new MTFLabel(of(19,  0), "19h", true));
		expected.add(new MTFLabel(of(20,  0), "20h", true));
		expected.add(new MTFLabel(of(21,  0), "21h", true));
		expected.add(new MTFLabel(of(22,  0), "22h", true));
		expected.add(new MTFLabel(of(23,  0), "23h", true));
		assertEqualsOneByOne(expected, actual);
	}
	
	@Test
	public void GetIntradayLabels3_M30_BS20() {
		List<MTFLabel> actual = service.getIntradayLabels(20, 30, labelSizeStub);

		List<MTFLabel> expected = new ArrayList<>();
		expected.add(new MTFLabel(of( 0,  0),  "0h", true));
		expected.add(new MTFLabel(of( 0, 30), ":30", false));
		expected.add(new MTFLabel(of( 1,  0),  "1h", true));
		expected.add(new MTFLabel(of( 1, 30), ":30", false));
		expected.add(new MTFLabel(of( 2,  0),  "2h", true));
		expected.add(new MTFLabel(of( 2, 30), ":30", false));
		expected.add(new MTFLabel(of( 3,  0),  "3h", true));
		expected.add(new MTFLabel(of( 3, 30), ":30", false));
		expected.add(new MTFLabel(of( 4,  0),  "4h", true));
		expected.add(new MTFLabel(of( 4, 30), ":30", false));
		expected.add(new MTFLabel(of( 5,  0),  "5h", true));
		expected.add(new MTFLabel(of( 5, 30), ":30", false));
		expected.add(new MTFLabel(of( 6,  0),  "6h", true));
		expected.add(new MTFLabel(of( 6, 30), ":30", false));
		expected.add(new MTFLabel(of( 7,  0),  "7h", true));
		expected.add(new MTFLabel(of( 7, 30), ":30", false));
		expected.add(new MTFLabel(of( 8,  0),  "8h", true));
		expected.add(new MTFLabel(of( 8, 30), ":30", false));
		expected.add(new MTFLabel(of( 9,  0),  "9h", true));
		expected.add(new MTFLabel(of( 9, 30), ":30", false));
		expected.add(new MTFLabel(of(10,  0), "10h", true));
		expected.add(new MTFLabel(of(10, 30), ":30", false));
		expected.add(new MTFLabel(of(11,  0), "11h", true));
		expected.add(new MTFLabel(of(11, 30), ":30", false));
		expected.add(new MTFLabel(of(12,  0), "12h", true));
		expected.add(new MTFLabel(of(12, 30), ":30", false));
		expected.add(new MTFLabel(of(13,  0), "13h", true));
		expected.add(new MTFLabel(of(13, 30), ":30", false));
		expected.add(new MTFLabel(of(14,  0), "14h", true));
		expected.add(new MTFLabel(of(14, 30), ":30", false));
		expected.add(new MTFLabel(of(15,  0), "15h", true));
		expected.add(new MTFLabel(of(15, 30), ":30", false));
		expected.add(new MTFLabel(of(16,  0), "16h", true));
		expected.add(new MTFLabel(of(16, 30), ":30", false));
		expected.add(new MTFLabel(of(17,  0), "17h", true));
		expected.add(new MTFLabel(of(17, 30), ":30", false));
		expected.add(new MTFLabel(of(18,  0), "18h", true));
		expected.add(new MTFLabel(of(18, 30), ":30", false));
		expected.add(new MTFLabel(of(19,  0), "19h", true));
		expected.add(new MTFLabel(of(19, 30), ":30", false));
		expected.add(new MTFLabel(of(20,  0), "20h", true));
		expected.add(new MTFLabel(of(20, 30), ":30", false));
		expected.add(new MTFLabel(of(21,  0), "21h", true));
		expected.add(new MTFLabel(of(21, 30), ":30", false));
		expected.add(new MTFLabel(of(22,  0), "22h", true));
		expected.add(new MTFLabel(of(22, 30), ":30", false));
		expected.add(new MTFLabel(of(23,  0), "23h", true));
		expected.add(new MTFLabel(of(23, 30), ":30", false));
		assertEqualsOneByOne(expected, actual);
	}
	
	@Test
	public void GetIntradayLabels3_M30_BS3() {
		List<MTFLabel> actual = service.getIntradayLabels(3, 30, labelSizeStub);
		
		List<MTFLabel> expected = new ArrayList<>();
		expected.add(new MTFLabel(of( 0, 0),  "0h", true));
		expected.add(new MTFLabel(of( 2, 0),  "2h", true));
		expected.add(new MTFLabel(of( 4, 0),  "4h", true));
		expected.add(new MTFLabel(of( 6, 0),  "6h", true));
		expected.add(new MTFLabel(of( 8, 0),  "8h", true));
		expected.add(new MTFLabel(of(10, 0), "10h", true));
		expected.add(new MTFLabel(of(13, 0), "13h", true));
		expected.add(new MTFLabel(of(16, 0), "16h", true));
		expected.add(new MTFLabel(of(19, 0), "19h", true));
		expected.add(new MTFLabel(of(22, 0), "22h", true));
		assertEqualsOneByOne(expected, actual);
	}
	
}
