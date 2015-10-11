package ru.prolib.aquila.datatools.storage.moex;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.datatools.storage.SecurityStorageService;
import ru.prolib.aquila.datatools.storage.SecuritySessionProperties;
import ru.prolib.aquila.datatools.storage.dao.RepositoryObjectNotFoundException;
import ru.prolib.aquila.datatools.storage.dao.SecurityPropertiesRepository;
import ru.prolib.aquila.datatools.storage.dao.SymbolRepository;
import ru.prolib.aquila.datatools.storage.dao.SecuritySessionPropertiesRepository;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;
import ru.prolib.aquila.datatools.storage.model.SecuritySessionPropertiesEntity;

public class MOEXSecurityStorageServiceImpl implements SecurityStorageService {
	private SymbolRepository symbolRepository;
	private SecurityPropertiesRepository securityPropertiesRepository;
	private SecuritySessionPropertiesRepository securitySessionPropertiesRepository;
	private final MOEXUtils utils;
	private final Map<SecurityDescriptor, SecuritySessionProperties> entityCache;
	private final Map<SecurityDescriptor, Boolean> propertiesSaved;
	private final Lock lock;
	
	/**
	 * Service constructor (for tests).
	 * <p>
	 * @param utils - the utility functions object
	 * @param entityCache - cache of session properties 
	 * @param propertiesSaved - security properties status
	 */
	protected MOEXSecurityStorageServiceImpl(MOEXUtils utils,
			Map<SecurityDescriptor, SecuritySessionProperties> entityCache,
			Map<SecurityDescriptor, Boolean> propertiesSaved)
	{
		super();
		this.entityCache = entityCache;
		this.propertiesSaved = propertiesSaved;
		this.utils = utils;
		lock = new ReentrantLock();
	}
	
	/**
	 * Constructor.
	 */
	public MOEXSecurityStorageServiceImpl() {
		this(new MOEXUtils(),
			new HashMap<SecurityDescriptor, SecuritySessionProperties>(),
			new HashMap<SecurityDescriptor, Boolean>());
	}
	
	public void setSymbolRepository(SymbolRepository repository) {
		this.symbolRepository = repository;
	}
	
	public void setSecurityPropertiesRepository(SecurityPropertiesRepository repository) {
		this.securityPropertiesRepository = repository;
	}
	
	public void setSecuritySessionPropertiesRepository(SecuritySessionPropertiesRepository repository) {
		this.securitySessionPropertiesRepository = repository;
	}

	@Override
	public void snapshotSessionAttributes(Security security, DateTime time) {
		makeSnapshot(security, time, true);
	}

	@Override
	public void snapshotSessionAttributes(Security security) {
		makeSnapshot(security, security.getTerminal().getCurrentTime(), false);
	}
	
	private void makeSnapshot(Security security, DateTime time, boolean force) {
		lock.lock();
		try {
			SecurityDescriptor descr = security.getDescriptor();
			if ( ! propertiesSaved.containsKey(descr) ) {
				updateSecurityProperties(security);
			}
			SecuritySessionProperties p1 = entityCache.get(descr);
			SecuritySessionPropertiesEntity p2 = toProperties(security);
			if ( ! force && p1 != null && utils.isPropertiesEquals(p1, p2) ) {
				return; // skip this update
			}
			p2.setSnapshotTime(time);
			p2.setClearingTime(utils.getClearingTime(descr, time));
			entityCache.put(descr, p2);
			securitySessionPropertiesRepository.save(p2);
		} finally {
			lock.unlock();
		}
	}
	
	private SecuritySessionPropertiesEntity toProperties(Security security) {
		SecuritySessionPropertiesEntity p = securitySessionPropertiesRepository.createEntity();
		p.setSymbol(getSymbolEntity(security));
		utils.fillSessionProperties(security, p);
		return p;
	}
	
	private SymbolEntity getSymbolEntity(Security security) {
		return getSymbolEntity(security.getDescriptor());
	}
	
	private SymbolEntity getSymbolEntity(SecurityDescriptor descr) {
		return symbolRepository.getByDescriptor(descr);
	}
	
	private void updateSecurityProperties(Security security) {
		SecurityDescriptor descr = security.getDescriptor();
		Boolean x = propertiesSaved.get(descr); 
		if ( x != null && x ) {
			return;
		}
		SecurityPropertiesEntity p = null;
		try {
			p = securityPropertiesRepository.getByDescriptor(descr);
		} catch ( RepositoryObjectNotFoundException e ) {
			p = securityPropertiesRepository.createEntity();
			p.setSymbol(getSymbolEntity(descr));
			utils.fillProperties(security, p);
			securityPropertiesRepository.save(p);
		}
		propertiesSaved.put(descr, true);
	}

}
