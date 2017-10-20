package ru.prolib.aquila.core.utils;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

/**
 * 2012-11-07<br>
 * $Id: DepsTest.java 307 2012-11-07 14:34:39Z whirlwind $
 */
public class DepsTest {
	private Dependencies<String> deps;
	private Set<String> set;

	@Before
	public void setUp() throws Exception {
		set = null;
		deps = new Deps<String>()
			.setDependency("butter", "milk")
			.setDependency("coctail", "milk")
			.setDependency("coctail", "sugar")
			.setDependency("children", "parents")
			.setDependency("milk", "cow");
	}
	
	@Test (expected=NullPointerException.class)
	public void testSetDependency_ThrowsIfSubjIsNull() throws Exception {
		deps.setDependency(null, "grass");
	}
	
	@Test (expected=NullPointerException.class)
	public void testSetDependency_ThrowsIfDependentToIsNull() throws Exception {
		deps.setDependency("cow", null);
	}
	
	@Test
	public void testHasDependentTo() throws Exception {
		assertTrue(deps.hasDependentTo("milk"));
		assertTrue(deps.hasDependentTo("parents"));
		assertTrue(deps.hasDependentTo("cow"));
		assertFalse(deps.hasDependentTo("butter"));
		assertFalse(deps.hasDependentTo("something else"));
	}
	
	@Test
	public void testHasDependency1() throws Exception {
		assertTrue(deps.hasDependency("butter"));
		assertTrue(deps.hasDependency("coctail"));
		assertTrue(deps.hasDependency("children"));
		assertTrue(deps.hasDependency("milk"));
		assertFalse(deps.hasDependency("cow"));
		assertFalse(deps.hasDependency("something else"));
	}
	
	@Test
	public void testHasDependency2() throws Exception {
		assertTrue(deps.hasDependency("butter", "milk"));
		assertTrue(deps.hasDependency("coctail", "milk"));
		assertTrue(deps.hasDependency("coctail", "sugar"));
		assertTrue(deps.hasDependency("children", "parents"));
		assertTrue(deps.hasDependency("milk", "cow"));
		assertFalse(deps.hasDependency("children", "chool"));
		assertFalse(deps.hasDependency("cowboy", "cow"));
	}
	
	@Test
	public void testGetDependentsTo() throws Exception {
		set = deps.getDependentsTo("milk");
		assertEquals(2, set.size());
		assertTrue(set.contains("butter"));
		assertTrue(set.contains("coctail"));
		
		set = deps.getDependentsTo("sugar");
		assertEquals(1, set.size());
		assertTrue(set.contains("coctail"));
		
		set = deps.getDependentsTo("something else");
		assertEquals(0, set.size());
	}
	
	@Test
	public void testGetDependencies() throws Exception {
		set = deps.getDependencies("coctail");
		assertEquals(2, set.size());
		assertTrue(set.contains("milk"));
		assertTrue(set.contains("sugar"));
		
		set = deps.getDependencies("children");
		assertEquals(1, set.size());
		assertTrue(set.contains("parents"));
		
		set = deps.getDependencies("something else");
		assertEquals(0, set.size());
	}
	
	@Test
	public void testDropDependency() throws Exception {
		deps.dropDependency("butter", "milk")
			.dropDependency("coctail", "milk") // зависимые от milk отсутствуют
			 								   // но осталась зависимость milk 
			.dropDependency("something else", "anything") // nothing to do
			.dropDependency("children", "school") // ничего не происходит
			.dropDependency("cowboy", "cow") // nothing to do
			.dropDependency("coctail", "sugar"); // сахар нигде не фигурирует
		
		assertFalse(deps.hasDependentTo("milk"));
		assertFalse(deps.hasDependency("butter"));
		assertFalse(deps.hasDependency("butter", "milk"));
		assertFalse(deps.hasDependency("coctail"));
		assertFalse(deps.hasDependency("coctail", "milk"));
		assertTrue(deps.hasDependency("milk"));
		assertTrue(deps.hasDependency("milk", "cow"));
		
		assertFalse(deps.hasDependentTo("sugar"));
		assertFalse(deps.hasDependency("coctail", "sugar"));
		
		assertTrue(deps.hasDependentTo("parents"));
		assertTrue(deps.hasDependency("children"));
		assertTrue(deps.hasDependency("children", "parents"));
	}
	
	@Test
	public void testDropDependencies() throws Exception {
		deps.dropDependencies("coctail")
			.dropDependencies("milk");
		
		assertTrue(deps.hasDependency("butter", "milk"));
		assertFalse(deps.hasDependency("coctail"));
		assertFalse(deps.hasDependency("coctail", "milk"));
		assertFalse(deps.hasDependency("coctail", "sugar"));
		assertTrue(deps.hasDependency("children", "parents"));
		assertFalse(deps.hasDependency("milk"));
		assertFalse(deps.hasDependency("milk", "cow"));
	}
	
	@Test
	public void testDropDependentsTo() throws Exception {
		deps.dropDependentsTo("milk")
			.dropDependentsTo("parents");
		
		assertFalse(deps.hasDependency("butter", "milk"));
		assertFalse(deps.hasDependency("coctail", "milk"));
		assertTrue(deps.hasDependency("coctail", "sugar"));
		assertFalse(deps.hasDependency("children", "parents"));
		assertTrue(deps.hasDependency("milk", "cow"));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(deps.equals(deps));
		assertTrue(deps.equals(new Deps<String>()
			.setDependency("butter", "milk")
			.setDependency("coctail", "milk")
			.setDependency("coctail", "sugar")
			.setDependency("children", "parents")
			.setDependency("milk", "cow")));
		assertFalse(deps.equals(null));
		assertFalse(deps.equals(this));
		assertFalse(deps.equals(new Deps<String>()
			.setDependency("spaceship", "science")
			.setDependency("politics", "lie")));
	}
	
	@Test
	public void testhashCode() throws Exception {
		Map<String, Set<String>> d1 = new HashMap<String, Set<String>>();
		d1.put("butter", deps.getDependencies("butter"));
		d1.put("coctail", deps.getDependencies("coctail"));
		d1.put("children", deps.getDependencies("children"));
		d1.put("milk", deps.getDependencies("milk"));
		Map<String, Set<String>> d2 = new HashMap<String, Set<String>>();
		d2.put("milk", deps.getDependentsTo("milk"));
		d2.put("sugar", deps.getDependentsTo("sugar"));
		d2.put("parents", deps.getDependentsTo("parents"));
		d2.put("cow", deps.getDependentsTo("cow"));
		int hashCode = new HashCodeBuilder(20121107, 173637)
			.append(d1)
			.append(d2)
			.toHashCode();
		assertEquals(hashCode, deps.hashCode());
	}

}
