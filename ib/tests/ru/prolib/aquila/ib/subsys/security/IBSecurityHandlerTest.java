package ru.prolib.aquila.ib.subsys.security;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.ContractDetails;
import com.ib.client.TickType;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.event.*;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.api.*;

/**
 * 2012-11-20<br>
 * $Id: IBSecurityHandlerTest.java 499 2013-02-07 10:43:25Z whirlwind $
 *
 */
public class IBSecurityHandlerTest {
	private static SecurityDescriptor descr;
	private IMocksControl control;
	private IBServiceLocator locator;
	private IBClient client;
	private EditableTerminal term;
	private S<EditableSecurity> modifier;
	private IBRequestContract reqContract;
	private IBRequestMarketData reqMktData;
	private EventType onContrError,onContrResponse,onMktError,onMktTick,onConn;
	private EditableSecurity security;
	private IBSecurityHandler handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
		descr = new SecurityDescriptor("AAPL","SMART","JPY",SecurityType.OPT);
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		term = control.createMock(EditableTerminal.class);
		client = control.createMock(IBClient.class);
		locator = control.createMock(IBServiceLocator.class);
		reqContract = control.createMock(IBRequestContract.class);
		reqMktData = control.createMock(IBRequestMarketData.class);
		modifier = control.createMock(S.class);
		onContrError = control.createMock(EventType.class);
		onContrResponse = control.createMock(EventType.class);
		onMktError = control.createMock(EventType.class);
		onMktTick = control.createMock(EventType.class);
		onConn = control.createMock(EventType.class);
		security = control.createMock(EditableSecurity.class);

		expect(reqContract.OnError()).andStubReturn(onContrError);
		expect(reqContract.OnResponse()).andStubReturn(onContrResponse);
		expect(reqMktData.OnError()).andStubReturn(onMktError);
		expect(reqMktData.OnTick()).andStubReturn(onMktTick);
		expect(locator.getTerminal()).andStubReturn(term);
		expect(locator.getApiClient()).andStubReturn(client);
		expect(client.OnConnectionOpened()).andStubReturn(onConn);
		
