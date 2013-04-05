package ru.prolib.aquila.ta.ds;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DataSetIteratorEmptyTest {
	DataSetIteratorEmpty iterator;

	@Before
	public void setUp() throws Exception {
		iterator = new DataSetIteratorEmpty();
	}
	
	@Test (expected=DataSetException.class)
	public void testGetDouble_AlwaysThrows() throws Exception {
		iterator.getDouble("foobar");
	}

	@Test (expected=DataSetException.class)
	public void testGetString_AlwaysThrows() throws Exception {
		iterator.getString("foobar");
	}
	
	@Test (expected=DataSetException.class)
	public void testGetDate_AlwaysThrows() throws Exception {
		iterator.getDate("foobar");
	}

	@Test (expected=DataSetException.class)
	public void testGetLong_AlwaysThrows() throws Exception {
		iterator.getLong("foobar");
	}
	
	@Test
	public void testNext_AlwaysFalse() throws Exception {
		assertFalse(iterator.next());
	}
	
	@Test
	public void testClose_Ok() throws Exception {
		iterator.close();
	}

}
