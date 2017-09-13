package ru.prolib.aquila.data.storage.segstor.file;

import static org.junit.Assert.*;

import java.io.File;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.segstor.SymbolAnnual;
import ru.prolib.aquila.data.storage.segstor.SymbolDaily;
import ru.prolib.aquila.data.storage.segstor.SymbolMonthly;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo;
import ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfoImpl;
import ru.prolib.aquila.data.storage.segstor.file.V1SegmentFileManagerImpl;

public class V1SegmentFileManagerImplTest {
	private static final File root = new File("fixture/temp");
	private static final Symbol symbol1 = new Symbol("RG1EU-11.16"),
			symbol2 = new Symbol("W4EXU-9.16"),
			symbol3 = new Symbol("MTSI-12.16"),
			symbol4 = new Symbol("RVI-8.16");
	
	private V1SegmentFileManagerImpl service;

	@Before
	public void setUp() throws Exception {
		FileUtils.forceMkdir(root);
		service = new V1SegmentFileManagerImpl(root);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test
	public void testCtor1() {
		assertEquals(root, service.getRoot());
	}
	
	@Test
	public void testScanForSymbolDirectories() {
		new File(root, "zulu").mkdirs(); // must be skipped by L1 filter
		new File(root, "charlie").mkdirs(); // must be skipped by L1 filter
		new File(root, "15/RG1EU%2D11%2E16/2016").mkdirs();
		new File(root, "15/W4EXU%2D9%2E16/2016").mkdirs();
		new File(root, "84/MTSI%2D12%2E16/2009").mkdirs();
		new File(root, "84/A@B@C").mkdirs(); // must be skipped by L2 filter
		new File(root, "C6/RVI%2D8%2E16/2000").mkdirs();

		Set<Symbol> actual = service.scanForSymbolDirectories();
		
		Set<Symbol> expected = new HashSet<>();
		expected.add(symbol1);
		expected.add(symbol2);
		expected.add(symbol3);
		expected.add(symbol4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetDirectory_1SM() {
		assertEquals(new File(root, "15/RG1EU%2D11%2E16/2016/04"),
				service.getDirectory(new SymbolMonthly(symbol1, 2016, Month.APRIL)));
		assertEquals(new File(root, "84/MTSI%2D12%2E16/2009/01"),
				service.getDirectory(new SymbolMonthly(symbol3, 2009, Month.JANUARY)));
	}
	
	@Test
	public void testGetDirectory_1SA() {
		assertEquals(new File(root, "15/RG1EU%2D11%2E16/2016"),
				service.getDirectory(new SymbolAnnual(symbol1, 2016)));
		assertEquals(new File(root, "84/MTSI%2D12%2E16/2009"),
				service.getDirectory(new SymbolAnnual(symbol3, 2009)));		
	}
	
	@Test
	public void testGetDirectory_1S() {
		assertEquals(new File(root, "15/RG1EU%2D11%2E16"), service.getDirectory(symbol1));
		assertEquals(new File(root, "84/MTSI%2D12%2E16"), service.getDirectory(symbol3));		
	}
	
	@Test
	public void testScanForYearDirectories() {
		Symbol symbol = new Symbol("MSFT");
		service = new V1SegmentFileManagerImpl(new File("fixture"));
		
		List<SymbolAnnual> actual = service.scanForYearDirectories(symbol);
		
		List<SymbolAnnual> expected = new ArrayList<>();
		expected.add(new SymbolAnnual(symbol, 2005));
		expected.add(new SymbolAnnual(symbol, 2006));
		expected.add(new SymbolAnnual(symbol, 2008));
		expected.add(new SymbolAnnual(symbol, 2009));
		expected.add(new SymbolAnnual(symbol, 2012));
		expected.add(new SymbolAnnual(symbol, 2013));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScanForMonthDirectories() {
		Symbol symbol = new Symbol("MSFT");
		service = new V1SegmentFileManagerImpl(new File("fixture"));

		List<SymbolMonthly> actual = service.scanForMonthDirectories(new SymbolAnnual(symbol, 2006));
		
		List<SymbolMonthly> expected = new ArrayList<>();
		expected.add(new SymbolMonthly(symbol, 2006, Month.FEBRUARY));
		expected.add(new SymbolMonthly(symbol, 2006, Month.APRIL));
		expected.add(new SymbolMonthly(symbol, 2006, Month.MAY));
		expected.add(new SymbolMonthly(symbol, 2006, Month.JUNE));
		expected.add(new SymbolMonthly(symbol, 2006, Month.JULY));
		expected.add(new SymbolMonthly(symbol, 2006, Month.AUGUST));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetFileInfo_Symbol() {
		SegmentFileInfo actual = service.getFileInfo(symbol1, "-segment.dat");
		
		SegmentFileInfo expected = new SegmentFileInfoImpl()
			.setFullPath(new File(root, "15/RG1EU%2D11%2E16"), "RG1EU%2D11%2E16", "-segment.dat");
		assertEquals(expected, actual);
		assertEquals(new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-segment.dat"),
				actual.getFullPath());
	}
	
	@Test
	public void testGetFileInfo_Daily() {
		SegmentFileInfo actual = service.getFileInfo(new SymbolDaily(symbol1, 2010, 11, 1),  "-data.bin");
		
		SegmentFileInfo expected = new SegmentFileInfoImpl()
			.setFullPath(new File(root, "15/RG1EU%2D11%2E16/2010/11"),
					"RG1EU%2D11%2E16-20101101", "-data.bin");
		assertEquals(expected, actual);
		assertEquals(new File(root, "15/RG1EU%2D11%2E16/2010/11/RG1EU%2D11%2E16-20101101-data.bin"),
				actual.getFullPath());
	}
	
	@Test
	public void testGetFileInfo_Monthly() {
		SegmentFileInfo actual = service.getFileInfo(new SymbolMonthly(symbol1, 1992, 1), "-foo.bar");
		
		SegmentFileInfo expected = new SegmentFileInfoImpl()
			.setFullPath(new File(root, "15/RG1EU%2D11%2E16/1992"),
					"RG1EU%2D11%2E16-199201", "-foo.bar");
		assertEquals(expected, actual);
		assertEquals(new File(root, "15/RG1EU%2D11%2E16/1992/RG1EU%2D11%2E16-199201-foo.bar"),
				actual.getFullPath());
	}
	
	@Test
	public void testGetFileInfo_Annual() {
		SegmentFileInfo actual = service.getFileInfo(new SymbolAnnual(symbol1, 1997), "-alf.dat");
		
		SegmentFileInfo expected = new SegmentFileInfoImpl()
			.setFullPath(new File(root, "15/RG1EU%2D11%2E16"), "RG1EU%2D11%2E16-1997", "-alf.dat");
		assertEquals(expected, actual);
		assertEquals(new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-1997-alf.dat"),
				actual.getFullPath());
	}
	
	@Test
	public void testHasSymbolSegment() throws Exception {
		new File(root, "84/MTSI%2D12%2E16").mkdirs();
		new File(root, "84/MTSI%2D12%2E16/2016").mkdirs();
		new File(root, "84/MTSI%2D12%2E16/MTSI%2D12%2E16-symbol.dat").createNewFile();
		new File(root, "84/MTSI%2D12%2E16/MTSI%2D12%2E16-my-file.txt").createNewFile();
		new File(root, "15/W4EXU%2D9%2E16").mkdirs();
		new File(root, "15/W4EXU%2D9%2E16/2001").mkdirs();
		new File(root, "15/W4EXU%2D9%2E16/W4EXU%2D9%2E16-my-file.txt").createNewFile();

		assertFalse(service.hasSymbolSegment(symbol1, "-symbol.dat"));
		assertTrue(service.hasSymbolSegment(symbol3, "-symbol.dat"));
		assertTrue(service.hasSymbolSegment(symbol3, "-my-file.txt"));
		assertFalse(service.hasSymbolSegment(symbol3, "-foo.dat"));
		assertTrue(service.hasSymbolSegment(symbol2, "-my-file.txt"));
		assertFalse(service.hasSymbolSegment(symbol2, "-symbol.dat"));
		assertFalse(service.hasSymbolSegment(symbol2, "-foo.dat"));
	}
	
	@Test
	public void testScanForSymbolSegments() throws Exception {
		new File(root, "84/MTSI%2D12%2E16").mkdirs();
		new File(root, "84/MTSI%2D12%2E16/2016").mkdirs();
		new File(root, "84/MTSI%2D12%2E16/MTSI%2D12%2E16-symbol.dat").createNewFile();
		new File(root, "84/MTSI%2D12%2E16/MTSI%2D12%2E16-my-file.txt").createNewFile();
		new File(root, "15/W4EXU%2D9%2E16").mkdirs();
		new File(root, "15/W4EXU%2D9%2E16/2001").mkdirs();
		new File(root, "15/W4EXU%2D9%2E16/W4EXU%2D9%2E16-my-file.txt").createNewFile();
		List<Symbol> actual, expected = new ArrayList<>();
		
		actual = service.scanForSymbolSegments("-my-file.txt");
		
		expected.add(symbol3); // MTSI
		expected.add(symbol2); // W4EXU
		assertEquals(expected, actual);
		
		actual = service.scanForSymbolSegments("-symbol.dat");
		
		expected.clear();
		expected.add(symbol3);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScanForAnnualSegments() throws Exception {
		new File(root, "15/RG1EU%2D11%2E16").mkdirs(); // symbol1
		new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-2001-best.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-2001-test.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-2002-best.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-2006-best.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-2006-test.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-2007-test.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/RG1EU%2D11%2E16-2009-test.dat").createNewFile();
		List<SymbolAnnual> actual, expected = new ArrayList<>();
		
		actual = service.scanForAnnualSegments(symbol1, "-best.dat");
		
		expected.clear();
		expected.add(new SymbolAnnual(symbol1, 2001));
		expected.add(new SymbolAnnual(symbol1, 2002));
		expected.add(new SymbolAnnual(symbol1, 2006));
		assertEquals(expected, actual);
		
		actual = service.scanForAnnualSegments(symbol1, "-test.dat");
		
		expected.clear();
		expected.add(new SymbolAnnual(symbol1, 2001));
		expected.add(new SymbolAnnual(symbol1, 2006));
		expected.add(new SymbolAnnual(symbol1, 2007));
		expected.add(new SymbolAnnual(symbol1, 2009));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScanForMonthlySegments() throws Exception {
		new File(root, "15/RG1EU%2D11%2E16/2010").mkdirs(); // symbol1
		new File(root, "15/RG1EU%2D11%2E16/2010/RG1EU%2D11%2E16-201001-best.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/RG1EU%2D11%2E16-201001-test.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/RG1EU%2D11%2E16-201002-best.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/RG1EU%2D11%2E16-201006-best.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/RG1EU%2D11%2E16-201006-test.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/RG1EU%2D11%2E16-201007-test.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/RG1EU%2D11%2E16-201009-test.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/RG1EU%2D11%2E16-201012-best.dat").createNewFile();
		List<SymbolMonthly> actual, expected = new ArrayList<>();
		
		actual = service.scanForMonthlySegments(new SymbolAnnual(symbol1, 2010), "-best.dat");
		
		expected.clear();
		expected.add(new SymbolMonthly(symbol1, 2010,  1));
		expected.add(new SymbolMonthly(symbol1, 2010,  2));
		expected.add(new SymbolMonthly(symbol1, 2010,  6));
		expected.add(new SymbolMonthly(symbol1, 2010, 12));
		assertEquals(expected, actual);
		
		actual = service.scanForMonthlySegments(new SymbolAnnual(symbol1, 2010), "-test.dat");
		
		expected.clear();
		expected.add(new SymbolMonthly(symbol1, 2010,  1));
		expected.add(new SymbolMonthly(symbol1, 2010,  6));
		expected.add(new SymbolMonthly(symbol1, 2010,  7));
		expected.add(new SymbolMonthly(symbol1, 2010,  9));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScanForDailySegments() throws Exception {
		new File(root, "15/RG1EU%2D11%2E16/2010/06").mkdirs(); // symbol1
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-201006-1-my.dat").createNewFile(); // !
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100601-my.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100601-my.xxx").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100602-my.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100603-my.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100604-my.xxx").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100607-my.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100607-my.xxx").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100611-my.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100611-my.xxx").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100629-my.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100630-my.dat").createNewFile();
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-20100632-my.dat").createNewFile(); // !
		new File(root, "15/RG1EU%2D11%2E16/2010/06/RG1EU%2D11%2E16-201006XX-my.dat").createNewFile(); // !
		List<SymbolDaily> actual, expected = new ArrayList<>();
		
		actual = service.scanForDailySegments(new SymbolMonthly(symbol1, 2010, 6), "-my.dat");
		
		expected.clear();
		expected.add(new SymbolDaily(symbol1, 2010, 6, 1));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 2));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 3));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 7));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 11));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 29));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 30));
		assertEquals(expected, actual);
		
		actual = service.scanForDailySegments(new SymbolMonthly(symbol1, 2010, 6), "-my.xxx");
		
		expected.clear();
		expected.add(new SymbolDaily(symbol1, 2010, 6, 1));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 4));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 7));
		expected.add(new SymbolDaily(symbol1, 2010, 6, 11));
		assertEquals(expected, actual);
	}

}
