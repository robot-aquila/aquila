package ru.prolib.aquila.core.sm;

import java.time.Instant;

import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.utils.Variant;

public class SMTriggerOnTimerTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IMocksControl control;
	private SMTriggerRegistry registryMock1, registryMock2;
	private SMInput inMock1, inMock2;
	private Scheduler schedulerMock1, schedulerMock2;
	private Instant time1, time2;
	private TaskHandler thMock1, thMock2;
	private SMTriggerOnTimer service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		registryMock1 = control.createMock(SMTriggerRegistry.class);
		registryMock2 = control.createMock(SMTriggerRegistry.class);
		inMock1 = control.createMock(SMInput.class);
		inMock2 = control.createMock(SMInput.class);
		schedulerMock1 = control.createMock(Scheduler.class);
		schedulerMock2 = control.createMock(Scheduler.class);
		thMock1 = control.createMock(TaskHandler.class);
		thMock2 = control.createMock(TaskHandler.class);
		time1 = T("2018-12-20T15:00:00Z");
		time2 = T("2018-11-03T23:54:00Z");
		service = new SMTriggerOnTimer(schedulerMock1, time1, inMock1);
	}
	
	@Test
	public void testCtor() {
		assertSame(schedulerMock1, service.getScheduler());
		assertSame(time1, service.getTime());
		assertSame(inMock1, service.getInput());
		assertNull(service.getProxy());
		assertNull(service.getTaskHandler());
	}
	
	@Test
	public void testRun_DefaultInput() throws Exception {
		service = new SMTriggerOnTimer(schedulerMock1, time1);
		expect(schedulerMock1.schedule(service, time1)).andReturn(thMock1);
		registryMock1.input(time1);
		control.replay();
		service.activate(registryMock1);
		
		service.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_SpecifiedInput() throws Exception {
		expect(schedulerMock1.schedule(service, time1)).andReturn(thMock1);
		registryMock1.input(inMock1, time1);
		control.replay();
		service.activate(registryMock1);
		
		service.run();
		
		control.verify();
	}
	
	@Test
	public void testRun_SkipIfInactive() throws Exception {
		control.replay();
		
		service.run();
		
		control.verify();
	}
	
	@Test
	public void testActivate() throws Exception {
		expect(schedulerMock1.schedule(service, time1)).andReturn(thMock1);
		control.replay();
		
		service.activate(registryMock1);
		
		control.verify();
	}
	
	@Test
	public void testActivate_SkipIfActive() throws Exception {
		expect(schedulerMock1.schedule(service, time1)).andReturn(thMock1);
		control.replay();
		service.activate(registryMock1);
		control.resetToStrict();
		control.replay();
		
		service.activate(registryMock1);
		
		control.verify();
	}
	
	@Test
	public void testDeactivate() throws Exception {
		expect(schedulerMock1.schedule(service, time1)).andReturn(thMock1);
		control.replay();
		service.activate(registryMock1);
		control.resetToStrict();
		expect(thMock1.cancel()).andReturn(false);
		control.replay();
		
		service.deactivate();
		
		control.verify();
	}

	@Test
	public void testDeactivate_SkipIfInactive() throws Exception {
		control.replay();
		
		service.deactivate();
		
		control.verify();
	}
	
	@Test
	public void testIsEqualTo() {
		Variant<Scheduler> vSCH = new Variant<>(schedulerMock1, schedulerMock2);
		Variant<Instant> vTM = new Variant<>(vSCH, time1, time2);
		Variant<SMInput> vIN = new Variant<>(vTM, inMock1, inMock2);
		Variant<SMTriggerRegistry> vREG = new Variant<>(vIN, registryMock1, registryMock2);
		Variant<?> iterator = vREG;
		int foundCnt = 0;
		SMTriggerOnTimer x, found = null;
		expect(schedulerMock1.schedule(anyObject(), anyObject())).andStubReturn(thMock1);
		expect(schedulerMock2.schedule(anyObject(), anyObject())).andStubReturn(thMock2);
		control.replay();
		service.activate(registryMock1);
		do {
			x = new SMTriggerOnTimer(vSCH.get(), vTM.get(), vIN.get());
			x.activate(vREG.get());
			if ( service.isEqualTo(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(schedulerMock1, found.getScheduler());
		assertSame(time1, found.getTime());
		assertSame(inMock1, found.getInput());
		assertSame(registryMock1, found.getProxy());
		assertSame(thMock1, found.getTaskHandler());
	}
	
	@Test
	public void testToString() {
		expect(schedulerMock1.schedule(anyObject(), anyObject())).andStubReturn(thMock1);
		control.replay();
		service.activate(registryMock1);
		String expected = new StringBuilder()
				.append("SMTriggerOnTimer[")
				.append("scheduler=").append(schedulerMock1).append(",")
				.append("time=").append(time1).append(",")
				.append("handler=").append(thMock1).append(",")
				.append("input=").append(inMock1).append(",")
				.append("proxy=").append(registryMock1).append(",")
				.append("activated=true,closed=false")
				.append("]")
				.toString();

		assertEquals(expected, service.toString());
	}

}
