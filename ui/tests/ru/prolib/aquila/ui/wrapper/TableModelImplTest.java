package ru.prolib.aquila.ui.wrapper;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.easymock.IMocksControl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.GInteger;
import ru.prolib.aquila.core.data.GString;
import ru.prolib.aquila.core.data.row.RowAdapter;
/**
 * $Id: TableModelTest.java 578 2013-03-14 23:37:31Z huan.kaktus $
 */
public class TableModelImplTest {
		
	private static IMocksControl control;
	@SuppressWarnings("rawtypes")
	private G rowReader;
	private G<?> getter;
	private TableModelImpl tm;
		
	@Before
	public void setUp() throws Exception {		
		control = createStrictControl();
		rowReader = control.createMock(G.class);
		getter = control.createMock(G.class);
		tm = new TableModelImpl(rowReader);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testConstructor() {
		assertEquals(rowReader, tm.getRowReader());
	}
	
	@Test
	public void testOnEvent_RowInserted() {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType evtType = control.createMock(EventType.class);

		tm.setOnRowAvailableListener(new DataSourceEventTranslator(
				dispatcher, evtType));
		
		Event source = control.createMock(Event.class);
		Event evt = new EventTranslatorEvent(evtType, source);
		
		tm.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				assertEquals(TableModelEvent.INSERT, e.getType());
				assertEquals(0, e.getFirstRow());
				assertEquals(0, e.getLastRow());
			}
			
		});
		TestRow row = new TestRow(1, "foo");
		expect(rowReader.get(source)).andReturn(row);
		control.replay();
		tm.onEvent(evt);
		
		List<RowAdapter> rows = tm.getRows();
		assertEquals(1, rows.size());
		RowAdapter r = (RowAdapter) rows.get(0);
		assertEquals(row, r.getSource());
		assertEquals(tm.getGetters(), r.getAdapters());
		
		List<Object> rowIndex = tm.getRowIndex();
		assertEquals(1, rowIndex.size());
		assertEquals(row, rowIndex.get(0));
	}
	
	@Test
	public void testOnEvent_RowUpdated() {
		TestRow row = new TestRow(1, "foo");
		tm.insertRow(row);
		
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		
		tm.setOnRowAvailableListener(new DataSourceEventTranslator(
				dispatcher, control.createMock(EventType.class)));
		
		EventType evtType = control.createMock(EventType.class);
		tm.setOnRowChangedListener(new DataSourceEventTranslator(
				dispatcher, evtType));
		
		Event source = control.createMock(Event.class);
		Event evt = new EventTranslatorEvent(evtType, source);
		
		tm.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				assertEquals(TableModelEvent.UPDATE, e.getType());
				assertEquals(0, e.getFirstRow());
				assertEquals(0, e.getLastRow());
			}
			
		});
		
		expect(rowReader.get(source)).andReturn(row);
		control.replay();
		tm.onEvent(evt);
	}
	
	@Test
	public void testGetColumnName() throws Exception {
		tm.addColumn(new TableColumnWrp("id", getter, "ColId"));
		tm.addColumn(new TableColumnWrp("name", getter, "ColName"));
		
		assertEquals("ColId", tm.getColumnName(0));
		assertEquals("ColName", tm.getColumnName(1));
	}
	
	@Test
	public void testGetValueAt_RowNotFound() {
		assertNull(tm.getValueAt(1,3));
	}
	
	@Test
	public void testGetValueAt_Ok() throws Exception {
		GInteger idGetter = new GInteger() {
			
			@Override
			public Integer get(Object row) {
				TestRow r = (TestRow) row;
				return super.get(r.getId());
			}
		};
		GString nameGetter = new GString() {
			
			@Override
			public String get(Object row) {
				TestRow r = (TestRow) row;
				return super.get(r.getName());
			}
		};
		tm.addColumn(new TableColumnWrp("id", idGetter));
		tm.addColumn(new TableColumnWrp("name", nameGetter));		
		
		
		tm.insertRow(new TestRow(1, "John Doe"));
		tm.insertRow(new TestRow(3, "John Brown"));
		
		assertEquals(3, tm.getValueAt(1, 0));
		assertEquals("John Brown", tm.getValueAt(1,1));
		assertEquals(1, tm.getValueAt(0, 0));
		assertEquals("John Doe", tm.getValueAt(0,1));
	}
	
	@Test
	public void testGetColumnCount() throws Exception {
		assertEquals(0, tm.getColumnCount());
		TableColumnWrp col = new TableColumnWrp("MY_COL", getter);
		tm.addColumn(col);
		
		TableColumnWrp col2 = new TableColumnWrp("HIS_COL", getter);
		tm.addColumn(col2);
		
		assertEquals(2, tm.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() throws TableColumnAlreadyExistsException {
		assertEquals(0, tm.getRowCount());
		TableColumnWrp col = new TableColumnWrp("MY_COL", getter);
		tm.addColumn(col);
		tm.insertRow(new Object());
		tm.insertRow(new Object());
		
		assertEquals(2, tm.getRowCount());
	}
	
	@Test
	public void testSetOnRowAvailableListener() {
		DataSourceEventTranslator listener = control.createMock(
				DataSourceEventTranslator.class);
		tm.setOnRowAvailableListener(listener);
		assertEquals(listener, tm.getOnRowAvailableListener());
	}
	
	@Test
	public void testSetOnRowChangedListener() {
		DataSourceEventTranslator listener = control.createMock(
				DataSourceEventTranslator.class);
		tm.setOnRowChangedListener(listener);
		assertEquals(listener, tm.getOnRowChangedListener());
	}
	
	@Test
	public void testStart() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		
		EventType onAvailableEvt = control.createMock(EventType.class);
		EventType onChangedEvt = control.createMock(EventType.class);
		
		DataSourceEventTranslator onAvailableListener = new DataSourceEventTranslator(
				dispatcher, onAvailableEvt);
		tm.setOnRowAvailableListener(onAvailableListener);
		
		DataSourceEventTranslator onChangedListener = new DataSourceEventTranslator(
				dispatcher, onChangedEvt);
		tm.setOnRowChangedListener(onChangedListener);
		
		onAvailableEvt.addListener(tm);
		onChangedEvt.addListener(tm);
		control.replay();
		tm.start();
		control.verify();
	}
	
	@Test
	public void testStop() throws StarterException {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		
		EventType onAvailableEvt = control.createMock(EventType.class);
		EventType onChangedEvt = control.createMock(EventType.class);
		
		DataSourceEventTranslator onAvailableListener = new DataSourceEventTranslator(
				dispatcher, onAvailableEvt);
		tm.setOnRowAvailableListener(onAvailableListener);
		
		DataSourceEventTranslator onChangedListener = new DataSourceEventTranslator(
				dispatcher, onChangedEvt);
		tm.setOnRowChangedListener(onChangedListener);
		
		onAvailableEvt.removeListener(tm);
		onChangedEvt.removeListener(tm);
		control.replay();
		tm.stop();
		control.verify();
	}

	//@Test
	public void testAddColum_Ok() throws Exception {
		TableColumnWrp col = new TableColumnWrp("MY_COL", getter);
		tm.addColumn(col);
		assertTrue(tm.isColumnExists("MY_COL"));
		assertEquals(col, tm.getColumn("MY_COL"));
		
		G<?> getter2 = control.createMock(G.class);
		tm.addColumn(new TableColumnWrp("COL_2", getter2));
		List<String> index = tm.getIndex();
		assertEquals(2, index.size());
		assertEquals("MY_COL", index.get(0));
		assertEquals("COL_2", index.get(1));
		
		assertEquals(getter, tm.getGetters().get("MY_COL"));
		assertEquals(getter2, tm.getGetters().get("COL_2"));
	}
	
	@Test(expected=TableColumnAlreadyExistsException.class)
	public void testAddColumn_AlreadyExistsThrows() throws Exception {
		TableColumnWrp col = new TableColumnWrp("MY_COL", getter);
		tm.addColumn(col);
		tm.addColumn(col);
	}
	
	@Test(expected=TableColumnNotExistsException.class)
	public void testGetColumn_ByNameNotExistsThrows() throws Exception {
		tm.getColumn("MY_COL");
	}
	
	@Test
	public void testGetColumn_ByIndexOk() throws Exception {
		TableColumnWrp col = new TableColumnWrp("MY_COL", getter);
		tm.addColumn(col);		
		TableColumnWrp col2 = new TableColumnWrp("HIS_COL", getter);
		tm.addColumn(col2);
		
		assertEquals(col, tm.getColumn(0));
		assertEquals(col2, tm.getColumn(1));
	}
	
	@Test(expected=TableColumnNotExistsException.class)
	public void testGetColumn_ByIndexTgrowsNotExists() throws Exception {
		tm.getColumn(2);
	}
	
	private class TestRow {
		private Integer id;
		private String name;		
		
		public TestRow(Integer id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public Integer getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
	}

}
