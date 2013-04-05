package ru.prolib.aquila.core.BusinessEntities.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.PortfolioImpl;
import ru.prolib.aquila.core.BusinessEntities.PositionsImpl;
import ru.prolib.aquila.core.BusinessEntities.utils.PortfolioFactoryImpl;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-10<br>
 * $Id$
 */
public class PortfolioFactoryImplTest {
	private IMocksControl control;
	private EventSystem es;
	private EditableTerminal terminal;
	private PortfolioFactoryImpl factory;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		es = control.createMock(EventSystem.class);
		terminal = control.createMock(EditableTerminal.class);
		factory = new PortfolioFactoryImpl(es, terminal);
	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(es)
			.add(null);
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>(vEs)
			.add(null)
			.add(terminal);
		Variant<?> iterator = vTerm;
		int exceptionCnt = 0;
		PortfolioFactoryImpl found = null;
		do {
			try {
				found = new PortfolioFactoryImpl(vEs.get(), vTerm.get());
			} catch ( NullPointerException e ) {
				exceptionCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(iterator.count() - 1, exceptionCnt);
		assertSame(es, found.getEventSystem());
		assertSame(terminal, found.getTerminal());
	}
	
	@Test
	public void testCreatePortfolio() throws Exception {
		EventDispatcher posDisp = control.createMock(EventDispatcher.class);
		EventType onPosAvailable = control.createMock(EventType.class);
		EventType onPosChanged = control.createMock(EventType.class);
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType onChanged = control.createMock(EventType.class);
		
		expect(es.createEventDispatcher("Positions[ZULU]"))
			.andReturn(posDisp);
		expect(es.createGenericType(posDisp, "OnAvailable"))
			.andReturn(onPosAvailable);
		expect(es.createGenericType(posDisp, "OnChanged"))
			.andReturn(onPosChanged);
		expect(es.createEventDispatcher("Portfolio[ZULU]"))
			.andReturn(dispatcher);
		expect(es.createGenericType(dispatcher, "OnChanged"))
			.andReturn(onChanged);
		control.replay();
		
		PortfolioImpl p =
				(PortfolioImpl) factory.createPortfolio(new Account("ZULU"));
		
		control.verify();
		assertNotNull(p);
		assertEquals(new Account("ZULU"), p.getAccount());
		assertSame(terminal, p.getTerminal());
		assertSame(dispatcher, p.getEventDispatcher());
		assertSame(onChanged, p.OnChanged());
		assertSame(onPosAvailable, p.OnPositionAvailable());
		assertSame(onPosChanged, p.OnPositionChanged());
		assertEquals(0x02, PortfolioImpl.VERSION);
		assertNull(p.getCash());
		assertNull(p.getVariationMargin());
		assertNull(p.getBalance());
		
		PositionsImpl pos = (PositionsImpl) p.getPositionsInstance();
		assertNotNull(pos);
		assertSame(posDisp, pos.getEventDispatcher());
		assertSame(onPosAvailable, pos.OnPositionAvailable());
		assertSame(onPosChanged, pos.OnPositionChanged());
		assertSame(pos.OnPositionAvailable(), p.OnPositionAvailable());
		assertSame(pos.OnPositionChanged(), p.OnPositionChanged());
		assertEquals(
			new PositionFactoryImpl(es, new Account("ZULU"), terminal),
			pos.getPositionFactory()); 
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(es)
			.add(control.createMock(EventSystem.class));
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>(vEs)
			.add(control.createMock(EditableTerminal.class))
			.add(terminal);
		Variant<?> iterator = vTerm;
		int foundCnt = 0;
		PortfolioFactoryImpl found = null, x = null;
		do {
			x = new PortfolioFactoryImpl(vEs.get(), vTerm.get());
			if ( factory.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(es, found.getEventSystem());
		assertSame(terminal, found.getTerminal());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, 220013)
			.append(es)
			.append(terminal)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}

}
