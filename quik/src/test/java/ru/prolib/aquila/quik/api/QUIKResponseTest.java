package ru.prolib.aquila.quik.api;

import static org.junit.Assert.*;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.t2q.T2QTransStatus;

public class QUIKResponseTest {
	private QUIKResponse response;

	@Before
	public void setUp() throws Exception {
		response = new QUIKResponse(T2QTransStatus.DONE, 80, 200L, "test");
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(response.equals(response));
		assertFalse(response.equals(null));
		assertFalse(response.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<T2QTransStatus> vSt = new Variant<T2QTransStatus>()
			.add(T2QTransStatus.DONE)
			.add(T2QTransStatus.ERR_AUTH);
		Variant<Integer> vId = new Variant<Integer>(vSt)
			.add(80)
			.add(91);
		Variant<Long> vOrd = new Variant<Long>(vId)
			.add(200L)
			.add(null);
		Variant<String> vMsg = new Variant<String>(vOrd)
			.add("test")
			.add("guest");
		Variant<?> iterator = vMsg;
		int foundCnt = 0;
		QUIKResponse x, found = null;
		do {
			x = new QUIKResponse(vSt.get(), vId.get(), vOrd.get(), vMsg.get());
			if ( response.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(T2QTransStatus.DONE, found.getStatus());
		assertEquals(80, found.getId());
		assertEquals(new Long(200L), found.getOrderId());
		assertEquals("test", found.getMessage());
	}
	
	static class FR {
		final T2QTransStatus status;
		final boolean expected;
		FR(T2QTransStatus status, boolean expected) {
			this.status = status;
			this.expected = expected;
		}
	}
	
	@Test
	public void testIsError() throws Exception {
		FR fix[] = {
				new FR(T2QTransStatus.DONE, false),
				new FR(T2QTransStatus.ERR_AUTH, true),
				new FR(T2QTransStatus.ERR_CON, true),
				new FR(T2QTransStatus.ERR_CROSS, true),
				new FR(T2QTransStatus.ERR_LIMIT, true),
				new FR(T2QTransStatus.ERR_NOK, true),
				new FR(T2QTransStatus.ERR_REJ, true),
				new FR(T2QTransStatus.ERR_TIMEOUT, true),
				new FR(T2QTransStatus.ERR_TSYS, true),
				new FR(T2QTransStatus.ERR_UNK, true),
				new FR(T2QTransStatus.ERR_UNSUPPORTED, true),
				new FR(T2QTransStatus.RECV, false),
				new FR(T2QTransStatus.SENT, false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At#" + i;
			response = new QUIKResponse(fix[i].status, 0, 0L, "");
			assertEquals(msg, fix[i].expected, response.isError());
		}
	}
	
	@Test
	public void testIsSuccess() throws Exception {
		FR fix[] = {
				new FR(T2QTransStatus.DONE, true),
				new FR(T2QTransStatus.ERR_AUTH, false),
				new FR(T2QTransStatus.ERR_CON, false),
				new FR(T2QTransStatus.ERR_CROSS, false),
				new FR(T2QTransStatus.ERR_LIMIT, false),
				new FR(T2QTransStatus.ERR_NOK, false),
				new FR(T2QTransStatus.ERR_REJ, false),
				new FR(T2QTransStatus.ERR_TIMEOUT, false),
				new FR(T2QTransStatus.ERR_TSYS, false),
				new FR(T2QTransStatus.ERR_UNK, false),
				new FR(T2QTransStatus.ERR_UNSUPPORTED, false),
				new FR(T2QTransStatus.RECV, false),
				new FR(T2QTransStatus.SENT, false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At#" + i;
			response = new QUIKResponse(fix[i].status, 0, 0L, "");
			assertEquals(msg, fix[i].expected, response.isSuccess());
		}
	}
	
	@Test
	public void testIsFinal() throws Exception {
		FR fix[] = {
				new FR(T2QTransStatus.DONE, true),
				new FR(T2QTransStatus.ERR_AUTH, true),
				new FR(T2QTransStatus.ERR_CON, true),
				new FR(T2QTransStatus.ERR_CROSS, true),
				new FR(T2QTransStatus.ERR_LIMIT, true),
				new FR(T2QTransStatus.ERR_NOK, true),
				new FR(T2QTransStatus.ERR_REJ, true),
				new FR(T2QTransStatus.ERR_TIMEOUT, true),
				new FR(T2QTransStatus.ERR_TSYS, true),
				new FR(T2QTransStatus.ERR_UNK, true),
				new FR(T2QTransStatus.ERR_UNSUPPORTED, true),
				new FR(T2QTransStatus.RECV, false),
				new FR(T2QTransStatus.SENT, false),
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At#" + i;
			response = new QUIKResponse(fix[i].status, 0, 0L, "");
			assertEquals(msg, fix[i].expected, response.isFinal());
		}
	}

}
