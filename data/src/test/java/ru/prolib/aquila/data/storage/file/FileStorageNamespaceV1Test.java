package ru.prolib.aquila.data.storage.file;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DatedSymbol;

public class FileStorageNamespaceV1Test {
	private static String FS = File.separator;
	private static File root = new File("fixture/storage"), fakeRoot = new File("fixture/dummy");
	private static Symbol symbol1 = new Symbol("RTS-9.16"), symbol2 = new Symbol("MSFT");
	private static LocalDate date1 = LocalDate.of(1997, 1, 1), date2 = LocalDate.of(2016, 12, 31);
	private FileStorageNamespace namespace, fakeNamespace;
	
	@Before
	public void setUp() throws Exception {
		FileUtils.forceMkdir(root);
		namespace = new FileStorageNamespaceV1(root);
		fakeNamespace = new FileStorageNamespaceV1(fakeRoot);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(root);
	}
	
	@Test
	public void testGetDirectory() {
		File expected = new File("fixture" + FS + "storage" + FS + "2C" +
				FS + "RTS%2D9%2E16" + FS + "1997" + FS + "01");
		
		assertEquals(expected, namespace.getDirectory(new DatedSymbol(symbol1, date1)));
	}
	
	@Test
	public void testGetDirectoryForWriting() throws Exception {
		File expected = new File("fixture" + FS + "storage" + FS + "B0" +
				FS + "MSFT" + FS + "2016" + FS + "12");
		
		File actual = namespace.getDirectoryForWriting(new DatedSymbol(symbol2, date2));
		
		assertEquals(expected, actual);
		assertTrue(actual.exists());
		assertTrue(actual.isDirectory());
	}
	
	@Test (expected=IOException.class)
	public void testGetDirectoryForWriting_ThrowsBadRoot() throws Exception {
		fakeNamespace.getDirectoryForWriting(new DatedSymbol(symbol1, date1));
	}

	@Test
	public void testScanForSymbols() throws Exception {
		new File(root, "zulu").mkdirs(); // must be skipped by L1 filter
		new File(root, "charlie").mkdirs(); // must be skipped by L1 filter
		new File(root, "15/RG1EU%2D11%2E16/2016").mkdirs();
		new File(root, "15/W4EXU%2D9%2E16/2016").mkdirs();
		new File(root, "84/MTSI%2D12%2E16/2009").mkdirs();
		new File(root, "84/A@B@C").mkdirs(); // must be skipped by L2 filter
		new File(root, "C6/RVI%2D8%2E16/2000").mkdirs();
		
		Set<Symbol> actual = namespace.scanForSymbols();
		
		Set<Symbol> expected = new HashSet<>();
		expected.add(new Symbol("RG1EU-11.16"));
		expected.add(new Symbol("W4EXU-9.16"));
		expected.add(new Symbol("MTSI-12.16"));
		expected.add(new Symbol("RVI-8.16"));
		assertEquals(expected, actual);
	}

}
