package ru.prolib.aquila.transaq.xml;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
	
	@Test
	public void testReadSecurities_UnknownSecType() throws Exception {
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
