package ru.prolib.aquila.quik.assembler;

import static org.junit.Assert.*;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalBuilder;
import ru.prolib.aquila.quik.dde.*;

public class AssemblerBuilderTest {
	private AssemblerBuilder builder;

	@Before
	public void setUp() throws Exception {
		builder = new AssemblerBuilder();
	}
	
	@Test
	public void testCreateAssembler() throws Exception {
		EditableTerminal terminal = new TerminalBuilder().createTerminal("foo");
		Cache cache = new CacheBuilder().createCache(terminal);
		Assembler expected = new Assembler(terminal, cache,
			new AssemblerHighLvl(terminal, cache,
				new AssemblerMidLvl(terminal, cache,
					new AssemblerLowLvl(terminal, cache))));
		assertEquals(expected, builder.createAssembler(terminal, cache));
	}

}
