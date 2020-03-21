package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventDispatcherImpl;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.PortfolioImpl.PortfolioController;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCController;
import ru.prolib.aquila.core.BusinessEntities.osc.impl.PortfolioParamsBuilder;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.concurrency.Multilock;
import ru.prolib.aquila.core.data.DataProviderStub;

/**
 * 2012-09-06
 */
public class PortfolioImplTest extends ObservableStateContainerImplTest {
	private static Account account = new Account("ZUMBA");
	private EditableTerminal terminal;
	private SchedulerStub schedulerStub;
	private PortfolioController controller;
	private PortfolioImpl portfolio;
	Lock lock;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ObservableStateContainerImplTest.setUpBeforeClass();
	}
	
	@Before
	public void setUp() throws Exception {
		lock = new ReentrantLock();
		controller = new PortfolioController();
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Override
	protected String getID() {
		return portfolio.getContainerID();
	}
	
	private void prepareTerminal() {
		schedulerStub = new SchedulerStub();
		terminal = new BasicTerminalBuilder()
				.withTerminalID("Terminal#1")
				.withEventQueue(queue)
				.withDataProvider(new DataProviderStub())
				.withScheduler(schedulerStub)
				.withLock(lock)
				.buildTerminal();
		terminal.getEditableSecurity(new Symbol("SBER"));
		terminal.getEditableSecurity(new Symbol("GAZP"));
		terminal.getEditableSecurity(new Symbol("AAPL"));
		terminal.getEditableSecurity(new Symbol("MSFT"));
		terminal.getEditableSecurity(new Symbol("SPY"));
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer() {
		prepareTerminal();
		portfolio = new PortfolioImpl(new PortfolioParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withObjectFactory(new ObjectFactoryImpl(lock))
				.withLock(lock)
				.buildParams());
		return portfolio;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(OSCController controller) {
		prepareTerminal();
		portfolio = new PortfolioImpl(new PortfolioParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withController(controller)
				.withObjectFactory(new ObjectFactoryImpl(lock))
				.withLock(lock)
				.buildParams());
		return portfolio;
	}
	
	@Override
	protected ObservableStateContainerImpl produceContainer(EventDispatcher eventDispatcher,
			OSCController controller)
	{
		prepareTerminal();
		portfolio = new PortfolioImpl(new PortfolioParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withEventDispatcher(eventDispatcher)
				.withController(controller)
				.withObjectFactory(new ObjectFactoryImpl(lock))
				.withLock(lock)
				.buildParams());
		return portfolio;
	}
	
	@Test
	public void testCtor_DefaultController() throws Exception {
		produceContainer();
		assertEquals(PortfolioController.class, portfolio.getController().getClass());
		assertNotNull(portfolio.getTerminal());
		assertNotNull(portfolio.getEventDispatcher());
		assertSame(terminal, portfolio.getTerminal());
		assertSame(queue, ((EventDispatcherImpl)portfolio.getEventDispatcher()).getEventQueue());
		assertEquals(account, portfolio.getAccount());
		String prefix = String.format("%s.ZUMBA.PORTFOLIO", terminal.getTerminalID());
		assertEquals(prefix, portfolio.getContainerID());
		assertEquals(prefix + ".AVAILABLE", portfolio.onAvailable().getId());
		assertEquals(prefix + ".UPDATE", portfolio.onUpdate().getId());
		assertEquals(prefix + ".POSITION_AVAILABLE", portfolio.onPositionAvailable().getId());
		assertEquals(prefix + ".POSITION_CHANGE", portfolio.onPositionChange().getId());
		assertEquals(prefix + ".POSITION_PRICE_CHANGE", portfolio.onPositionCurrentPriceChange().getId());
		assertEquals(prefix + ".POSITION_UPDATE", portfolio.onPositionUpdate().getId());
		assertEquals(prefix + ".POSITION_CLOSE", portfolio.onPositionClose().getId());
		assertSame(lock, portfolio.getLock());
	}
	
	@Test
	public void testClose() throws Exception {
		produceContainer(controller);
		schedulerStub.setFixedTime("2017-08-04T18:15:00Z");
		EventListenerStub listener = new EventListenerStub();
		EventType type = new EventTypeImpl();
		portfolio.onAvailable().addListener(listener);
		portfolio.onAvailable().addAlternateType(type);
		portfolio.onPositionAvailable().addListener(listener);
		portfolio.onPositionAvailable().addAlternateType(type);
		portfolio.onPositionChange().addListener(listener);
		portfolio.onPositionChange().addAlternateType(type);
		portfolio.onPositionCurrentPriceChange().addListener(listener);
		portfolio.onPositionCurrentPriceChange().addAlternateType(type);
		portfolio.onPositionUpdate().addListener(listener);
		portfolio.onPositionUpdate().addAlternateType(type);
		portfolio.onUpdate().addListener(listener);
		portfolio.onUpdate().addAlternateType(type);
		portfolio.onClose().addListener(listenerStub);
		portfolio.onClose().addAlternateType(type);
		
		portfolio.close();
		
		assertNull(portfolio.getTerminal());
		assertFalse(portfolio.onAvailable().hasListeners());
		assertFalse(portfolio.onAvailable().hasAlternates());
		assertFalse(portfolio.onPositionAvailable().hasListeners());
		assertFalse(portfolio.onPositionAvailable().hasAlternates());
		assertFalse(portfolio.onPositionChange().hasListeners());
		assertFalse(portfolio.onPositionChange().hasAlternates());
		assertFalse(portfolio.onPositionCurrentPriceChange().hasListeners());
		assertFalse(portfolio.onPositionCurrentPriceChange().hasAlternates());
		assertFalse(portfolio.onPositionUpdate().hasListeners());
		assertFalse(portfolio.onPositionUpdate().hasAlternates());
		assertFalse(portfolio.onUpdate().hasListeners());
		assertFalse(portfolio.onUpdate().hasAlternates());
		assertTrue(portfolio.onClose().isAlternateType(type));
		assertTrue(portfolio.onClose().isListener(listenerStub));
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEventWUT(listenerStub.getEvent(0),
				PortfolioEvent.class,
				portfolio.onClose(),
				portfolio,
				T("2017-08-04T18:15:00Z"));
	}
	
	@Test
	public void testClose_ClosesAndRemovesAllPositions() throws Exception {
		Position p1 = portfolio.getPosition(new Symbol("MSFT"));
		Position p2 = portfolio.getPosition(new Symbol("AAPL"));
		
		portfolio.close();
		
		assertEquals(0, portfolio.getPositionCount());
		assertTrue(p1.isClosed());
		assertTrue(p2.isClosed());
	}
	
	@Test
	public void testGetBalance() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getBalance();
			}
		};
		testGetter(PortfolioField.BALANCE,
				CDecimalBD.ofUSD2("40560.28"), CDecimalBD.ofUSD2("80340.95"));
	}

	@Test
	public void testGetEquity() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getEquity();
			}
		};
		testGetter(PortfolioField.EQUITY,
				CDecimalBD.ofUSD2("812.76"), CDecimalBD.ofUSD2("324.10"));
	}
	
	@Test
	public void testGetProfitAndLoss() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getProfitAndLoss();
			}
		};
		testGetter(PortfolioField.PROFIT_AND_LOSS,
				CDecimalBD.ofRUB2("100000.00"), CDecimalBD.ofRUB2("80000.00"));
	}

	@Test
	public void testGetUsedMargin() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getUsedMargin();
			}
		};
		testGetter(PortfolioField.USED_MARGIN,
				CDecimalBD.ofRUB2("96283.15"), CDecimalBD.ofRUB2("94518.22"));
	}
	
	@Test
	public void testGetFreeMargin() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getFreeMargin();
			}
		};
		testGetter(PortfolioField.FREE_MARGIN,
				CDecimalBD.ofUSD2("4519.72"), CDecimalBD.ofUSD2("5425.12"));
	}

	@Test
	public void testGetMarginCallLevel() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getMarginCallLevel();
			}
		};
		testGetter(PortfolioField.MARGIN_CALL_AT, CDecimalBD.of("0.30"), CDecimalBD.of("0.25"));
	}
	
	@Test
	public void testGetMarginStopOutLevel() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getMarginStopOutLevel();
			}
		};
		testGetter(PortfolioField.MARGIN_STOP_OUT_AT, CDecimalBD.of("0.50"), CDecimalBD.of("0.75"));
	}

	@Test
	public void testGetAssets() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getAssets();
			}
		};
		testGetter(PortfolioField.ASSETS,
				CDecimalBD.ofUSD2("12.34"), CDecimalBD.ofUSD2("56.78"));
	}

	@Test
	public void testGetLiabilities() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getLiabilities();
			}
		};
		testGetter(PortfolioField.LIABILITIES,
				CDecimalBD.ofUSD2("632.88"), CDecimalBD.ofUSD2("640.19"));
	}

	@Test
	public void testGetCurrency() throws Exception {
		getter = new Getter<String>() {
			@Override public String get() {
				return portfolio.getCurrency();
			}
		};
		testGetter(PortfolioField.CURRENCY, "RUB", "USD");
	}
	
	@Test
	public void testGetLeverage() throws Exception {
		getter = new Getter<CDecimal>() {
			@Override public CDecimal get() {
				return portfolio.getLeverage();
			}
		};
		testGetter(PortfolioField.LEVERAGE, CDecimalBD.of("4.0"), CDecimalBD.of("2.0"));
	}
	
	@Test
	public void testGetPositionCount() throws Exception {
		assertEquals(0, portfolio.getPositionCount());
		
		portfolio.getPosition(new Symbol("GAZP"));
		portfolio.getPosition(new Symbol("SBER"));
		
		assertEquals(2, portfolio.getPositionCount());
		
		portfolio.getPosition(new Symbol("AAPL"));
		
		assertEquals(3, portfolio.getPositionCount());
	}
	
	@Test
	public void testGetPositions() throws Exception {
		Set<Position> expected = new HashSet<Position>();
		expected.add(portfolio.getPosition(new Symbol("MSFT")));
		expected.add(portfolio.getPosition(new Symbol("AAPL")));
		expected.add(portfolio.getPosition(new Symbol("SPY")));
		
		assertEquals(expected, portfolio.getPositions());
	}

	@Test
	public void testGetPosition() throws Exception {
		Position position = portfolio.getPosition(new Symbol("MSFT"));
		
		assertNotNull(position);
		assertEquals(new Symbol("MSFT"), position.getSymbol());
		assertEquals(account, position.getAccount());
		assertSame(terminal, position.getTerminal());
		assertTrue(position.onAvailable().isAlternateType(portfolio.onPositionAvailable()));
		assertTrue(position.onCurrentPriceChange().isAlternateType(portfolio.onPositionCurrentPriceChange()));
		assertTrue(position.onPositionChange().isAlternateType(portfolio.onPositionChange()));
		assertTrue(position.onUpdate().isAlternateType(portfolio.onPositionUpdate()));
		assertTrue(position.onClose().isAlternateType(portfolio.onPositionClose()));
		assertSame(position, portfolio.getPosition(new Symbol("MSFT")));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetPosition_ThrowsIfClosed() throws Exception {
		portfolio.close();
		
		portfolio.getPosition(new Symbol("SBER"));
	}

	@Test
	public void testGetEditablePosition() throws Exception {
		EditablePosition position = portfolio.getEditablePosition(new Symbol("MSFT"));
		
		assertNotNull(position);
		assertEquals(new Symbol("MSFT"), position.getSymbol());
		assertEquals(account, position.getAccount());
		assertSame(terminal, position.getTerminal());
		assertTrue(position.onAvailable().isAlternateType(portfolio.onPositionAvailable()));
		assertTrue(position.onCurrentPriceChange().isAlternateType(portfolio.onPositionCurrentPriceChange()));
		assertTrue(position.onPositionChange().isAlternateType(portfolio.onPositionChange()));
		assertTrue(position.onUpdate().isAlternateType(portfolio.onPositionUpdate()));
		assertTrue(position.onClose().isAlternateType(portfolio.onPositionClose()));
		assertSame(position, portfolio.getEditablePosition(new Symbol("MSFT")));
		assertSame(lock, ((PositionImpl) position).getLock());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetEditablePosition_ThrowsIfClosed() throws Exception {
		portfolio.close();
		
		portfolio.getEditablePosition(new Symbol("AAPL"));
	}

	@Test
	public void testPortfolioController_HasMinimalData() {
		assertFalse(controller.hasMinimalData(portfolio, null));
		
		portfolio.consume(new DeltaUpdateBuilder()
			.withToken(PortfolioField.CURRENCY, "USD")
			.withToken(PortfolioField.BALANCE, CDecimalBD.ofUSD2("415.08"))
			.withToken(PortfolioField.EQUITY, CDecimalBD.ofUSD2("213.34"))
			.withToken(PortfolioField.PROFIT_AND_LOSS, CDecimalBD.ofUSD2("1.18"))
			.withToken(PortfolioField.USED_MARGIN, CDecimalBD.ofUSD2("50.72"))
			.withToken(PortfolioField.FREE_MARGIN, CDecimalBD.ofUSD2("0.52"))
			.buildUpdate());
		
		assertTrue(controller.hasMinimalData(portfolio, null));
	}
	
	@Test
	public void testPortfolioController_ProcessAvailable() {
		// No additional event types. Nothing to do.
	}
	
	@Test
	public void testPortfolioController_ProcessUpdate() {
		// No additional event types. Nothing to do.
	}
	
	@Test
	public void testPortfolioController_GetCurrentTime_IfNotClosed() {
		schedulerStub.setFixedTime("2017-08-04T18:07:00Z");
		
		assertEquals(T("2017-08-04T18:07:00Z"), controller.getCurrentTime(portfolio));
	}
	
	@Test
	public void testPortfolioController_GetCurrentTime_IfClosed() {
		schedulerStub.setFixedTime("2017-08-04T18:07:00Z");
		portfolio.close();
		
		assertNull(controller.getCurrentTime(portfolio));
	}

	@Test
	public void testUpdate_OnAvailable() throws Exception {
		Instant time = T("2017-08-04T18:25:00Z");
		container = produceContainer(controllerMock);
		container.onAvailable().addListener(listenerStub);
		expect(controllerMock.getCurrentTime(portfolio)).andReturn(time);
		controllerMock.processUpdate(portfolio, time);
		expect(controllerMock.hasMinimalData(portfolio, time)).andReturn(true);
		controllerMock.processAvailable(portfolio, time);
		getMocksControl().replay();

		portfolio.update(12345, 415); // any value
		
		getMocksControl().verify();
		assertEquals(1, listenerStub.getEventCount());
		assertContainerEventWUT(listenerStub.getEvent(0),
				PortfolioEvent.class,
				portfolio.onAvailable(),
				portfolio,
				time);
	}
	
	@Test
	public void testUpdate_OnUpdateEvent() throws Exception {
		Instant time1 = T("2017-08-04T18:28:00Z"), time2 = T("2017-08-04T18:29:00Z");
		container = produceContainer(controllerMock);
		container.onUpdate().addListener(listenerStub);
		expect(controllerMock.getCurrentTime(portfolio)).andReturn(time1);
		controllerMock.processUpdate(portfolio, time1);
		expect(controllerMock.hasMinimalData(portfolio, time1)).andReturn(true);
		controllerMock.processAvailable(portfolio, time1);
		expect(controllerMock.getCurrentTime(portfolio)).andReturn(time2);
		controllerMock.processUpdate(portfolio, time2);
		getMocksControl().replay();
		
		portfolio.update(12345, 415);
		portfolio.update(12345, 450);

		getMocksControl().verify();
		assertEquals(2, listenerStub.getEventCount());
		assertContainerEvent(listenerStub.getEvent(0),
				PortfolioUpdateEvent.class,
				portfolio.onUpdate(),
				portfolio,
				time1,
				m(p(12345, null)),
				m(p(12345, 415)));
		assertContainerEvent(listenerStub.getEvent(1),
				PortfolioUpdateEvent.class,
				portfolio.onUpdate(),
				portfolio,
				time2,
				m(p(12345, 415)),
				m(p(12345, 450)));
	}
	
	@Test
	public void testIsPositionExists() throws Exception {
		Symbol symbol1 = new Symbol("SBER"),
				symbol2 = new Symbol("GAZP");
		
		assertFalse(portfolio.isPositionExists(symbol1));
		assertFalse(portfolio.isPositionExists(symbol2));
		
		portfolio.getPosition(symbol1);
		
		assertTrue(portfolio.isPositionExists(symbol1));
		assertFalse(portfolio.isPositionExists(symbol2));
		
		portfolio.getPosition(symbol2);
		
		assertTrue(portfolio.isPositionExists(symbol1));
		assertTrue(portfolio.isPositionExists(symbol2));
	}

	// ------------------------------------------------------------------------
	// Тесты блокировки методов добавления новых позиций в портфель.
	//
	// Термины:
	// КСЗНП - Критическая секция запрета новых позиций;
	// БП - блокирующий поток - поток, который блокирует КСЗНП;
	// КП - конкурирующий поток - поток, который вызывает методы, приводящие к
	// созданию новых экземпляров позиций.
	// 
	// Case1 - БП успевает блокировать до обращения КП. КП дожидаются снятия
	// блокировки, после чего действия КП приводят к созданию новых позиций.
	//
	// Case2 - БП пытается заблокировать в тот момент, когда выполняется
	// обращение к методу, порождающему новую позицию. Действия КП приводят
	// к созданию новых позиций, которые доступны после блокировки объекта БП.
	//
	// Case3 - Два БП последовательно пытаются запретить новые позиции.
	// Второй БП должен ждать окончания запрета в методе запрета.
	//
	// Case4 - БП пытается захватить блокировку дважды. Это может
	// свидетельствовать о некорректности алгоритма (какая-то сложная логика
	// размазана между захватом и освобождением лока). В этом случае должно
	// выбрасываться исключение с освобождением лока.
	//
	// Case5 - БП после захвата блокировки пытается выполнить действия, которые
	// приведут (могут привести) к созданию новой позиции. Данное поведение
	// потенциально опасно по причинам аналогичным Case4. В таком случае должно
	// выбрасываться исключение. При этом, блокировка должна сниматься
	// автоматически!
	//
	// Case6 - Типовой случай использования блокировки добавления новых позиций
	// - БП блокирует создание позиций с целью создания мультлока на портфель и
	// инструменты позиции. После захвата мультилока, БП снимает блокировку на
	// добавление позиций. При этом, КП спускается до обычного лока и находится
	// в этом состоянии до освобождения объекта БП.
	//
	// Case7 - КП захватывает основной лок, но не успевает зайти в метод,
	// ограниченный КСЗНП. БП устанавливает запрет новых позиций и пытается
	// получить список позиций. Не должен возникнуть дедлок.
	//
	// Case9 - Снять блокировку может только тот поток, который выполнил
	// блокировку. Если это условие не выполняется, значит поток предпринявший
	// такую попытку не пытался получить лок на КСЗНП. Иначе он бы находился
	// именно там в ожидании снятия блокировки.
	//
	// Вопросы: ---
	// 
	
	@Test
	public void testLockNewPositions_Case1() throws Exception {
		produceContainer();
		final Symbol symbol = new Symbol("SBER");
		final CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(3);
		Thread bt = new Thread() {
			@Override
			public void run() {
				portfolio.lockNewPositions();
				try {
					started.countDown();
					Thread.sleep(100L);
					portfolio.update(PortfolioField.BALANCE, CDecimalBD.ofRUB2("1500"));
					if ( ! portfolio.isPositionExists(symbol) ) {
						successPoints.countDown();
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				} finally {
					portfolio.unlockNewPositions();
				}
			}
		};
		Thread ct = new Thread() {
			@Override
			public void run() {
				try {
					if ( started.await(1L, TimeUnit.SECONDS) ) {
						if ( ! CDecimalBD.ofRUB2("1500").equals(portfolio.getBalance()) ) {
							successPoints.countDown();
						}
						portfolio.getPosition(symbol);
						if ( CDecimalBD.ofRUB2("1500").equals(portfolio.getBalance()) ) {
							successPoints.countDown();
						}
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		ct.start();
		bt.start();
		assertTrue(successPoints.await(1L, TimeUnit.SECONDS));
	}
	
	@Test
	public void testLockNewPositions_Case2() throws Exception {
		final Symbol symbol = new Symbol("SBER");
		final CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(4);
		final List<String> log = new Vector<>();
		ObjectFactory objectFactory = new ObjectFactoryImpl(lock) {
			@Override
			public EditablePosition createPosition(EditableTerminal terminal, Account account, Symbol symbol) {
				try {
					log.add("OF: enter");
					started.countDown();
					log.add("OF: sleep");
					Thread.sleep(100L);
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
				log.add("OF: exit");
				return new ObjectFactoryImpl(lock).createPosition(terminal, account, symbol);
			}
		};
		portfolio = new PortfolioImpl(new PortfolioParamsBuilder(queue)
				.withTerminal(terminal)
				.withAccount(account)
				.withObjectFactory(objectFactory)
				.buildParams());
		Thread bt = new Thread() {
			@Override
			public void run() {
				try {
					log.add("BT: enter");
					// Здесь нельзя проверять наличие позиции!
					// Поскольку КП захватывает лок на портфель, мы не можем добавить
					// дополнительный сигнал для метода createPosition фабрики, чтобы
					// пройти эту проверку. Мы можем либо добавить сигнал, либо
					// надеяться, что проверка пройдет до того, как КП захватит лок
					// в методе getEditablePosition. А это - ненадежный тест.
					//if ( ! portfolio.isPositionExists(symbol) ) {
					//	log.add("BT: position not exists");
						successPoints.countDown();
					//}
					if ( started.await(1, TimeUnit.SECONDS) ) {
						log.add("BT: lock new positions...");
						portfolio.lockNewPositions();
						log.add("BT: lock portfolio...");
						portfolio.lock();
						try {
							if ( portfolio.isPositionExists(symbol) ) {
								log.add("BT: position exists");
								successPoints.countDown();
							}
							log.add("BT: update portfolio");
							portfolio.update(PortfolioField.BALANCE, CDecimalBD.ofRUB2("10000"));
						} finally {
							log.add("BT: unlock portfolio and new positions");
							portfolio.unlock();
							portfolio.unlockNewPositions();
						}
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		Thread ct = new Thread() {
			@Override
			public void run() {
				log.add("CT: enter");
				if ( ! CDecimalBD.ofRUB2("10000").equals(portfolio.getBalance()) ) {
					log.add("CT: portfolio isn't updated");
					successPoints.countDown();
				}
				log.add("CT: lock portfolio...");
				portfolio.lock();
				try {
					log.add("CT: create position");
					portfolio.getPosition(symbol);
					if ( ! CDecimalBD.ofRUB2("10000").equals(portfolio.getBalance()) ) {
						log.add("CT: portfolio isn't updated");
						successPoints.countDown();
					}
				} finally {
					log.add("CT: unlock portfolio");
					portfolio.unlock();
				}
			}
		};
		ct.start();
		bt.start();
		boolean r = successPoints.await(2L, TimeUnit.SECONDS);
		//System.err.println(getClass().getSimpleName() + "#testLockNewPositions_Case2 log:");
		//for ( String x : log ) {
		//	System.err.println(x);
		//}
		assertTrue("Timeout", r);
	}
	
	@Test
	public void testLockNewPositions_Case3() throws Exception {
		final CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(2);
		Thread bt1 = new Thread() {
			@Override
			public void run() {
				portfolio.lockNewPositions();
				try {
					started.countDown();
					Thread.sleep(100L);
					successPoints.countDown();
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				} finally {
					portfolio.unlockNewPositions();
				}
			}
		};
		Thread bt2 = new Thread() {
			@Override
			public void run() {
				try {
					if ( started.await(1L, TimeUnit.SECONDS) ) {
						portfolio.lockNewPositions();
						try {
							successPoints.countDown();
						} finally {
							portfolio.unlockNewPositions();
						}
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		bt2.start();
		bt1.start();
		assertTrue(successPoints.await(1L, TimeUnit.SECONDS));
	}
	
	@Test
	public void testLockNewPositions_Case4() throws Exception {
		final Symbol symbol = new Symbol("AAPL");
		final CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(2);
		Thread bt = new Thread() {
			@Override
			public void run() {
				portfolio.lockNewPositions();
				started.countDown();
				try {
					portfolio.lockNewPositions();
				} catch ( IllegalStateException e ) {
					//System.err.println(e.getMessage());
					successPoints.countDown();
				}
				// No explicit unlock needed.
				// The second call must release lock.
			}
		};
		Thread ct = new Thread() {
			@Override
			public void run() {
				try {
					if ( started.await(1L, TimeUnit.SECONDS) ) {
						portfolio.getPosition(symbol);
						successPoints.countDown();
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		ct.start();
		bt.start();
		assertTrue(successPoints.await(1L, TimeUnit.SECONDS));
	}
	
	@Test
	public void testLockNewPositions_Case5() throws Exception {
		final Symbol symbol = new Symbol("AAPL");
		final CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(2);
		Thread bt = new Thread() {
			@Override
			public void run() {
				portfolio.lockNewPositions();
				started.countDown();
				try {
					portfolio.getPosition(symbol);
				} catch ( IllegalStateException e ) {
					//System.err.println(e.getMessage());
					successPoints.countDown();
				}
				// No explicit unlock needed.
				// The second call must release lock.
			}
		};
		Thread ct = new Thread() {
			@Override
			public void run() {
				try {
					if ( started.await(1L, TimeUnit.SECONDS) ) {
						portfolio.getPosition(symbol);
						successPoints.countDown();
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		ct.start();
		bt.start();
		assertTrue(successPoints.await(1L, TimeUnit.SECONDS));
	}
	
	// Case6 - Типовой случай использования блокировки добавления новых позиций
	// - БП блокирует создание позиций с целью создания мультлока на портфель и
	// инструменты позиции. После захвата мультилока, БП снимает блокировку на
	// добавление позиций. При этом, КП спускается до обычного лока и находится
	// в этом состоянии до освобождения объекта БП.
	@Test
	public void testLockNewPositions_Case6() throws Exception {
		final Symbol symbol1 = new Symbol("AAPL"),
				symbol2 = new Symbol("MSFT"),
				symbol3 = new Symbol("SBER");
		terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol2);
		terminal.getEditableSecurity(symbol3);
		portfolio.getPosition(symbol1);
		portfolio.getPosition(symbol2);
		final CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(3);
		Thread bt = new Thread() {
			@Override
			public void run() {
				portfolio.lockNewPositions();
				started.countDown();
				try {
					Thread.sleep(100L);
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
					portfolio.unlockNewPositions();
					return;
				}
				Set<Lockable> lockable = new HashSet<>();
				lockable.add(portfolio);
				for ( Position p : portfolio.getPositions() ) {
					lockable.add(p.getSecurity());
				}
				if ( lockable.size() == 3 ) {
					successPoints.countDown();
				}
				Lockable z = new Multilock(lockable);
				z.lock();
				try {
					portfolio.unlockNewPositions();
					successPoints.countDown();
				} finally {
					z.unlock();
				}
			}
		};
		Thread ct = new Thread() {
			@Override
			public void run() {
				try {
					if ( started.await(1L, TimeUnit.SECONDS) ) {
						portfolio.getPosition(symbol3);
						successPoints.countDown();
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		ct.start();
		bt.start();
		assertTrue(successPoints.await(1L, TimeUnit.SECONDS));
	}

	@Test
	public void testLockNewPositions_Case7() throws Exception {
		final Symbol symbol = new Symbol("SBER");
		final CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(2); 
		Thread ct = new Thread() {
			@Override
			public void run() {
				portfolio.lock();
				try {
					started.countDown();
					Thread.sleep(100L);
					portfolio.getPosition(symbol);
					successPoints.countDown();
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				} finally {
					portfolio.unlock();
				}
			}
		};
		Thread bt = new Thread() {
			@Override
			public void run() {
				try {
					started.await(1, TimeUnit.SECONDS);
					portfolio.lockNewPositions();
					try {
						if ( portfolio.isPositionExists(symbol) ) {
							successPoints.countDown();
						}
					} finally {
						portfolio.unlockNewPositions();
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		ct.start();
		bt.start();
		assertTrue(successPoints.await(1L, TimeUnit.SECONDS));
	}

	@Test
	public void testLockNewPositions_Case9() throws Exception {
		final CountDownLatch started = new CountDownLatch(1),
				successPoints = new CountDownLatch(2); 
		Thread bt1 = new Thread() {
			@Override
			public void run() {
				portfolio.lockNewPositions();
				try {
					started.countDown();
					Thread.sleep(100L);
					successPoints.countDown();
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				} finally {
					portfolio.unlockNewPositions();
				}
			}
		};
		Thread bt2 = new Thread() {
			@Override
			public void run() {
				try {
					if ( started.await(1L, TimeUnit.SECONDS) ) {
						try {
							portfolio.unlockNewPositions();
						} catch ( IllegalStateException e ) {
							//System.err.println(e.getMessage());
							successPoints.countDown();
						}
					}
				} catch ( Exception e ) {
					e.printStackTrace(System.err);
				}
			}
		};
		bt1.start();
		bt2.start();
		assertTrue(successPoints.await(1L, TimeUnit.SECONDS));
	}

}
