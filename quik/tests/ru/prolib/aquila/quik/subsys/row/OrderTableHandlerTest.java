package ru.prolib.aquila.quik.subsys.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.row.Spec;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.QUIKConfigImpl;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

public class OrderTableHandlerTest {
	private static SimpleDateFormat format;
	private static Map<String, G<?>> elementAdapters;
	private static QUIKServiceLocator locator;
	private IMocksControl control;
	private Validator headerValidator;
	private OrderTable orderTable;
	private DDETable ddeTable;
	private OrderTableHandler handler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		locator = new QUIKServiceLocator(new TerminalDecorator());
		QUIKConfigImpl config = new QUIKConfigImpl();
		config.dateFormat = "yyyy-MM-dd";
		config.timeFormat = "HH:mm:ss";
		locator.setConfig(config);
		elementAdapters = new RowAdapters(locator).createOrderAdapters();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		headerValidator = control.createMock(Validator.class);
		orderTable = control.createMock(OrderTable.class);
		handler = new OrderTableHandler(headerValidator,
				elementAdapters, orderTable);
	}
	
	@Test (expected=DDEException.class)
	public void testHandle_ThrowsIfItemInvalid() throws Exception {
		Object cells[] = { "foo", "bar" };
		ddeTable = new DDETableImpl(cells, "orders", "bad item", 2);
		handler.handle(ddeTable);
	}
	
	@Test
	public void testHandle_WithHeadersInvalid() throws Exception {
		Object cells[] = { "foo", "bar" };
		ddeTable = new DDETableImpl(cells, "orders", "R1C1:R2C1", 2);
		Map<String, Integer> expectedHeader = new Hashtable<String, Integer>();
		expectedHeader.put("foo", 0);
		expectedHeader.put("bar", 1);
		orderTable.clear();
		expect(headerValidator.validate(eq(expectedHeader.keySet())))
			.andReturn(false);
		control.replay();
		
		handler.handle(ddeTable);
		
		control.verify();
		assertEquals(OrderTableHandler.EMPTY_MAP,
				handler.getCurrentHeaderIndex());
	}
	
	@Test
	public void testHandle_WithValidHeaders() throws Exception {
		Object cells[] = {
				RowAdapters.ORD_ID,
				RowAdapters.ORD_TRANS_ID,
				RowAdapters.ORD_STATUS,
				RowAdapters.ORD_SEC,
				RowAdapters.ORD_SECCLASS,
				RowAdapters.ORD_ACCOUNT,
				RowAdapters.ORD_PORTFOLIO,
				RowAdapters.ORD_DIR,
				RowAdapters.ORD_QTY,
				RowAdapters.ORD_QTY_REST,
				RowAdapters.ORD_PRICE,
				RowAdapters.ORD_DATE,
				RowAdapters.ORD_TIME,
				RowAdapters.ORD_CHNGDATE,
				RowAdapters.ORD_CHNGTIME,
				RowAdapters.ORD_TYPE,
				// row 1
				1000D, 0D, "FILLED", "SBER", "EQBR", "LX01", "9634", "B",
					10D, 0D, 120.05D,
					"2012-01-01", "20:45:15", "1999-01-01", "00:00:00", "L",
				// row 2
				1001D, 1D, "KILLED", "GAZP", "EQXX", "LX02", "1834", "S",
					5D, 5D, 0D,
					"2012-01-02", "21:55:11", "1999-01-01", "00:00:00", "M",
		};
		ddeTable = new DDETableImpl(cells, "orders", "R1C1:R3C16", 16);
		Map<String, Integer> expectedHeader = new Hashtable<String, Integer>();
		expectedHeader.put(RowAdapters.ORD_ID, 0);
		expectedHeader.put(RowAdapters.ORD_TRANS_ID, 1);
		expectedHeader.put(RowAdapters.ORD_STATUS, 2);
		expectedHeader.put(RowAdapters.ORD_SEC, 3);
		expectedHeader.put(RowAdapters.ORD_SECCLASS, 4);
		expectedHeader.put(RowAdapters.ORD_ACCOUNT, 5);
		expectedHeader.put(RowAdapters.ORD_PORTFOLIO, 6);
		expectedHeader.put(RowAdapters.ORD_DIR, 7);
		expectedHeader.put(RowAdapters.ORD_QTY, 8);
		expectedHeader.put(RowAdapters.ORD_QTY_REST, 9);
		expectedHeader.put(RowAdapters.ORD_PRICE, 10);
		expectedHeader.put(RowAdapters.ORD_DATE, 11);
		expectedHeader.put(RowAdapters.ORD_TIME, 12);
		expectedHeader.put(RowAdapters.ORD_CHNGDATE, 13);
		expectedHeader.put(RowAdapters.ORD_CHNGTIME, 14);
		expectedHeader.put(RowAdapters.ORD_TYPE, 15);
		orderTable.clear();
		expect(headerValidator.validate(eq(expectedHeader.keySet())))
			.andReturn(true);
		/*
			orderTable.setRow(new OrderTableRow(
					(Long) rs.get(Spec.ORD_ID),
					(Long) rs.get(Spec.ORD_TRANSID),
					(Account) rs.get(Spec.ORD_ACCOUNT),
					(Date) rs.get(Spec.ORD_TIME),
					(OrderDirection) rs.get(Spec.ORD_DIR),
					(SecurityDescriptor) rs.get(Spec.ORD_SECDESCR),
					(Long) rs.get(Spec.ORD_QTY),
					(Double) rs.get(Spec.ORD_PRICE),
					(Long) rs.get(Spec.ORD_QTYREST),
					(OrderStatus) rs.get(Spec.ORD_STATUS),
					(OrderType) rs.get(Spec.ORD_TYPE)));
		 */
		//expect(orderTable.setRow(new OrderTableRow(1000L, 0L,
		//		new Account())));
		
		fail("TODO: incomplete");
	}

	@Test
	public void testHandle_WithoutHeaders() throws Exception {
		fail("TODO: incomplete");
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	public void testEquals() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	public void testEquals_CurrentHeadersConsidered() throws Exception {
		fail("TODO: incomplete");
	}

}
