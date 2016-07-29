package ru.prolib.aquila.data.storage;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

public class DataSegmentInfoTest {
	private DataSegmentInfo info;

	@Before
	public void setUp() throws Exception {
		info = new DataSegmentInfo("foo");
	}

	@Test
	public void testDefaults() {
		assertEquals("foo", info.getSourceID());
		assertNull(info.getStatus());
		assertNull(info.getUpdateTime());
		assertNull(info.getVersion());
		assertNull(info.getRecordCount());
	}
	
	@Test
	public void testSetters() {
		Instant updateTime = Instant.parse("2016-07-29T05:49:15Z");
		info.setStatus(DataSegmentStatus.EMPTY);
		info.setUpdateTime(updateTime);
		info.setVersion("0.1");
		info.setRecordCount(280L);
		
		assertEquals(DataSegmentStatus.EMPTY, info.getStatus());
		assertEquals(updateTime, info.getUpdateTime());
		assertEquals("0.1", info.getVersion());
		assertEquals(new Long(280L), info.getRecordCount());
	}

}
