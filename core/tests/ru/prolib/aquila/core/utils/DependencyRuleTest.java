package ru.prolib.aquila.core.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 2013-02-16<br>
 * $Id$
 */
public class DependencyRuleTest {
	
	@Test
	public void testConstants() throws Exception {
		assertEquals("WAIT", DependencyRule.WAIT.toString());
		assertEquals("DROP", DependencyRule.DROP.toString());
	}

}
