package ru.prolib.aquila.t2q;


import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.t2q.T2QConnStatus;

/**
 * 2013-01-29<br>
 * $Id: T2QConnStatusTest.java 457 2013-01-29 11:32:17Z whirlwind $
 */
public class T2QConnStatusTest {

	/**
	 * Ряд фикстуры.
	 */
	private static class FR {
		private final String code;
		private final T2QConnStatus status;
		
		public FR(String code, T2QConnStatus status) {
			super();
			this.code = code;
			this.status = status;
		}
	}

	@Test
	public void testConstants() throws Exception {
		FR fix[] = {
				new FR("QUIK_CONN",	T2QConnStatus.QUIK_CONN),
				new FR("QUIK_DISC",	T2QConnStatus.QUIK_DISC),
				new FR("DLL_CONN",	T2QConnStatus.DLL_CONN),
				new FR("DLL_DISC",	T2QConnStatus.DLL_DISC),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			FR row = fix[i];
			assertEquals(row.code, row.status.getCode());
			assertEquals(row.code, row.status.toString());
		}
	}

}
