package ru.prolib.aquila.ib.assembler;


import static org.junit.Assert.*;

import org.junit.*;

public class AssemblerTest {
	private Assembler asm; 

	@Before
	public void setUp() throws Exception {
		asm = new Assembler();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(asm.equals(asm));
		assertFalse(asm.equals(null));
		assertFalse(asm.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(asm.equals(new Assembler()));
	}

}