		handler = new IBSecurityHandler(locator, descr,  reqContract,
				reqMktData, modifier, 1000);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, handler.getServiceLocator());
		assertSame(descr, handler.getSecurityDescriptor());
		assertSame(reqContract, handler.getRequestContract());
		assertSame(reqMktData, handler.getRequestMarketData());
		assertSame(modifier, handler.getSecurityModifier());
		assertEquals(1000, handler.getRequestTimeout());
	}

	@Test
	public void testGetSecurityStatus_StartNone_Timeout() throws Exception {
		handler.setInitialStatus(IBSecurityStatus.NONE);
		reqContract.start();
		expectLastCall().andDelegateTo(new IBRequest() {
			@Override public void stop() { }
			@Override public void start() {
				// Можно подождать таймаут, но мы просто дернем
				// монитор, что бы ускорить процесс.
				// Естессно, делать это надо в "соседнем" потоке.
				new Thread(new Runnable() {
					@Override public void run() {
						synchronized ( handler.getMonitor() ) {
							handler.getMonitor().notifyAll();
						}
					}
				}).start();
			}
		});
		control.replay();
		try {
			handler.getSecurityStatus();
			fail("Expected exception: " +
					IBSecurityTimeoutException.class.getSimpleName());
		} catch ( IBSecurityTimeoutException e ) {
			// Статус сбрасывается, что бы повторный запрос запрашивал снова
			assertTrue(handler.isCurrentStatus(IBSecurityStatus.NONE));
		}
	}
	
	@Test
	public void testGetSecurityStatus_StartNone_Interrupted() throws Exception {
		handler.setInitialStatus(IBSecurityStatus.NONE);
		final CountDownLatch started = new CountDownLatch(1);
		final CountDownLatch finished = new CountDownLatch(1);
		final Thread test = new Thread(new Runnable() {
			@Override
			public void run() {
				started.countDown();
				try {
					handler.getSecurityStatus();
					fail("Expected exception: IBSecurityInterruptedException");
				} catch ( IBSecurityInterruptedException e ) {
					// Статус сбрасывается, что бы повторный запрос прошел
					assertTrue(handler.isCurrentStatus(IBSecurityStatus.NONE));
					finished.countDown();
				} catch ( Exception e ) {
					fail("Unhandled exception: " + e);
				}
			}
		});
		reqContract.start();
		control.replay();
		test.start();
		assertTrue(started.await(1000, TimeUnit.MILLISECONDS));
		test.interrupt();
		assertTrue(finished.await(1000, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testGetSecurityStatus_StartNone_Done() throws Exception {
		handler.setInitialStatus(IBSecurityStatus.NONE);
		reqContract.start();
		expectLastCall().andDelegateTo(new IBRequest() {
			@Override public void stop() { }
			@Override public void start() {
				new Thread(new Runnable() {
					@Override public void run() {
						synchronized ( handler.getMonitor() ) {
							handler.setInitialStatus(IBSecurityStatus.DONE);
							handler.getMonitor().notifyAll();
						}						
					}
				}).start();
			}
		});
		control.replay();
		assertSame(IBSecurityStatus.DONE, handler.getSecurityStatus());
		control.verify();
	}
	
	@Test
	public void testGetSecurity_StartNone_NotFound() throws Exception {
		handler.setInitialStatus(IBSecurityStatus.NONE);
		reqContract.start();
		expectLastCall().andDelegateTo(new IBRequest() {
			@Override public void stop() { }
			@Override public void start() {
				new Thread(new Runnable() {
					@Override public void run() {
						synchronized ( handler.getMonitor() ) {
							handler.setInitialStatus(IBSecurityStatus.NFND);
							handler.getMonitor().notifyAll();
						}						
					}
				}).start();
			}
		});
		control.replay();
		assertSame(IBSecurityStatus.NFND, handler.getSecurityStatus());
		control.verify();
	}
	
	/**
	 * Тест в три конкурирующих потока.
	 * <p>
	 * @throws Exception
	 */
	@Test
	public void testGetSecurityStatus_Concurrent_Done() throws Exception {
		final CountDownLatch started = new CountDownLatch(3);
		final CountDownLatch finished = new CountDownLatch(3);
		reqContract.start();
		expectLastCall().andDelegateTo(new IBRequest() {
			@Override public void stop() { }
			@Override public void start() {
				new Thread(new Runnable() {
					@Override public void run() {
						try {
							started.await();
							Thread.sleep(50); // эмуляция исполнения запроса
							synchronized ( handler.getMonitor() ) {
								handler.setInitialStatus(IBSecurityStatus.DONE);
								handler.getMonitor().notifyAll();
							}											
						} catch ( Exception e ) { }
					}
				}).start();
			}
		});
		control.replay();
		Runnable action = new Runnable() {
			@Override
			public void run() {
				started.countDown();
				try {
					started.await();
					assertSame(IBSecurityStatus.DONE,
							handler.getSecurityStatus());
					finished.countDown();
				} catch ( Exception e ) { }
			}
		}; 
		new Thread(action).start();
		new Thread(action).start();
		new Thread(action).start();
		assertTrue(finished.await(1000, TimeUnit.MILLISECONDS));
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		onContrError.addListener(same(handler));
		onContrResponse.addListener(same(handler));
		onMktTick.addListener(same(handler));
		onConn.addListener(same(handler));
		control.replay();
		
		handler.start();
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_Error200() throws Exception {
		reqContract.start();
		expectLastCall().andDelegateTo(new IBRequest() {
			@Override public void stop() { }
			@Override public void start() {
				new Thread(new Runnable() {@Override public void run() {
					handler.onEvent(new IBEventError(onContrError,0,200,"n/a"));
				}}).start();
			}
		});
		control.replay();
		assertSame(IBSecurityStatus.NFND, handler.getSecurityStatus());
		control.verify();
	}
	
	@Test (expected=IBSecurityTimeoutException.class)
	public void testOnEvent_ErrorUnk() throws Exception {
		reqContract.start();
		expectLastCall().andDelegateTo(new IBRequest() {
			@Override public void stop() { }
			@Override public void start() {
				new Thread(new Runnable() {
					@Override public void run() {
						handler.onEvent(new IBEventError(onContrError,0,0,"n"));
					}
				}).start();
			}
		});
		control.replay();
		handler.getSecurityStatus();
	}
	
	/**
	 * Тест успешной обработки события с деталями контракта.
	 * <p>
	 * @param subType субтип события 
	 */
	private void testOnEvent_OnResponse(final int subType) throws Exception {
		final ContractDetails details = new ContractDetails();
		reqContract.start();
		expectLastCall().andDelegateTo(new IBRequest() {
			@Override public void stop() { }
			@Override public void start() {
				new Thread(new Runnable() {
					@Override public void run() {
						handler.onEvent(new IBEventContract(onContrResponse, 0,
								subType, details));
					}
				}).start();
			}
		});
		expect(term.getEditableSecurity(same(descr))).andReturn(security);
		modifier.set(same(security), same(details));
		reqMktData.start();
		control.replay();
		assertSame(IBSecurityStatus.DONE, handler.getSecurityStatus());
		control.verify();		
	}
	
	@Test
	public void testOnEvent_OnResponse_OkNorm() throws Exception {
		testOnEvent_OnResponse(IBEventContract.SUBTYPE_NORM);
	}
	
	@Test
	public void testOnEvent_OnResponse_OkBond() throws Exception {
		testOnEvent_OnResponse(IBEventContract.SUBTYPE_BOND);
	}
	
	@Test
	public void testOnEvent_OnResponse_SkipEndContract() throws Exception {
		control.replay();
		handler.onEvent(new IBEventContract(onContrResponse, 0,
				IBEventContract.SUBTYPE_END, null));
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnMktError() throws Exception {
		control.replay();
		handler.onEvent(new IBEventError(onMktError, 0, 404, "test error"));
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnMktTick() throws Exception {
		IBEventTick event = new IBEventTick(onMktTick, 0, TickType.ASK, 10.0d);
		expect(term.getEditableSecurity(same(descr))).andReturn(security);
		modifier.set(same(security), same(event));
		control.replay();
		handler.onEvent(event);
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnConnectionOpened() throws Exception {
		Object[][] fix = {
			// initial status, request mkt data?
			{ IBSecurityStatus.NONE, false },
			{ IBSecurityStatus.SENT, false },
			{ IBSecurityStatus.NFND, false },
			{ IBSecurityStatus.DONE, true  },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			setUp();
			handler.setInitialStatus((IBSecurityStatus) fix[i][0]);
			if ( (Boolean) fix[i][1] == true ) {
				reqMktData.start();
			}
			control.replay();
			handler.onEvent(new EventImpl(onConn));
			control.verify();
		}
	}

}
