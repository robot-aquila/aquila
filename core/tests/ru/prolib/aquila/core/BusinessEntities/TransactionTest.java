package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

public class TransactionTest {
	private static SimpleDateFormat df;
	private static Object request, response;
	private static Date requestTime, responseTime;
	private Transaction trans;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		requestTime = df.parse("2008-12-08 00:00:00.150");
		responseTime = df.parse("2008-12-08 00:00:01.012");
		request = new Object();
		response = new Object();		
	}

	@Before
	public void setUp() throws Exception {
		trans = new Transaction();
	}
	
	@Test
	public void testDefaults() throws Exception {
		assertNull(trans.getLatency());
		assertNull(trans.getRequest());
		assertNull(trans.getRequestTime());
		assertNull(trans.getResponse());
		assertNull(trans.getResponseTime());
	}
	
	@Test
	public void testSetRequest() throws Exception {
		trans.setRequest(request);
		assertSame(request, trans.getRequest());
	}
	
	@Test
	public void testSetRequestTime() throws Exception {
		trans.setRequestTime(requestTime);
		assertEquals(requestTime, trans.getRequestTime());
		trans.setRequestTime();
		assertEquals(new Date(), trans.getRequestTime());
	}
	
	@Test
	public void testSetResponse() throws Exception {
		trans.setResponse(response);
		assertSame(response, trans.getResponse());
	}
	
	@Test
	public void testSetResponseTime() throws Exception {
		trans.setResponseTime(responseTime);
		assertEquals(responseTime, trans.getResponseTime());
		trans.setResponseTime();
		assertEquals(new Date(), trans.getResponseTime());
	}
	
	@Test
	public void testGetLatency() throws Exception {
		assertNull(trans.getLatency());
		trans.setRequestTime(requestTime);
		assertNull(trans.getLatency());
		trans.setResponseTime(responseTime);
		assertEquals(new Long(862), trans.getLatency());
	}
	
	@Test
	public void testIsStarted() throws Exception {
		assertFalse(trans.isStarted());
		trans.setRequest(request);
		assertTrue(trans.isStarted());
	}
	
	@Test
	public void testIsExecuted() throws Exception {
		assertFalse(trans.isExecuted());
		trans.setResponse(response);
		assertTrue(trans.isExecuted());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(trans.equals(trans));
		assertFalse(trans.equals(null));
		assertFalse(trans.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		trans.setRequest(request);
		trans.setRequestTime(requestTime);
		trans.setResponse(response);
		trans.setResponseTime(responseTime);
		Variant<Object> vReq = new Variant<Object>()
			.add(request)
			.add(new Object())
			.add(null);
		Variant<Date> vReqTime = new Variant<Date>(vReq)
			.add(requestTime)
			.add(new Date())
			.add(null);
		Variant<Object> vResp = new Variant<Object>(vReqTime)
			.add(response)
			.add(new Object())
			.add(null);
		Variant<Date> vRespTime = new Variant<Date>(vResp)
			.add(responseTime)
			.add(new Date())
			.add(null);
		Variant<?> iterator = vRespTime;
		int foundCnt = 0;
		Transaction x, found = null;
		do {
			x = new Transaction();
			x.setRequest(vReq.get());
			x.setRequestTime(vReqTime.get());
			x.setResponse(vResp.get());
			x.setResponseTime(vRespTime.get());
			if ( trans.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(request, found.getRequest());
		assertEquals(requestTime, found.getRequestTime());
		assertSame(response, found.getResponse());
		assertEquals(responseTime, found.getResponseTime());
	}

}
