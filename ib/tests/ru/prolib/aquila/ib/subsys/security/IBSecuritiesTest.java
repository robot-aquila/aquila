package ru.prolib.aquila.ib.subsys.security;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurities;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityNotExistsException;
import ru.prolib.aquila.ib.subsys.security.IBSecurities;
import ru.prolib.aquila.ib.subsys.security.IBSecurityHandler;
import ru.prolib.aquila.ib.subsys.security.IBSecurityHandlerFactory;
import ru.prolib.aquila.ib.subsys.security.IBSecurityStatus;

/**
 * 2012-11-24<br>
 * $Id: IBSecuritiesTest.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class IBSecuritiesTest {
	private static IMocksControl control;
	private static EditableSecurities storage;
	private static EditableSecurity security;
	private static IBSecurityHandlerFactory factory;
	private static IBSecurityHandler handler;
	private static SecurityDescriptor descr;
	private IBSecurities securities;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		storage = control.createMock(EditableSecurities.class);
		security = control.createMock(EditableSecurity.class);
		factory = control.createMock(IBSecurityHandlerFactory.class);
		handler = control.createMock(IBSecurityHandler.class);
		descr = new SecurityDescriptor("AAPL", "SMART", "USD", SecurityType.STK);
	}

	@Before
	public void setUp() throws Exception {
		securities = new IBSecurities(storage, factory);
		control.resetToStrict();
	}
	
	/**
	 * Вспомогательный интерфейс для тестирования методов получения инструмента.
	 * <p>
	 * 2012-11-24<br>
	 * $Id: IBSecuritiesTest.java 490 2013-02-05 19:42:02Z whirlwind $
	 */
	private static interface GetSecurityAction {
		public Security execute() throws Exception;
	}
	
	/**
	 * Вспомогательный интерфейс для тестирования наличия инструмента.
	 * <p>
	 * 2012-11-24<br>
	 * $Id: IBSecuritiesTest.java 490 2013-02-05 19:42:02Z whirlwind $ 
	 */
	private static interface IsSecurityExistsAction{
		public boolean execute() throws Exception;
	};
	
	/**
	 * Вспомогательный метод для тестирования метода получения инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющая соответствующий вызов
	 */
	private void testGetSecurity_Done(GetSecurityAction action)
			throws Exception
	{
		expect(handler.getSecurityStatus()).andReturn(IBSecurityStatus.DONE);
		expect(storage.getSecurity(eq(descr))).andReturn(security);
		control.replay();
		Security actual = action.execute();
		control.verify();
		assertSame(security, actual);
		assertSame(handler, securities.map.get(descr));		
	}
	
	/**
	 * Вспомогательный метод для тестирования наличия инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющего вызов
	 * @throws Exception
	 */
	private void testIsSecurityExists_Done(IsSecurityExistsAction action)
			throws Exception
	{
		expect(handler.getSecurityStatus()).andReturn(IBSecurityStatus.DONE);
		control.replay();
		boolean actual = action.execute();
		control.verify();
		assertTrue(actual);
		assertSame(handler, securities.map.get(descr));
	}
	
	/**
	 * Вспомогательный метод для тестирования метода получения инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющая соответствующий вызов
	 */
	private void testGetSecurity_Nfnd(GetSecurityAction action)
			throws Exception
	{
		expect(handler.getSecurityStatus()).andReturn(IBSecurityStatus.NFND);
		control.replay();
		try {
			action.execute();
			fail("Expected exception: SecurityNotExistsException");
		} catch ( SecurityNotExistsException e ) { }
		control.verify();
		assertSame(handler, securities.map.get(descr));
	}
	
	/**
	 * Вспомогательный метод для тестирования наличия инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющего вызов
	 * @throws Exception
	 */
	private void testIsSecurityExists_Nfnd(IsSecurityExistsAction action)
			throws Exception
	{
		expect(handler.getSecurityStatus()).andReturn(IBSecurityStatus.NFND);
		control.replay();
		boolean actual = action.execute();
		control.verify();
		assertFalse(actual);
		assertSame(handler, securities.map.get(descr));
	}
	
	/**
	 * Вспомогательный метод для тестирования метода получения инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющая вызов
	 * @throws Exception
	 */
	private void testGetSecurity_NewHandler_Done(GetSecurityAction action)
			throws Exception
	{
		expect(factory.createHandler(eq(descr))).andReturn(handler);
		handler.start();
		testGetSecurity_Done(action);
	}
	
	/**
	 * Вспомогательный метод для тестирования наличия инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющая вызов
	 * @throws Exception
	 */
	private void
		testIsSecurityExists_NewHandler_Done(IsSecurityExistsAction action)
			throws Exception
	{
		expect(factory.createHandler(eq(descr))).andReturn(handler);
		handler.start();
		testIsSecurityExists_Done(action);
	}
	
	/**
	 * Вспомогательный метод для тестирования метода получения инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющая вызов
	 * @throws Exception
	 */
	private void testGetSecurity_NewHandler_Nfnd(GetSecurityAction action)
			throws Exception
	{
		expect(factory.createHandler(eq(descr))).andReturn(handler);
		handler.start();
		testGetSecurity_Nfnd(action);
	}
	
	/**
	 * Вспомогательный метод для тестирования наличия инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющего вызов
	 * @throws Exception
	 */
	private void
		testIsSecurityExists_NewHandler_Nfnd(IsSecurityExistsAction action)
			throws Exception
	{
		expect(factory.createHandler(eq(descr))).andReturn(handler);
		handler.start();
		testIsSecurityExists_Nfnd(action);
	}

	/**
	 * Вспомогательный метод для тестирования метода получения инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющая вызов
	 * @throws Exception
	 */
	private void testGetSecurity_HasHandler_Done(GetSecurityAction action)
			throws Exception
	{
		securities.map.put(descr, handler);
		testGetSecurity_Done(action);
	}
	
	/**
	 * Вспомогательный метод для тестирования наличия инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющего вызов
	 * @throws Exception
	 */
	private void
		testIsSecurityExists_HasHandler_Done(IsSecurityExistsAction action)
			throws Exception
	{
		securities.map.put(descr, handler);
		testIsSecurityExists_Done(action);
	}
	
	/**
	 * Вспомогательный метод для тестирования метода получения инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющая вызов
	 * @throws Exception
	 */
	private void testGetSecurity_HasHandler_Nfnd(GetSecurityAction action)
			throws Exception
	{
		securities.map.put(descr, handler);
		testGetSecurity_Nfnd(action);
	}
	
	/**
	 * Вспомогательный метод для тестирования наличия инструмента.
	 * <p>
	 * @param action обертка метода, осуществляющего вызов
	 * @throws Exception
	 */
	private void
		testIsSecurityExists_HasHandler_Nfnd(IsSecurityExistsAction action)
			throws Exception
	{
		securities.map.put(descr, handler);
		testIsSecurityExists_Nfnd(action);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(storage, securities.getStorageSecurities());
		assertSame(factory, securities.getSecurityHandlerFactory());
	}
	
	@Test
	public void testOnSecurityAvailable() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(storage.OnSecurityAvailable()).andReturn(type);
		control.replay();
		assertSame(type, securities.OnSecurityAvailable());
		control.verify();
	}
	
	@Test
	public void testGetSecurities() throws Exception {
		List<Security> list = new Vector<Security>();
		expect(storage.getSecurities()).andReturn(list);
		control.replay();
		assertSame(list, securities.getSecurities());
		control.verify();
	}
	
	@Test
	public void testGetSecurity() throws Exception {
		GetSecurityAction action = new GetSecurityAction() {
			@Override public Security execute() throws Exception {
				return securities.getSecurity(descr); } };
	
		setUp(); testGetSecurity_NewHandler_Done(action);
		setUp(); testGetSecurity_HasHandler_Done(action);
		setUp(); testGetSecurity_NewHandler_Nfnd(action);
		setUp(); testGetSecurity_HasHandler_Nfnd(action);
	}
	
	@Test
	public void testIsSecurityExists() throws Exception {
		IsSecurityExistsAction action = new IsSecurityExistsAction() {
			@Override public boolean execute() {
				return securities.isSecurityExists(descr); } };

		setUp(); testIsSecurityExists_NewHandler_Done(action);
		setUp(); testIsSecurityExists_NewHandler_Nfnd(action);
		setUp(); testIsSecurityExists_HasHandler_Done(action);
		setUp(); testIsSecurityExists_HasHandler_Nfnd(action);
	}
	
	@Test
	public void testFireSecurityAvailableEvent() throws Exception {
		storage.fireSecurityAvailableEvent(same(security));
		control.replay();
		securities.fireSecurityAvailableEvent(security);
		control.verify();
	}
	
	@Test
	public void testGetEditableSecurity() throws Exception {
		expect(storage.getEditableSecurity(same(descr))).andReturn(security);
		control.replay();
		assertSame(security, securities.getEditableSecurity(descr));
		control.verify();
	}
	
	@Test
	public void testOnSecurityChanged() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(storage.OnSecurityChanged()).andReturn(type);
		control.replay();
		assertSame(type, securities.OnSecurityChanged());
		control.verify();
	}

	@Test
	public void testOnSecurityTrade() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(storage.OnSecurityTrade()).andReturn(type);
		control.replay();
		assertSame(type, securities.OnSecurityTrade());
		control.verify();
	}

	@Test
	public void testGetSecuritiesCount() throws Exception {
		expect(storage.getSecuritiesCount()).andReturn(345);
		control.replay();
		assertEquals(345, securities.getSecuritiesCount());
		control.verify();
	}
	
	@Test
	public void testCreateSecurity() throws Exception {
		EditableTerminal terminal = control.createMock(EditableTerminal.class);
		expect(storage.createSecurity(same(terminal), same(descr)))
			.andReturn(security);
		control.replay();
		
		assertSame(security, securities.createSecurity(terminal, descr));
		
		control.verify();
	}

}
