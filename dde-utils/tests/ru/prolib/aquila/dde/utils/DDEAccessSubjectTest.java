package ru.prolib.aquila.dde.utils;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

public class DDEAccessSubjectTest {
	private DDEAccessSubject subj;

	@Before
	public void setUp() throws Exception {
		subj = new DDEAccessSubject("srv", "table");
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals("srv", subj.getService());
		assertEquals("table", subj.getTopic());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfServiceIsNull() throws Exception {
		new DDEAccessSubject(null, "table");
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfTopicIsNull() throws Exception {
		new DDEAccessSubject("srv", null);
	}
	
	@Test
	public void testEquals() throws Exception {
		Object[][] fix = {
			// subj, expected result
			{ new DDEAccessSubject("srv", "table"), true  },
			{ new DDEAccessSubject("foo", "table"), false },
			{ new DDEAccessSubject("srv", "bable"), false },
			{ null,									false },
			{ this,									false },
			{ subj,									true  },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			assertEquals(msg, (Boolean)fix[i][1], subj.equals(fix[i][0]));
		}
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/85909)
			.append("srv")
			.append("table")
			.toHashCode();
		assertEquals(hashCode, subj.hashCode());
	}

}
