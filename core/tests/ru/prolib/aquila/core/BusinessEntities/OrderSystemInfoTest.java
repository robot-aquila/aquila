package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class OrderSystemInfoTest {
	private static Object regReq, regResp, killReq, killResp;
	private static Date regReqTime, regRespTime, killReqTime, killRespTime;
	private OrderSystemInfo info;
	
	@BeforeClass
	public static void setUpBreforeClass() throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		regReq = new Object();
		regResp = new Object();
		killReq = new Object();
		killResp = new Object();
		regReqTime = df.parse("2008-12-08 00:00:01.001");
		regRespTime = df.parse("2008-12-08 00:00:01.050");
		killReqTime = df.parse("2008-12-08 10:44:05.002");
		killRespTime = df.parse("2008-12-08 10:44:06.019");
	}

	@Before
	public void setUp() throws Exception {
		info = new OrderSystemInfo();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(info.equals(info));
		assertFalse(info.equals(null));
		assertFalse(info.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Transaction trans = info.getRegisteration();
		synchronized ( trans ) {
			trans.setRequest(regReq);
			trans.setRequestTime(regReqTime);
			trans.setResponse(regResp);
			trans.setResponseTime(regRespTime);
		}
		trans = info.getCancellation();
		synchronized ( trans ) {
			trans.setRequest(killReq);
			trans.setRequestTime(killReqTime);
			trans.setResponse(killResp);
			trans.setResponseTime(killRespTime);
		}
		Variant<Object> vRegReq = new Variant<Object>()
			.add(regReq)
			.add(new Object())
			.add(null);
		Variant<Date> vRegReqTime = new Variant<Date>(vRegReq)
			.add(regReqTime)
			.add(null)
			.add(new Date());
		Variant<Object> vRegResp = new Variant<Object>(vRegReqTime)
			.add(regResp)
			.add(new Object())
			.add(null);
		Variant<Date> vRegRespTime = new Variant<Date>(vRegResp)
			.add(regRespTime)
			.add(new Date())
			.add(null);
		Variant<Object> vKillReq = new Variant<Object>(vRegRespTime)
			.add(killReq)
			.add(new Object())
			.add(null);
		Variant<Date> vKillReqTime = new Variant<Date>(vKillReq)
			.add(killReqTime)
			.add(null)
			.add(new Date());
		Variant<Object> vKillResp = new Variant<Object>(vKillReqTime)
			.add(killResp)
			.add(new Object())
			.add(null);
		Variant<Date> vKillRespTime = new Variant<Date>(vKillResp)
			.add(killRespTime)
			.add(new Date())
			.add(null);
		Variant<?> iterator = vKillRespTime;
		int foundCnt = 0;
		OrderSystemInfo x, found = null;
		do {
			x = new OrderSystemInfo();
			trans = x.getRegisteration();
			trans.setRequest(vRegReq.get());
			trans.setRequestTime(vRegReqTime.get());
			trans.setResponse(vRegResp.get());
			trans.setResponseTime(vRegRespTime.get());
			trans = x.getCancellation();
			trans.setRequest(vKillReq.get());
			trans.setRequestTime(vKillReqTime.get());
			trans.setResponse(vKillResp.get());
			trans.setResponseTime(vKillRespTime.get());
			if ( info.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		trans = new Transaction();
		trans.setRequest(regReq);
		trans.setRequestTime(regReqTime);
		trans.setResponse(regResp);
		trans.setResponseTime(regRespTime);
		assertEquals(trans, found.getRegisteration());
		trans = new Transaction();
		trans.setRequest(killReq);
		trans.setRequestTime(killReqTime);
		trans.setResponse(killResp);
		trans.setResponseTime(killRespTime);
		assertEquals(trans, found.getCancellation());
	}

}
