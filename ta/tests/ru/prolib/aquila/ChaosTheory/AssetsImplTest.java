package ru.prolib.aquila.ChaosTheory;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Observable;
import java.util.Observer;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;


public class AssetsImplTest {
	IMocksControl control;
	Observer observer;
	AssetsImpl assets;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		observer = control.createMock(Observer.class);
		assets = new AssetsImpl(); 
	}
	
	@Test
	public void testAdd() throws Exception {
		AssetImpl asset = new AssetImpl("FOO", "CLASS", 0.01d, 2);
		observer.update(assets, asset);
		expectLastCall().andDelegateTo(new Observer(){
			@Override
			public void update(Observable o, Object arg) {
				try {
					assertTrue(assets.exists("FOO"));
					assertSame(arg, assets.getByCode("FOO"));
				} catch ( Exception e ) {
					fail("Unexpected exception: " + e);
				}
			}});
		assets.addObserver(observer);
		control.replay();
		
		assertFalse(assets.exists("FOO"));
		assets.add(asset);
		assertTrue(assets.exists("FOO"));
		assertSame(asset, assets.getByCode("FOO"));
		
		control.verify();
	}
	
	@Test
	public void testRemove() throws Exception {
		AssetImpl asset = new AssetImpl("FOO", "CLASS", 0.01d, 2);
		assets.add(asset);
		assets.remove("FOO");
		assertFalse(assets.exists("FOO"));
	}
	
	@Test
	public void testGetByCode_ThrowsNotExists() throws Exception {
		try {
			assets.getByCode("FOO");
			fail("Expected exception: " + AssetsNotExistsException.class);
		} catch ( AssetsNotExistsException e ) {
			assertEquals("Asset not exists: FOO", e.getMessage());
		}
	}
	
	@Test
	public void testAdd_ThrowsAlreadyExists() throws Exception {
		AssetImpl asset1 = new AssetImpl("FOO", "CLASS", 0.01d, 2);
		AssetImpl asset2 = new AssetImpl("FOO", "ZORG5", 1.00d, 1);
		assets.add(asset1);
		try {
			assets.add(asset2);
			fail("Expected exception: " + AssetsAlreadyExistsException.class);
		} catch ( AssetsAlreadyExistsException e ) {
			assertEquals("Asset already exists: FOO", e.getMessage());
		}
		assertSame(asset1, assets.getByCode("FOO"));
	}

}
