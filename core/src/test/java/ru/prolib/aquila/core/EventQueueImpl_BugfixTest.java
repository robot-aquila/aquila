package ru.prolib.aquila.core;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventQueueImpl_BugfixTest {
	private static final Logger logger;

	static {
		logger = LoggerFactory.getLogger(EventQueueImpl_BugfixTest.class);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private EventQueueImpl queue;

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueImpl("TEST");
	}

	/**
	 * Кейс 1: Дедлок при использовании третьего объекта.
	 * <p>
	 * Проблема возникает когда два потока работают с очередью и одновременно с неким
	 * третьим блокируемым объектом. Такая ситуация легко возникает для пары планировщик
	 * и очередь событий. На примере нижеприведенных дампов: задача планировщика начинает
	 * транзакцию, блокируя инструмент, после чего пытается инициировать отправку сообщения.
	 * Поток диспетчеризации событий занят обработкой события, которая инициировала
	 * отправку нового события и уже захватила монитор очереди. У нового события есть
	 * синхронный обработчик, который пытается захватить монитор инструмента, который
	 * заблокирован потоком планировщика. 
	 * <p>
	 * Решение: Переработать синхронизацию методов отправки событий. Использовать
	 * дополнительную переменную для сигнализирования о наличии обработчика очереди.
	 * Использовать отдельный лок для доступа к статусу обработчика и кэшу заявок первого
	 * уровня.
<pre>
-------------------------------------------------------------------------------
Name: SECURITY_SIMULATION
State: WAITING on java.util.concurrent.locks.ReentrantLock$NonfairSync@373d2b02 owned by: PROBE-SCHEDULER
Total blocked: 6 339  Total waited: 7 500

Stack trace: 
sun.misc.Unsafe.park(Native Method)
java.util.concurrent.locks.LockSupport.park(Unknown Source)
java.util.concurrent.locks.AbstractQueuedSynchronizer.parkAndCheckInterrupt(Unknown Source)
java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireQueued(Unknown Source)
java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(Unknown Source)
java.util.concurrent.locks.ReentrantLock$NonfairSync.lock(Unknown Source)
java.util.concurrent.locks.ReentrantLock.lock(Unknown Source)
ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl.getObject(UpdatableStateContainerImpl.java:143)
ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl.getDecimal(UpdatableStateContainerImpl.java:159)
ru.prolib.aquila.core.BusinessEntities.SecurityImpl.getLowerPriceLimit(SecurityImpl.java:93)
ru.prolib.aquila.utils.experimental.sst.robot.SCloseLong.enter(SCloseLong.java:38)
ru.prolib.aquila.core.sm.SMStateMachine.doExit(SMStateMachine.java:248)
ru.prolib.aquila.core.sm.SMStateMachine.input(SMStateMachine.java:109)
   - locked ru.prolib.aquila.core.sm.SMStateMachine@24fd88ac
ru.prolib.aquila.core.sm.SMTriggerRegistry.input(SMTriggerRegistry.java:105)
   - locked ru.prolib.aquila.core.sm.SMStateMachine@24fd88ac
   - locked ru.prolib.aquila.core.sm.SMTriggerRegistry@d39046c
ru.prolib.aquila.core.sm.SMTriggerOnEvent.onEvent(SMTriggerOnEvent.java:50)
   - locked ru.prolib.aquila.core.sm.SMTriggerOnEvent@5e5ac82a
ru.prolib.aquila.core.EventQueueImpl.enqueue(EventQueueImpl.java:167)
ru.prolib.aquila.core.EventQueueImpl.enqueue(EventQueueImpl.java:241)
   - locked ru.prolib.aquila.core.EventQueueImpl@2e5e3eb5
ru.prolib.aquila.utils.experimental.sst.robot.Signal.fireBearish(Signal.java:40)
ru.prolib.aquila.utils.experimental.sst.robot.SignalProvider$CrossingMovingAverages.onEvent(SignalProvider.java:66)
ru.prolib.aquila.core.EventQueueImpl$QueueWorker.run(EventQueueImpl.java:214)
java.lang.Thread.run(Unknown Source)
-------------------------------------------------------------------------------
Name: PROBE-SCHEDULER
State: BLOCKED on ru.prolib.aquila.core.EventQueueImpl@2e5e3eb5 owned by: SECURITY_SIMULATION
Total blocked: 359  Total waited: 97

Stack trace: 
ru.prolib.aquila.core.EventQueueImpl.enqueue(EventQueueImpl.java:233)
ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl.update(ObservableStateContainerImpl.java:93)
ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl.consume(UpdatableStateContainerImpl.java:406)
ru.prolib.aquila.qforts.impl.QFAssembler.update(QFAssembler.java:55)
ru.prolib.aquila.qforts.impl.QFAssembler.update(QFAssembler.java:69)
ru.prolib.aquila.qforts.impl.QFTransactionService.updateByMarket(QFTransactionService.java:138)
ru.prolib.aquila.qforts.impl.QForts.updateByMarket(QForts.java:68)
ru.prolib.aquila.qforts.impl.QFReactor.run(QFReactor.java:101)
ru.prolib.aquila.core.BusinessEntities.SPRunnableTaskHandler.run(SPRunnableTaskHandler.java:43)
ru.prolib.aquila.probe.scheduler.SchedulerTaskImpl.execute(SchedulerTaskImpl.java:174)
ru.prolib.aquila.probe.scheduler.SchedulerWorkingPass.execute(SchedulerWorkingPass.java:64)
ru.prolib.aquila.probe.scheduler.SchedulerWorker.run(SchedulerWorker.java:40)
java.lang.Thread.run(Unknown Source)
</pre>
	 */
	@Test
	public void testDeadlockCase1() throws Exception {
		final CountDownLatch finished = new CountDownLatch(2),
				concurrentReady = new CountDownLatch(1),
				queueAcquired = new CountDownLatch(1);
		final Lock sharedObject = new ReentrantLock();
		final EventType signal1 = new EventTypeImpl(), signal2 = new EventTypeImpl();
		signal1.addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				queueAcquired.countDown();
				logger.debug("queue acquired");
				try {
					if ( sharedObject.tryLock(1, TimeUnit.SECONDS) ) {
						logger.debug("shared object locked");
						finished.countDown();
						sharedObject.unlock();
						logger.debug("shared object released");
					} else {
						logger.debug("cannot lock shared object");
					}
				} catch ( InterruptedException e ) {
					fail("Unhandled exception: " + e);
					Thread.currentThread().interrupt();
				}
			}
		});
		Thread concurrentThread = new Thread("CONCURRENT") {
			@Override
			public void run() {
				try {
					if ( sharedObject.tryLock(1, TimeUnit.SECONDS) ) {
						logger.debug("shared object locked");
						concurrentReady.countDown();
						logger.debug("signal concurrent thread ready");
						if ( queueAcquired.await(1, TimeUnit.SECONDS) ) {
							logger.debug("queue acquired signal received. enqueueing new event...");
							queue.enqueue(signal2, SimpleEventFactory.getInstance());
							logger.debug("new event enqueued");
							finished.countDown();
						}
						sharedObject.unlock();
						logger.debug("shared object released");
					}
				} catch ( InterruptedException e ) {
					fail("Unhandled exception: " + e);
					Thread.currentThread().interrupt();
				}
			}
		};
		concurrentThread.start();
		assertTrue(concurrentReady.await(1, TimeUnit.SECONDS));
		queue.enqueue(signal1, SimpleEventFactory.getInstance());
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}

}
