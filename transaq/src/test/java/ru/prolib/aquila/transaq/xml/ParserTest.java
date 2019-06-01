package ru.prolib.aquila.transaq.xml;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.SecField;
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.entity.SecurityUpdate1;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;

public class ParserTest {
	private static XMLInputFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		factory = XMLInputFactory.newInstance();
	}
	
	private Parser service;

	@Before
	public void setUp() throws Exception {
		service = new Parser();
	}
	
	@Test
	public void testReadDate() throws Exception {
		String xml = new StringBuilder()
				.append("<foo>")
					.append("<date1>01.06.2019 07:46:15.882</date1>")
					.append("<date2>31.12.1978 14:20:12</date2>")
					.append("<date3>11:37:15</date3>")
				.append("</foo>")
				.toString();
		InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		LocalDateTime actual1 = null, actual2 = null, actual3 = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( sr.getLocalName() ) {
				case "date1":
					actual1 = service.readDate(sr);
					break;
				case "date2":
					actual2 = service.readDate(sr);
					break;
				case "date3":
					actual3 = service.readDate(sr);
					break;
				}
				break;
			}
		}
		assertEquals(LocalDateTime.of(2019,  6,  1,  7, 46, 15, 882000000), actual1);
		assertEquals(LocalDateTime.of(1978, 12, 31, 14, 20, 12), actual2);
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 37, 15)), actual3);
	}
	
	@Test
	public void testReadMarkets() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/markets.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<Market> actual = null;
		while ( sr.hasNext() ) {
        	switch ( sr.next() ) {
        	case XMLStreamReader.START_DOCUMENT:
        	case XMLStreamReader.START_ELEMENT:
        		if ( "markets".equals(sr.getLocalName()) ) {
        			actual = service.readMarkets(sr);
        		}
        		break;
        	}
		}
		List<Market> expected = new ArrayList<>();
		expected.add(new Market( 0, "Collateral"));
		expected.add(new Market( 1, "MICEX"));
		expected.add(new Market( 4, "FORTS"));
		expected.add(new Market( 7, "SPBEX"));
		expected.add(new Market( 8, "INF"));
		expected.add(new Market( 9, "9 [N/A]"));
		expected.add(new Market(12, "12 [N/A]"));
		expected.add(new Market(14, "MMA"));
		expected.add(new Market(15, "ETS"));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadMarkets_ThrowsUnexpectedTag() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/markets2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readMarkets(sr);
				break;
			}
		}
	}

	@Test
	public void testReadBoards() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/boards.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<Board> actual = null;
		while ( sr.hasNext() ) {
        	switch ( sr.next() ) {
        	case XMLStreamReader.START_DOCUMENT:
        	case XMLStreamReader.START_ELEMENT:
        		if ( "boards".equals(sr.getLocalName()) ) {
        			actual = service.readBoards(sr);
        		}
        		break;
        	}
		}
		List<Board> expected = new ArrayList<>();
		expected.add(new Board("AUCT", "Auction", 1, 2));
		expected.add(new Board("EQDB", "Main market: D bonds", 1, 2));
		expected.add(new Board("EQDP", "Dark Pool", 1, 2));
		expected.add(new Board("CNGD", "ETS Neg. deals", 15, 2));
		expected.add(new Board("INDEXE", "ETS indexes", 15, 2));
		expected.add(new Board("ZLG", "Залоговые инструменты", 0, 2));
		expected.add(new Board("EQNL", "EQNL [N/A]", 255, 2));
		expected.add(new Board("AETS", "Дополнительная сессия", 15, 2));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadBoards_ThrowsUnexpectedTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/boards2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
        	switch ( sr.next() ) {
        	case XMLStreamReader.START_ELEMENT:
        		service.readBoards(sr);
        		break;
        	}
		}
	}
	
	@Test
	public void testReadCandleKinds() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/candlekinds.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<CandleKind> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "candlekinds".equals(sr.getLocalName()) ) {
					actual = service.readCandleKinds(sr);
				}
				break;
			}
		}
		List<CandleKind> expected = new ArrayList<>();
		expected.add(new CandleKind(1, 60, "1 minute"));
		expected.add(new CandleKind(2, 300, "5 minutes"));
		expected.add(new CandleKind(3, 900, "15 minutes"));
		expected.add(new CandleKind(4, 3600, "1 hour"));
		expected.add(new CandleKind(5, 86400, "1 day"));
		expected.add(new CandleKind(6, 604800, "1 week"));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadCandleKinds_ThrowsUnexpectedTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/candlekinds2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readCandleKinds(sr);
				break;
			}
		}
	}
	
	@Test
	public void testReadSecurities() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<DeltaUpdate> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "securities".equals(sr.getLocalName()) ) {
					actual = service.readSecurities(sr);
				}
				break;
			}
		}
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
				.withToken(SecField.SECID, 0)
				.withToken(SecField.ACTIVE, true)
				.withToken(SecField.SECCODE, "IRGZ")
				.withToken(SecField.SECCLASS, "E")
				.withToken(SecField.DEFAULT_BOARDCODE, "TQBR")
				.withToken(SecField.MARKETID, 1)
				.withToken(SecField.SHORT_NAME, "IrkutskEnrg")
				.withToken(SecField.DECIMALS, 2)
				.withToken(SecField.MINSTEP, of("0.02"))
				.withToken(SecField.LOTSIZE, of("100"))
				.withToken(SecField.POINT_COST, of("1"))
				.withToken(SecField.OPMASK, 0x01 | 0x02 | 0x04 | 0x08 | 0x10)
				.withToken(SecField.SECTYPE, SecType.SHARE)
				.withToken(SecField.SECTZ, "Russian Standard Time")
				.withToken(SecField.QUOTESTYPE, 1)
				.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
				.withToken(SecField.SECID, 3)
				.withToken(SecField.ACTIVE, true)
				.withToken(SecField.SECCODE, "RU000A0ZZ505")
				.withToken(SecField.SECCLASS, "B")
				.withToken(SecField.DEFAULT_BOARDCODE, "EQOB")
				.withToken(SecField.MARKETID, 1)
				.withToken(SecField.SHORT_NAME, "Russian Agricultural Bank 09T1")
				.withToken(SecField.DECIMALS, 2)
				.withToken(SecField.MINSTEP, of("0.01"))
				.withToken(SecField.LOTSIZE, of("1"))
				.withToken(SecField.POINT_COST, of("10"))
				.withToken(SecField.OPMASK, 0x01 | 0x04 | 0x08 | 0x10)
				.withToken(SecField.SECTYPE, SecType.BOND)
				.withToken(SecField.SECTZ, "Russian Standard Time")
				.withToken(SecField.QUOTESTYPE, 1)
				.buildUpdate());
		expected.add(new DeltaUpdateBuilder()
				.withToken(SecField.SECID, 41190)
				.withToken(SecField.ACTIVE, true)
				.withToken(SecField.SECCODE, "RIM9")
				.withToken(SecField.SECCLASS, "F")
				.withToken(SecField.DEFAULT_BOARDCODE, "FUT")
				.withToken(SecField.MARKETID, 4)
				.withToken(SecField.SHORT_NAME, "RTS-6.19")
				.withToken(SecField.DECIMALS, 0)
				.withToken(SecField.MINSTEP, of("10"))
				.withToken(SecField.LOTSIZE, of("1"))
				.withToken(SecField.POINT_COST, of("129.073"))
				.withToken(SecField.OPMASK, 0x02 | 0x10)
				.withToken(SecField.SECTYPE, SecType.FUT)
				.withToken(SecField.SECTZ, "Russian Standard Time")
				.withToken(SecField.QUOTESTYPE, 1)
				.buildUpdate());
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadSecurities_ThrowsUnexpectedTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities3.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readSecurities(sr);
				break;
			}
		}
	}
	
	@Test
	public void testReadSecurities_UnknownSecTypeAndTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities1.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<DeltaUpdate> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "securities".equals(sr.getLocalName()) ) {
					actual = service.readSecurities(sr);
				}
				break;
			}
		}
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
				.withToken(SecField.SECID, 0)
				.withToken(SecField.ACTIVE, true)
				.withToken(SecField.SECCODE, "IRGZ")
				.withToken(SecField.SECCLASS, "E")
				.withToken(SecField.DEFAULT_BOARDCODE, "TQBR")
				.withToken(SecField.MARKETID, 1)
				.withToken(SecField.SHORT_NAME, "IrkutskEnrg")
				.withToken(SecField.DECIMALS, 2)
				.withToken(SecField.MINSTEP, of("0.02"))
				.withToken(SecField.LOTSIZE, of("100"))
				.withToken(SecField.POINT_COST, of("1"))
				.withToken(SecField.OPMASK, 0x01 | 0x02 | 0x04 | 0x08 | 0x10)
				.withToken(SecField.SECTYPE, SecType.QUOTES)
				.withToken(SecField.SECTZ, "Russian Standard Time")
				.withToken(SecField.QUOTESTYPE, 1)
				.buildUpdate());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadSecurities_Inactive() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<DeltaUpdate> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "securities".equals(sr.getLocalName()) ) {
					actual = service.readSecurities(sr);
				}
				break;
			}
		}
		List<DeltaUpdate> expected = new ArrayList<>();
		expected.add(new DeltaUpdateBuilder()
				.withToken(SecField.SECID, 0)
				.withToken(SecField.ACTIVE, true)
				.withToken(SecField.SECCODE, "IRGZ")
				.withToken(SecField.SECCLASS, "E")
				.withToken(SecField.DEFAULT_BOARDCODE, "TQBR")
				.withToken(SecField.MARKETID, 1)
				.withToken(SecField.SHORT_NAME, "IrkutskEnrg")
				.withToken(SecField.DECIMALS, 2)
				.withToken(SecField.POINT_COST, of("1"))
				.withToken(SecField.SECTYPE, SecType.SHARE)
				.withToken(SecField.SECTZ, "Russian Standard Time")
				.withToken(SecField.QUOTESTYPE, 1)
				.buildUpdate());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadSecInfo() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		DeltaUpdate actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "sec_info".equals(sr.getLocalName()) ) {
					actual = service.readSecInfo(sr);
				}
				break;
			}
		}
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecField.SECID, 28334)
				.withToken(SecField.SECNAME, "FOOBAR")
				.withToken(SecField.SECCODE, "FOO-12.35")
				.withToken(SecField.MARKETID, 4)
				.withToken(SecField.PNAME, "pcs.")
				.withToken(SecField.MAT_DATE, LocalDateTime.of(2019, 6, 1, 6, 26, 15))
				.withToken(SecField.CLEARING_PRICE, of("203.082"))
				.withToken(SecField.MINPRICE, of("100.000"))
				.withToken(SecField.MAXPRICE, of("300.000"))
				.withToken(SecField.BUY_DEPOSIT, of("278991.92"))
				.withToken(SecField.SELL_DEPOSIT, of("728001.10"))
				.withToken(SecField.BGO_C, of("79.03"))
				.withToken(SecField.BGO_NC, of("86.12"))
				.withToken(SecField.ACCRUED_INT, of("0.02"))
				.withToken(SecField.COUPON_VALUE, of("192.77"))
				.withToken(SecField.COUPON_DATE, LocalDateTime.of(2019, 12, 31, 0, 0, 0))
				.withToken(SecField.COUPON_PERIOD, 12)
				.withToken(SecField.FACE_VALUE, of("1000.00"))
				.withToken(SecField.PUT_CALL, "P")
				.withToken(SecField.OPT_TYPE, "M")
				.withToken(SecField.LOT_VOLUME, 1)
				.buildUpdate();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadSecInfo_ThrowsUnexpectedTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readSecInfo(sr);
				break;
			}
		}
	}
	
	@Test
	public void testReadSecInfoUpd() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info_upd.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		DeltaUpdate actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "sec_info_upd".equals(sr.getLocalName()) ) {
					actual = service.readSecInfoUpd(sr);
				}
				break;
			}
		}
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecField.SECID, 66)
				.withToken(SecField.SECCODE, "BRH0")
				.withToken(SecField.MARKETID, 4)
				.withToken(SecField.BUY_DEPOSIT, of("6132.61"))
				.withToken(SecField.SELL_DEPOSIT, of("6467.30"))
				.withToken(SecField.MINPRICE, of("61.97"))
				.withToken(SecField.MAXPRICE, of("67.19"))
				.withToken(SecField.POINT_COST, of("7625.71"))
				.withToken(SecField.BGO_C, of("811.44"))
				.withToken(SecField.BGO_NC, of("4640.88"))
				.withToken(SecField.BGO_BUY, of("4605.53"))
				.buildUpdate();
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadSecInfoUpd_ThrowsUnexpectedTag() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info_upd2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readSecInfoUpd(sr);
				break;
			}
		}
	}
	
	@Ignore
	@Test
	public void _convert1() throws Exception {
		List<String> lines = FileUtils.readLines(new File("data-sample3.txt"));
		List<String> result = new ArrayList<>();
		String start_marker = "XHandler IN> ";
		for ( String line : lines ) {
			if ( line.startsWith(start_marker) ) {
				line = line.substring(start_marker.length());
				result.add(line);
			}
		}
		FileUtils.writeLines(new File("data-sample_.txt"), result);
	}
	
	@Ignore
	@Test
	public void _convert2() throws Exception {
		int count_sections = 0, count_securities = 0, line_num = 0;
		List<String> lines = FileUtils.readLines(new File("data-sample_.txt"));
		for ( String line : lines ) {
			ByteArrayInputStream is = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));
			try {
				XMLStreamReader sr = factory.createXMLStreamReader(is);
				while ( sr.hasNext() ) {
					switch ( sr.next() ) {
					case XMLStreamReader.START_DOCUMENT:
					case XMLStreamReader.START_ELEMENT:
						if ( "securities".equals(sr.getLocalName()) ) {
							count_sections ++;
							List<DeltaUpdate> list = service.readSecurities(sr);
							count_securities += list.size();
							for  ( DeltaUpdate s : list ) {
								if ( "RTS-6.19".equals(s.getContents().get(SecField.SHORT_NAME)) ) {
									System.out.println(s);
								}
							}
						}
						break;
					}
				}
				line_num ++;
				sr.close();
			} catch ( Throwable t ) {
				System.err.println("An error occurred: " + t.getMessage());
				System.err.println("While processing line [" + line_num + "]: " + line);
				t.printStackTrace(System.err);
				break;
			}
		}
		System.out.println("count sections: " + count_sections);
		System.out.println("count securities: " + count_securities);
	}
	
	/*
				if ( decimals != null && tick_size != null ) {
					tick_size = tick_size.withScale(decimals);
					if ( tick_val != null ) {
						tick_val = tick_val.multiply(tick_size)
								.multiply(of(10L).pow(decimals))
								.withScale(8)
								.divide(100L)
								.withScale(5)
								.withUnit(CDecimalBD.RUB);						
					}
				} else {
					tick_val = null;
					tick_size = null;
				}

	 */

}
