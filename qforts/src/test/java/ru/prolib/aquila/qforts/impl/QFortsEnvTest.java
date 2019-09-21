package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.PortfolioField;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFortsEnvTest {
	private static Account account;
	private IMocksControl control;
	private EditableTerminal terminal;
	private QForts facadeMock;
	private QFortsEnv service;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		account = new Account("TEST");
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		facadeMock = control.createMock(QForts.class);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		service = new QFortsEnv(terminal, facadeMock);
	}

	@Test
	public void testCreateAccount() throws Exception {
		EditablePortfolio p = terminal.getEditablePortfolio(account);
		p.consume(new DeltaUpdateBuilder()
				.withToken(PortfolioField.BALANCE, CDecimalBD.ofRUB2("8249981"))
				.buildUpdate());
		facadeMock.changeBalance(p, CDecimalBD.ofRUB2("-8239981"));
		control.replay();
		
		assertSame(p, service.createPortfolio(account, CDecimalBD.ofRUB2("10000")));
		
		control.verify();
	}

}
