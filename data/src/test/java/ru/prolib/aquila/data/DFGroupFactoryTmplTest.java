package ru.prolib.aquila.data;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;

public class DFGroupFactoryTmplTest {
	private Map<MDLevel, DFSubscrState> template;
	private DFGroupFactoryTmpl<String, MDLevel> service;

	@Before
	public void setUp() throws Exception {
		template = new LinkedHashMap<>();
		template.put(MDLevel.L0, new DFSubscrState(DFSubscrStatus.SUBSCR));
		template.put(MDLevel.L1, new DFSubscrState(DFSubscrStatus.PENDING_SUBSCR));
		template.put(MDLevel.L2, new DFSubscrState(DFSubscrStatus.NOT_SUBSCR));
		service = new DFGroupFactoryTmpl<>(template);
	}
	
	@Test
	public void testProduce() {
		DFGroup<String, MDLevel> x1 = service.produce("foo");
		assertEquals(DFSubscrStatus.SUBSCR, x1.getFeedStatus(MDLevel.L0));
		assertEquals(DFSubscrStatus.PENDING_SUBSCR, x1.getFeedStatus(MDLevel.L1));
		assertEquals(DFSubscrStatus.NOT_SUBSCR, x1.getFeedStatus(MDLevel.L2));
		
		template.put(MDLevel.L0, new DFSubscrState(DFSubscrStatus.PENDING_UNSUBSCR));
		
		// Produced instance uses copy of template -> shouldn't changed
		assertEquals(DFSubscrStatus.SUBSCR, x1.getFeedStatus(MDLevel.L0));
		assertEquals(DFSubscrStatus.PENDING_SUBSCR, x1.getFeedStatus(MDLevel.L1));
		assertEquals(DFSubscrStatus.NOT_SUBSCR, x1.getFeedStatus(MDLevel.L2));
	}

}
