package ru.prolib.aquila.core.BusinessEntities.row;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.row.Modifiers;
import ru.prolib.aquila.core.BusinessEntities.setter.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.row.RowElement;
import ru.prolib.aquila.core.utils.Validator;

/**
 * 2013-02-13<br>
 * $Id$
 */
public class ModifiersTest {
	private IMocksControl control;
	private EditableTerminal terminal;
	private Modifiers builder;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		builder = new Modifiers(terminal);
	}
	
	@Test
	public void testEquals() throws Exception {
		EditableTerminal terminal2 = control.createMock(EditableTerminal.class);
		assertTrue(builder.equals(builder));
		assertTrue(builder.equals(new Modifiers(terminal)));
		assertFalse(builder.equals(new Modifiers(terminal2)));
		assertFalse(builder.equals(null));
		assertFalse(builder.equals(this));
	}
	
	@Test
	public void testCreateTradeModifier() throws Exception {
		S<Trade> expected = new MListImpl<Trade>()
			.add(new MStd<Trade>(
					new RowElement("TRD_DIR", OrderDirection.class),
					new TradeSetDirection()))
			.add(new MStd<Trade>(
					new RowElement("TRD_ID", Long.class),
					new TradeSetId()))
			.add(new MStd<Trade>(
					new RowElement("TRD_PRICE", Double.class),
					new TradeSetPrice()))
			.add(new MStd<Trade>(
					new RowElement("TRD_QTY", Long.class),
					new TradeSetQty()))
			.add(new MStd<Trade>(
					new RowElement("TRD_SECDESCR", SecurityDescriptor.class),
					new TradeSetSecurityDescriptor()))
			.add(new MStd<Trade>(
					new RowElement("TRD_TIME", Date.class),
					new TradeSetTime()))
			.add(new MStd<Trade>(
					new RowElement("TRD_VOL", Double.class),
					new TradeSetVolume()));
		
		assertEquals(expected, builder.createTradeModifier());
	}
	
	@Test
	public void testCreatePortfolioModifier() throws Exception {
		S<EditablePortfolio> expected = new MListImpl<EditablePortfolio>()
			.add(new MStd<EditablePortfolio>(
					new RowElement("PORT_BAL", Double.class),
					new PortfolioSetBalance()))
			.add(new MStd<EditablePortfolio>(
					new RowElement("PORT_CASH", Double.class),
					new PortfolioSetCash()))
			.add(new MStd<EditablePortfolio>(
					new RowElement("PORT_VMARG", Double.class),
					new PortfolioSetVariationMargin()))
			.add(new EditableEventGenerator<EditablePortfolio>(
					new FirePortfolioAvailable(terminal)));
		
		assertEquals(expected, builder.createPortfolioModifier());
	}
	
	@Test
	public void testCreatePortfolioModifier1() throws Exception {
		Validator isAvailable = control.createMock(Validator.class);
		S<EditablePortfolio> expected = new MListImpl<EditablePortfolio>()
			.add(new MStd<EditablePortfolio>(
					new RowElement("PORT_BAL", Double.class),
					new PortfolioSetBalance()))
			.add(new MStd<EditablePortfolio>(
					new RowElement("PORT_CASH", Double.class),
					new PortfolioSetCash()))
			.add(new MStd<EditablePortfolio>(
					new RowElement("PORT_VMARG", Double.class),
					new PortfolioSetVariationMargin()))
			.add(new EditableEventGenerator<EditablePortfolio>(isAvailable,
					new FirePortfolioAvailable(terminal)));
		
		assertEquals(expected, builder.createPortfolioModifier(isAvailable));
	}
	
	@Test
	public void testCreatePositionModifier() throws Exception {
		S<EditablePosition> expected = new MListImpl<EditablePosition>()
			.add(new MStd<EditablePosition>(
					new RowElement("POS_BOOKVAL", Double.class),
					new PositionSetBookValue()))
			.add(new MStd<EditablePosition>(
					new RowElement("POS_MKTVAL", Double.class),
					new PositionSetMarketValue()))
			.add(new MStd<EditablePosition>(
					new RowElement("POS_CURR", Long.class),
					new PositionSetCurrQty()))
			.add(new MStd<EditablePosition>(
					new RowElement("POS_LOCK", Long.class),
					new PositionSetLockQty()))
			.add(new MStd<EditablePosition>(
					new RowElement("POS_OPEN", Long.class),
					new PositionSetOpenQty()))
			.add(new MStd<EditablePosition>(
					new RowElement("POS_VMARG", Double.class),
					new PositionSetVarMargin()))
			.add(new EditableEventGenerator<EditablePosition>(
					new FirePositionAvailableAuto(terminal)));
		
		assertEquals(expected, builder.createPositionModifier());
	}
	
	@Test
	public void testCreateOrderModifier() throws Exception {
		EditableOrders orders = control.createMock(EditableOrders.class);
		S<EditableOrder> expected = new MListImpl<EditableOrder>()
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_ACC", Account.class),
					new OrderSetAccount()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_DIR", OrderDirection.class),
					new OrderSetDirection()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_ID", Long.class),
					new OrderSetId()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_PRICE", Double.class),
					new OrderSetPrice()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_QTY", Long.class),
					new OrderSetQty()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_SECDESCR", SecurityDescriptor.class),
					new OrderSetSecurityDescriptor()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_STATUS", OrderStatus.class),
					new OrderSetStatus()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_TRANSID", Long.class),
					new OrderSetTransactionId()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_TYPE", OrderType.class),
					new OrderSetType()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_TIME", Date.class),
					new OrderSetTime()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_CHNGTIME", Date.class),
					new OrderSetLastChangeTime()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_QTYREST", Long.class),
					new OrderSetQtyRest()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_EXECVOL", Double.class),
					new OrderSetExecutedVolume()))
			.add(new EditableEventGenerator<EditableOrder>(
					new FireOrderAvailable(orders)));
		expect(terminal.getOrdersInstance()).andStubReturn(orders);
		control.replay();
		
		assertEquals(expected, builder.createOrderModifier());
		
		control.verify();
	}
	
	@Test
	public void testCreateStopOrderModifier() throws Exception {
		EditableOrders orders = control.createMock(EditableOrders.class);
		S<EditableOrder> expected = new MListImpl<EditableOrder>()
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_ACC", Account.class),
					new OrderSetAccount()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_DIR", OrderDirection.class),
					new OrderSetDirection()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_ID", Long.class),
					new OrderSetId()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_PRICE", Double.class),
					new OrderSetPrice()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_QTY", Long.class),
					new OrderSetQty()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_SECDESCR", SecurityDescriptor.class),
					new OrderSetSecurityDescriptor()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_STATUS", OrderStatus.class),
					new OrderSetStatus()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_TRANSID", Long.class),
					new OrderSetTransactionId()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_TYPE", OrderType.class),
					new OrderSetType()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_TIME", Date.class),
					new OrderSetTime()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_CHNGTIME", Date.class),
					new OrderSetLastChangeTime()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_LINKID", Long.class),
					new OrderSetLinkedOrderId()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_OFFSET", Price.class),
					new OrderSetOffset()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_SPREAD", Price.class),
					new OrderSetSpread()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_STOPLMT", Double.class),
					new OrderSetStopLimitPrice()))
			.add(new MStd<EditableOrder>(
					new RowElement("ORD_TAKEPFT", Double.class),
					new OrderSetTakeProfitPrice()))
			.add(new EditableEventGenerator<EditableOrder>(
					new FireOrderAvailable(orders)));
		expect(terminal.getStopOrdersInstance()).andStubReturn(orders);
		control.replay();

		assertEquals(expected, builder.createStopOrderModifier());
		
		control.verify();
	}
	
	@Test
	public void testCreateSecurityModifier() throws Exception {
		S<EditableSecurity> expected = new MListImpl<EditableSecurity>()
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_ASKPR", Double.class),
					new SecuritySetAskPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_ASKSZ", Long.class),
					new SecuritySetAskSize()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_BIDPR", Double.class),
					new SecuritySetBidPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_BIDSZ", Long.class),
					new SecuritySetBidSize()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_CLOSE", Double.class),
					new SecuritySetClosePrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_DISPNAME", String.class),
					new SecuritySetDisplayName()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_HIGH", Double.class),
					new SecuritySetHighPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_LAST", Double.class),
					new SecuritySetLastPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_LOTSZ", Integer.class),
					new SecuritySetLotSize()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_LOW", Double.class),
					new SecuritySetLowPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_MAXPR", Double.class),
					new SecuritySetMaxPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_MINPR", Double.class),
					new SecuritySetMinPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_MINSTEPPR", Double.class),
					new SecuritySetMinStepPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_MINSTEPSZ", Double.class),
					new SecuritySetMinStepSize()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_OPEN", Double.class),
					new SecuritySetOpenPrice()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_PREC", Integer.class),
					new SecuritySetPrecision()))
			.add(new MStd<EditableSecurity>(
					new RowElement("SEC_STATUS", SecurityStatus.class),
					new SecuritySetStatus()))
			.add(new EditableEventGenerator<EditableSecurity>(
					new FireSecurityAvailable(terminal)));
		
		control.replay();

		assertEquals(expected, builder.createSecurityModifier());
		
		control.verify();
	}

}
