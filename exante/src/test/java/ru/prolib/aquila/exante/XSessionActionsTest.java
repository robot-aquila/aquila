package ru.prolib.aquila.exante;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.easymock.EasyMock.*;

import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import quickfix.SessionID;

public class XSessionActionsTest {
	private static final boolean dump_result_matrix = true;
	
	private IMocksControl control;
	private SessionID sidMock1, sidMock2, sidMock3;
	private XLogonAction logonMock1, logonMock2, logonMock3, logonMock4, logonMock5;
	private XLogoutAction logoutMock1, logoutMock2, logoutMock3, logoutMock4, logoutMock5;
	private Map<SessionID, List<XLogonAction>> logon_actions;
	private Map<SessionID, List<XLogoutAction>> logout_actions;
	private XSessionActions service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sidMock1 = control.createMock(SessionID.class);
		sidMock2 = control.createMock(SessionID.class);
		sidMock3 = control.createMock(SessionID.class);
		logonMock1 = control.createMock(XLogonAction.class);
		logonMock2 = control.createMock(XLogonAction.class);
		logonMock3 = control.createMock(XLogonAction.class);
		logonMock4 = control.createMock(XLogonAction.class);
		logonMock5 = control.createMock(XLogonAction.class);
		logoutMock1 = control.createMock(XLogoutAction.class);
		logoutMock2 = control.createMock(XLogoutAction.class);
		logoutMock3 = control.createMock(XLogoutAction.class);
		logoutMock4 = control.createMock(XLogoutAction.class);
		logoutMock5 = control.createMock(XLogoutAction.class);
		logon_actions = new HashMap<>();
		logout_actions = new HashMap<>();
		service = new XSessionActions(logon_actions, logout_actions);
	}
	
	@Test
	public void testAddLogonAction() {
		service.addLogonAction(sidMock1, logonMock1);
		service.addLogonAction(sidMock1, logonMock2);
		service.addLogonAction(sidMock2, logonMock3);
		
		Map<SessionID, List<XLogonAction>> expected = new HashMap<>();
		List<XLogonAction> list = new ArrayList<>();
		list.add(logonMock1);
		list.add(logonMock2);
		expected.put(sidMock1, list);
		list = new ArrayList<>();
		list.add(logonMock3);
		expected.put(sidMock2, list);
		assertEquals(expected, logon_actions);
	}
	
	@Test
	public void testAddLogoutAction() {
		service.addLogoutAction(sidMock2, logoutMock1);
		service.addLogoutAction(sidMock3, logoutMock2);
		service.addLogoutAction(sidMock3, logoutMock3);
		
		Map<SessionID, List<XLogoutAction>> expected = new HashMap<>();
		List<XLogoutAction> list = new ArrayList<>();
		list.add(logoutMock1);
		expected.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logoutMock2);
		list.add(logoutMock3);
		expected.put(sidMock3, list);
		assertEquals(expected, logout_actions);
	}
	
	@Test
	public void testOnLogon_SimpleRepeatable() {
		List<XLogonAction> list = new ArrayList<>();
		list.add(logonMock2);
		list.add(logonMock3);
		logon_actions.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logonMock1);
		logon_actions.put(sidMock3, list);
		expect(logonMock2.onLogon(sidMock2)).andReturn(false);
		expect(logonMock3.onLogon(sidMock2)).andReturn(false);
		control.replay();
		
		service.onLogon(sidMock2);
		
		control.verify();
		Map<SessionID, List<XLogonAction>> expected = new HashMap<>();
		list = new ArrayList<>();
		list.add(logonMock2);
		list.add(logonMock3);
		expected.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logonMock1);
		expected.put(sidMock3, list);
		assertEquals(expected, logon_actions);
	}
	
	@Test
	public void testOnLogon_RemoveAfterExecution() {
		List<XLogonAction> list = new ArrayList<>();
		list.add(logonMock2);
		list.add(logonMock3);
		logon_actions.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logonMock1);
		logon_actions.put(sidMock3, list);
		expect(logonMock2.onLogon(sidMock2)).andReturn(true);
		expect(logonMock3.onLogon(sidMock2)).andReturn(false);
		control.replay();
		
		service.onLogon(sidMock2);
		
		control.verify();
		Map<SessionID, List<XLogonAction>> expected = new HashMap<>();
		list = new ArrayList<>();
		list.add(logonMock3);
		expected.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logonMock1);
		expected.put(sidMock3, list);
		assertEquals(expected, logon_actions);
	}
	
	@Test
	public void testOnLogon_AddActionsOnTheFly() {
		List<XLogonAction> list = new ArrayList<>();
		list.add(logonMock2);
		list.add(logonMock3);
		list.add(logonMock4);
		logon_actions.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logonMock1);
		list.add(logonMock5);
		logon_actions.put(sidMock3, list);
		expect(logonMock2.onLogon(sidMock2)).andReturn(false);
		expect(logonMock3.onLogon(sidMock2)).andAnswer(new IAnswer<Boolean>() {
			@Override
			public Boolean answer() throws Throwable {
				service.addLogonAction(sidMock2, logonMock1);
				return true;
			}
		});
		expect(logonMock4.onLogon(sidMock2)).andReturn(false);
		control.replay();
		
		service.onLogon(sidMock2);
		
		control.verify();
		Map<SessionID, List<XLogonAction>> expected = new HashMap<>();
		list = new ArrayList<>();
		list.add(logonMock2);
		//list.add(logonMock3);
		list.add(logonMock4);
		list.add(logonMock1);
		expected.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logonMock1);
		list.add(logonMock5);
		expected.put(sidMock3, list);
		assertEquals(expected, logon_actions);
	}
	
	static class TestAction implements XLogonAction, XLogoutAction {
		private final BlockingQueue<String> target;
		private final int my_id;
		
		TestAction(BlockingQueue<String> target, int my_id) {
			this.target = target;
			this.my_id = my_id;
		}
		
		private void put(String suffix) {
			try {
				target.put(new StringBuilder()
						.append(Thread.currentThread().getName())
						.append(".")
						.append(my_id)
						.append(".")
						.append(suffix)
						.toString());
			} catch ( InterruptedException e ) {
				e.printStackTrace(System.err);
			}			
		}

		@Override
		public boolean onLogon(SessionID session_id) {
			put("LOGON");
			return false;
		}

		@Override
		public boolean onLogout(SessionID session_id) {
			put("LOGOUT");
			return false;
		}

	}
	
	static class CallOnLogon implements Runnable {
		private final SessionID session_id;
		private final XSessionActions service;
		
		CallOnLogon(SessionID session_id, XSessionActions service) {
			this.session_id = session_id;
			this.service = service;
		}

		@Override
		public void run() {
			service.onLogon(session_id);
		}
		
	}
	
	static class CallOnLogout implements Runnable {
		private final SessionID session_id;
		private final XSessionActions service;
		
		CallOnLogout(SessionID session_id, XSessionActions service) {
			this.session_id = session_id;
			this.service = service;
		}
		
		@Override
		public void run() {
			service.onLogout(session_id);
		}
		
	}
	
	static class ConcurrencyTestNode {
		private final String name;
		private final Runnable task;
		
		ConcurrencyTestNode(String name, Runnable task) {
			this.name = name;
			this.task = task;
		}
		
	}
	
	/**
	 * Run the test nodes sequentially in random order.
	 * <p>
	 * @param test_nodes - list of nodes to run
	 * @return list of nodes sorted by actual execution order
	 * @throws Throwable - an error occurred
	 */
	List<ConcurrencyTestNode> concurrencyTestPass(List<ConcurrencyTestNode> test_nodes) throws Throwable {
		List<ConcurrencyTestNode> nodes = new ArrayList<>(test_nodes);
		int num_nodes = nodes.size();
		CountDownLatch all_start = new CountDownLatch(num_nodes), all_finish = new CountDownLatch(num_nodes),
				go = new CountDownLatch(1);
		Collections.shuffle(nodes);
		for ( ConcurrencyTestNode node : nodes ) {
			Thread thread = new Thread(node.name) {
				@Override
				public void run() {
					all_start.countDown();
					try {
						if ( go.await(1, TimeUnit.SECONDS) ) {
							node.task.run();
							all_finish.countDown();
						}
					} catch ( InterruptedException e ) {
						e.printStackTrace(System.err);
					}
				}
			};
			thread.start();
		}
		all_start.await(1, TimeUnit.SECONDS);
		go.countDown();
		all_finish.await(1, TimeUnit.SECONDS);
		return nodes;
	}
	
	
	/**
	 * Test call sequence and determine node execution order.
	 * <p>
	 * @param actual - actual information of the calls
	 * @param total_nodes - total number of nodes
	 * @param total_actions - total number of actions
	 * @param expected_suffix - suffix of call string
	 * @return list of node indices according to execution order e.g. [0, 3, 1, 2, 4]
	 */
	List<Integer> concurrencyTestPass_ExecutionOrder(List<String> actual,
			int total_nodes,
			int total_actions,
			String expected_suffix)
	{
		int total_calls = total_nodes * total_actions;
		List<String> dummy = new ArrayList<>(actual);
		assertEquals(total_calls, dummy.size());
		List<Integer> execution_order = new ArrayList<>();
		for ( int n = 0; n < total_nodes; n ++ ) {
			String line = dummy.get(n * total_actions);
			int p = line.indexOf(".");
			assertTrue(p >= 0);
			int node_index = Integer.parseInt(line.substring(1, p));
			execution_order.add(node_index);
			String prefix = "N" + node_index + ".";
			for ( int a = 0; a < total_actions; a ++ ) {
				String expected = prefix + a + "." + expected_suffix;
				int action_abs_index = n * total_actions + a;
				assertEquals("At#" + action_abs_index, expected, dummy.get(action_abs_index));
			}
		}
		return execution_order;
	}
	
	@Test
	public void testOnLogon_Concurrency() throws Throwable {
		int total_nodes = 5;
		int total_actions = 5;
		
		LinkedBlockingQueue<String> actual = new LinkedBlockingQueue<>();
		for ( int i = 0; i < total_actions; i ++ ) {
			service.addLogonAction(sidMock1, new TestAction(actual, i));
		}
		List<ConcurrencyTestNode> nodes = new ArrayList<>();
		for ( int i = 0; i < total_nodes; i ++ ) {
			nodes.add(new ConcurrencyTestNode("N" + i, new CallOnLogon(sidMock1, service)));			
		}
		
		// x - how many times node was executed at this position
		// y - node index
		int result_matrix[][] = {
				{ 0, 0, 0, 0, 0 }, // node #0
				{ 0, 0, 0, 0, 0 }, // node #1
				{ 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0 }, // node #4
		};
		
		int total_passes = 5000;
		for ( int i = 0; i < total_passes; i ++ ) {
			concurrencyTestPass(nodes);
			List<String> dummy = new ArrayList<>();
			actual.drainTo(dummy);
			List<Integer> neo = concurrencyTestPass_ExecutionOrder(dummy, total_nodes, total_actions, "LOGON");
			for ( int call_pos = 0; call_pos < neo.size(); call_pos ++ ) {
				int node_index = neo.get(call_pos);
				result_matrix[node_index][call_pos] ++;
			}
		}
		
		// Dump matrix
		if ( dump_result_matrix ) System.out.println("Result matrix (onLogon)");
		for ( int node_index = 0; node_index < total_nodes; node_index ++ ) {
			if ( dump_result_matrix ) System.out.print("Node #" + node_index + ": ");
			int node_total_calls = 0;
			for ( int call_pos = 0; call_pos < total_nodes; call_pos ++ ) {
				int node_current_calls = result_matrix[node_index][call_pos];
				node_total_calls += node_current_calls;
				if ( dump_result_matrix ) System.out.print(node_current_calls + "\t ");
			}
			if ( dump_result_matrix ) System.out.println("");
			assertEquals("N#" + node_index, total_passes, node_total_calls);
		}
	}
	
	@Test
	public void testOnLogout_SimpleRepeatable() throws Throwable {
		List<XLogoutAction> list = new ArrayList<>();
		list.add(logoutMock2);
		list.add(logoutMock3);
		logout_actions.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logoutMock1);
		logout_actions.put(sidMock3, list);
		expect(logoutMock2.onLogout(sidMock2)).andReturn(false);
		expect(logoutMock3.onLogout(sidMock2)).andReturn(false);
		control.replay();
		
		service.onLogout(sidMock2);
		
		control.verify();
		Map<SessionID, List<XLogoutAction>> expected = new HashMap<>();
		list = new ArrayList<>();
		list.add(logoutMock2);
		list.add(logoutMock3);
		expected.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logoutMock1);
		expected.put(sidMock3, list);
		assertEquals(expected, logout_actions);
	}
	
	@Test
	public void testOnLogout_RemoveAfterExecution() throws Throwable {
		List<XLogoutAction> list = new ArrayList<>();
		list.add(logoutMock2);
		list.add(logoutMock3);
		logout_actions.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logoutMock1);
		logout_actions.put(sidMock3, list);
		expect(logoutMock2.onLogout(sidMock2)).andReturn(true);
		expect(logoutMock3.onLogout(sidMock2)).andReturn(false);
		control.replay();
		
		service.onLogout(sidMock2);
		
		control.verify();
		Map<SessionID, List<XLogoutAction>> expected = new HashMap<>();
		list = new ArrayList<>();
		list.add(logoutMock3);
		expected.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logoutMock1);
		expected.put(sidMock3, list);
		assertEquals(expected, logout_actions);
	}
	
	@Test
	public void testOnLogout_AddActionsOfTheFly() throws Throwable {
		List<XLogoutAction> list = new ArrayList<>();
		list.add(logoutMock2);
		list.add(logoutMock3);
		list.add(logoutMock4);
		logout_actions.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logoutMock1);
		list.add(logoutMock5);
		logout_actions.put(sidMock3, list);
		expect(logoutMock2.onLogout(sidMock2)).andReturn(false);
		expect(logoutMock3.onLogout(sidMock2)).andAnswer(new IAnswer<Boolean>() {
			@Override
			public Boolean answer() throws Throwable {
				service.addLogoutAction(sidMock2, logoutMock1);
				return true;
			}
		});
		expect(logoutMock4.onLogout(sidMock2)).andReturn(false);
		control.replay();
		
		service.onLogout(sidMock2);
		
		control.verify();
		Map<SessionID, List<XLogoutAction>> expected = new HashMap<>();
		list = new ArrayList<>();
		list.add(logoutMock2);
		//list.add(logoutMock3);
		list.add(logoutMock4);
		list.add(logoutMock1);
		expected.put(sidMock2, list);
		list = new ArrayList<>();
		list.add(logoutMock1);
		list.add(logoutMock5);
		expected.put(sidMock3, list);
		assertEquals(expected, logout_actions);
	}
	
	@Test
	public void testOnLogout_Concurrency() throws Throwable {
		int total_nodes = 5;
		int total_actions = 5;
		
		LinkedBlockingQueue<String> actual = new LinkedBlockingQueue<>();
		for ( int i = 0; i < total_actions; i ++ ) {
			service.addLogoutAction(sidMock1, new TestAction(actual, i));
		}
		List<ConcurrencyTestNode> nodes = new ArrayList<>();
		for ( int i = 0; i < total_nodes; i ++ ) {
			nodes.add(new ConcurrencyTestNode("N" + i, new CallOnLogout(sidMock1, service)));			
		}
		
		// x - how many times node was executed at this position
		// y - node index
		int result_matrix[][] = {
				{ 0, 0, 0, 0, 0 }, // node #0
				{ 0, 0, 0, 0, 0 }, // node #1
				{ 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0 }, // node #4
		};
		
		int total_passes = 5000;
		for ( int i = 0; i < total_passes; i ++ ) {
			concurrencyTestPass(nodes);
			List<String> dummy = new ArrayList<>();
			actual.drainTo(dummy);
			List<Integer> neo = concurrencyTestPass_ExecutionOrder(dummy, total_nodes, total_actions, "LOGOUT");
			for ( int call_pos = 0; call_pos < neo.size(); call_pos ++ ) {
				int node_index = neo.get(call_pos);
				result_matrix[node_index][call_pos] ++;
			}
		}
		
		// Dump matrix
		if ( dump_result_matrix ) System.out.println("Result matrix (onLogout)");
		for ( int node_index = 0; node_index < total_nodes; node_index ++ ) {
			if ( dump_result_matrix ) System.out.print("Node #" + node_index + ": ");
			int node_total_calls = 0;
			for ( int call_pos = 0; call_pos < total_nodes; call_pos ++ ) {
				int node_current_calls = result_matrix[node_index][call_pos];
				node_total_calls += node_current_calls;
				if ( dump_result_matrix ) System.out.print(node_current_calls + "\t ");
			}
			if ( dump_result_matrix ) System.out.println("");
			assertEquals("N#" + node_index, total_passes, node_total_calls);
		}
	}

}
