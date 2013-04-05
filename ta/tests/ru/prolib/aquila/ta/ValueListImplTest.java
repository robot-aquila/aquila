package ru.prolib.aquila.ta;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertSame;

import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class ValueListImplTest {
	IMocksControl control;
	ValueListImpl list;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		list = new ValueListImpl();
	}
	
	@Test
	public void testAddValue_Ok() throws Exception {
		Value<?> val1 = new TestValue<Double>("foo");
		Value<?> val2 = new TestValue<Integer>("bar");
		list.addValue(val1);
		list.addValue(val2);
		
		assertSame(val1, list.getValue("foo"));
		assertSame(val2, list.getValue("bar"));
	}
	
	@Test (expected=ValueExistsException.class)
	public void testAddValue_ThrowsExists() throws Exception {
		Value<?> val1 = new TestValue<Double>("foo");
		Value<?> val2 = new TestValue<Integer>("foo");
		list.addValue(val1);
		list.addValue(val2);
	}
	
	@Test (expected=ValueNotExistsException.class)
	public void testGetValue_ThrowsNotExists() throws Exception {
		list.getValue("foobar");
	}

	@Test
	public void testUpdate_Ok() throws Exception {
		Value<?> val1 = control.createMock(Value.class);
		Value<?> val2 = control.createMock(Value.class);
		Value<?> val3 = control.createMock(Value.class);
		
		list.addValue("foo",  val1);
		list.addValue("zulu", val3);
		list.addValue("bar",  val2);
		
		val1.update();
		val3.update();
		val2.update();
		control.replay();
		
		list.update();
		
		control.verify();
	}
	
	@Test
	public void testNotifyAfterUpdate() throws Exception {
		Observer o = control.createMock(Observer.class);
		o.update(list, null);
		o.update(list, null);
		control.replay();
		
		list.addObserver(o);
		list.update();
		list.update();
		list.deleteObserver(o);
		list.update();
		
		control.verify();
	}
	
}
