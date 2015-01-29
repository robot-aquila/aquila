package ru.prolib.aquila.ui.wrapper;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.row.RowAdapter;

/**
 * $Id$
 */
public class TableImplTest {

	private static IMocksControl control;
	private static EventSystem eventSystem;
	private static EventQueue queue;
	private EventType onRowSelected;
	private EventDispatcher dispatcher;
	private TableModel model;
	private TableImpl tb;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeCLass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();		
		
		eventSystem = new EventSystemImpl();
		queue = eventSystem.getEventQueue();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dispatcher = eventSystem.createEventDispatcher();
		onRowSelected = dispatcher.createType();
		model = new TableModelImpl(control.createMock(G.class));
		
		tb = new TableImpl(model, dispatcher, onRowSelected);
		queue.start();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		queue.stop();
		assertTrue(queue.join(1000));
	}
	
	@Test
	public void testGetSelectedRows() throws Exception {
		model.addColumn(new TableColumnWrp("COL_1", control.createMock(G.class)));
		model.addColumn(new TableColumnWrp("COL_2", control.createMock(G.class)));
		
		TestRow[] rows = {
			new TestRow(1, "Row_1"),
			new TestRow(32, "Row_2"),
			new TestRow(15, "Row_3")
		};
		for(int i = 0; i < rows.length; i++) {
			((TableModelImpl) model).insertRow(rows[i]);
		}
		JTable underlayed = control.createMock(JTable.class);
		tb.setUnderlayed(underlayed);
		
		int[] sr = {0, 2};
		expect(underlayed.getSelectedRows()).andReturn(sr);
		control.replay();
		
		List<RowAdapter> res = tb.getSelectedRows();
		
		control.verify();
		assertEquals(2, res.size());
		assertEquals(rows[0], res.get(0).getSource());
		assertEquals(rows[2], res.get(1).getSource());
	}
	
	@Test
	public void testStart() throws Exception {
		TableColumnWrp col1 = new TableColumnWrp(
				"COL_1", control.createMock(G.class), 250) ;
		TableColumnWrp col2 = new TableColumnWrp(
				"COL_2", control.createMock(G.class),180) ;
		model.addColumn(col1);
		model.addColumn(col2);
		
		EventType onRowAvailable = control.createMock(EventType.class);
		EventType onRowChanged = control.createMock(EventType.class);
		
		model.setOnRowAvailableListener(
				new DataSourceEventTranslator(dispatcher, onRowAvailable));
		model.setOnRowChangedListener(
				new DataSourceEventTranslator(dispatcher, onRowChanged));
		
		onRowAvailable.addListener(model);
		onRowChanged.addListener(model);
		
		// устанавливаем необходимое для проверки прикрепления слушателя селекта строк		
		final Event evtExpected = new EventImpl(onRowSelected);
		tb.OnRowSelected().addListener(new EventListener() {
			@Override
			public void onEvent(Event e) {
				assertEquals(evtExpected, e);	
			}			
		});
		
		control.replay();
		tb.start();
		control.verify();
		
		IsInstanceOf.instanceOf(JTable.class).matches(tb.getUnderlayed());
		JTable underlayed = tb.getUnderlayed();
		assertEquals(col1.getUnderlayed(), underlayed.getColumnModel().getColumn(0));
		assertEquals(250, underlayed.getColumnModel().getColumn(0).getPreferredWidth());
		assertEquals(col2.getUnderlayed(), underlayed.getColumnModel().getColumn(1));
		assertEquals(180, underlayed.getColumnModel().getColumn(1).getPreferredWidth());
		
		// проверяем слушателя селекта строк
		
		DefaultListSelectionModel sm =  (DefaultListSelectionModel) (tb.getUnderlayed().getSelectionModel());
		ListSelectionListener[] lsls = sm.getListeners(ListSelectionListener.class);
		assertEquals(2, lsls.length);
		lsls[1].valueChanged(new ListSelectionEvent(new Object(), 0, 0, false));
	}

	@Test
	public void testConstructor() {
		assertEquals(model, tb.getModel());
		assertEquals(dispatcher, tb.getDispatcher());
		assertEquals(onRowSelected, tb.OnRowSelected());
	}
	
	private class TestRow {
		private Integer id;
		private String name;		
		
		public TestRow(Integer id, String name) {
			this.id = id;
			this.name = name;
		}
	}

}
