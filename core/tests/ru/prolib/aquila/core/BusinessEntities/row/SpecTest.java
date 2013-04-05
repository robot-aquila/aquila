package ru.prolib.aquila.core.BusinessEntities.row;

import static org.junit.Assert.*;

import org.junit.*;

/**
 * 2013-02-17<br>
 * $Id$
 */
public class SpecTest {
	
	@Test
	public void testConstants() throws Exception {
		assertEquals("TRD_DIR", Spec.TRADE_DIR.toString());
		assertEquals("TRD_ID", Spec.TRADE_ID.toString());
		assertEquals("TRD_PRICE", Spec.TRADE_PRICE.toString());
		assertEquals("TRD_QTY", Spec.TRADE_QTY.toString());
		assertEquals("TRD_SECDESCR", Spec.TRADE_SECDESCR.toString());
		assertEquals("TRD_TIME", Spec.TRADE_TIME.toString());
		assertEquals("TRD_VOL", Spec.TRADE_VOL.toString());
		
		assertEquals("PORT_ACC", Spec.PORT_ACCOUNT.toString());
		assertEquals("PORT_BAL", Spec.PORT_BALANCE.toString());
		assertEquals("PORT_CASH", Spec.PORT_CASH.toString());
		assertEquals("PORT_VMARG", Spec.PORT_VMARGIN.toString());
		
		assertEquals("POS_ACC", Spec.POS_ACCOUNT.toString());
		assertEquals("POS_BOOKVAL", Spec.POS_BOOKVAL.toString());
		assertEquals("POS_MKTVAL", Spec.POS_MARKETVAL.toString());
		assertEquals("POS_CURR", Spec.POS_CURR.toString());
		assertEquals("POS_LOCK", Spec.POS_LOCK.toString());
		assertEquals("POS_OPEN", Spec.POS_OPEN.toString());
		assertEquals("POS_SECDESCR", Spec.POS_SECDESCR.toString());
		assertEquals("POS_VMARG", Spec.POS_VMARGIN.toString());
		
		assertEquals("ORD_ACC", Spec.ORD_ACCOUNT.toString());
		assertEquals("ORD_DIR", Spec.ORD_DIR.toString());
		assertEquals("ORD_EXECVOL", Spec.ORD_EXECVOL.toString());
		assertEquals("ORD_ID", Spec.ORD_ID.toString());
		assertEquals("ORD_LINKID", Spec.ORD_LINKID.toString());
		assertEquals("ORD_OFFSET", Spec.ORD_OFFSET.toString());
		assertEquals("ORD_PRICE", Spec.ORD_PRICE.toString());
		assertEquals("ORD_QTY", Spec.ORD_QTY.toString());
		assertEquals("ORD_QTYREST", Spec.ORD_QTYREST.toString());
		assertEquals("ORD_SECDESCR", Spec.ORD_SECDESCR.toString());
		assertEquals("ORD_SPREAD", Spec.ORD_SPREAD.toString());
		assertEquals("ORD_STATUS", Spec.ORD_STATUS.toString());
		assertEquals("ORD_STOPLMT", Spec.ORD_STOPLMT.toString());
		assertEquals("ORD_TAKEPFT", Spec.ORD_TAKEPFT.toString());
		assertEquals("ORD_TRANSID", Spec.ORD_TRANSID.toString());
		assertEquals("ORD_TYPE", Spec.ORD_TYPE.toString());
		assertEquals("ORD_TIME", Spec.ORD_TIME.toString());
		assertEquals("ORD_CHNGTIME", Spec.ORD_CHNGTIME.toString());
	}

}
