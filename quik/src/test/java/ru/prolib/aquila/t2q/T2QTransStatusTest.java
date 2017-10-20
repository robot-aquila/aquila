package ru.prolib.aquila.t2q;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.t2q.T2QTransStatus;

/**
 * 2013-01-29<br>
 * $Id: T2QTransStatusTest.java 457 2013-01-29 11:32:17Z whirlwind $
 */
public class T2QTransStatusTest {
	
	/**
	 * Ряд фикстуры.
	 */
	private static class FR {
		private final String code;
		private final T2QTransStatus status;
		
		private FR(String code, T2QTransStatus status) {
			super();
			this.code = code;
			this.status = status;
		}
		
	}
	
	@Test
	public void testConstant() throws Exception {
		FR fix[] = {
				new FR("SENT", T2QTransStatus.SENT),
				new FR("RECV", T2QTransStatus.RECV),
				new FR("DONE", T2QTransStatus.DONE),
				new FR("ERR_REJ", T2QTransStatus.ERR_REJ),
				new FR("ERR_NOK", T2QTransStatus.ERR_NOK),
				new FR("ERR_CON", T2QTransStatus.ERR_CON),
				new FR("ERR_TSYS", T2QTransStatus.ERR_TSYS),
				new FR("ERR_LIMIT", T2QTransStatus.ERR_LIMIT),
				new FR("ERR_UNSUPPORTED", T2QTransStatus.ERR_UNSUPPORTED),
				new FR("ERR_AUTH", T2QTransStatus.ERR_AUTH),
				new FR("ERR_TIMEOUT", T2QTransStatus.ERR_TIMEOUT),
				new FR("ERR_CROSS", T2QTransStatus.ERR_CROSS),
				new FR("ERR_UNK", T2QTransStatus.ERR_UNK),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			FR row = fix[i];
			assertEquals(row.code, row.status.getCode());
			assertEquals(row.code, row.status.toString());
		}
	}

}
