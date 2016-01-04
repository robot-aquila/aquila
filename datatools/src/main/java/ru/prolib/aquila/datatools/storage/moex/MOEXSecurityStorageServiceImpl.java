package ru.prolib.aquila.datatools.storage.moex;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
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
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MOEXSecurityStorageServiceImpl.class);
	}
	
	private SymbolRepository symbolRepository;
	private SecurityPropertiesRepository securityPropertiesRepository;
	private SecuritySessionPropertiesRepository securitySessionPropertiesRepository;
	private final MOEXUtils utils;
	private final Map<Symbol, SecuritySessionProperties> entityCache;
	private final Map<Symbol, Boolean> propertiesSaved;
	private final Lock lock;
	
	/**
	 * Service constructor (for tests).
	 * <p>
	 * @param utils - the utility functions object
	 * @param entityCache - cache of session properties 
	 * @param propertiesSaved - security properties status
	 */
	protected MOEXSecurityStorageServiceImpl(MOEXUtils utils,
			Map<Symbol, SecuritySessionProperties> entityCache,
			Map<Symbol, Boolean> propertiesSaved)
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
			new HashMap<Symbol, SecuritySessionProperties>(),
			new HashMap<Symbol, Boolean>());
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
	@Transactional
	public void snapshotSessionAttributes(Security security, LocalDateTime time) {
		makeSnapshot(security, time, true);
	}

	@Override
	@Transactional
	public void snapshotSessionAttributes(Security security) {
		makeSnapshot(security, LocalDateTime.ofInstant(security.getTerminal()
				.getCurrentTime(), ZoneOffset.UTC), false);
	}
	
	private void makeSnapshot(Security security, LocalDateTime time, boolean force) {
		lock.lock();
		try {
			Symbol symbol = security.getSymbol();
			if ( ! propertiesSaved.containsKey(symbol) ) {
				updateSecurityProperties(security);
			}
			SecuritySessionProperties p1 = entityCache.get(symbol);
			SecuritySessionPropertiesEntity p2 = toProperties(security);
			if ( ! force && p1 != null && utils.isPropertiesEquals(p1, p2) ) {
				return; // skip this update
			}
			p2.setSnapshotTime(time);
			p2.setClearingTime(utils.getClearingTime(symbol, time));
			entityCache.put(symbol, p2);
			securitySessionPropertiesRepository.save(p2);
			logger.debug("Security session properties updated: {}", symbol);
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
		return getSymbolEntity(security.getSymbol());
	}
	
	private SymbolEntity getSymbolEntity(Symbol symbol) {
		return symbolRepository.getBySymbol(symbol);
	}
	
	private void updateSecurityProperties(Security security) {
		Symbol symbol = security.getSymbol();
		Boolean x = propertiesSaved.get(symbol); 
		if ( x != null && x ) {
			return;
		}
		SecurityPropertiesEntity p = null;
		try {
			p = securityPropertiesRepository.getBySymbol(symbol);
		} catch ( RepositoryObjectNotFoundException e ) {
			p = securityPropertiesRepository.createEntity();
			p.setSymbol(getSymbolEntity(symbol));
			utils.fillProperties(security, p);
			securityPropertiesRepository.save(p);
			logger.debug("Security properties updated: {}", symbol);
		}
		propertiesSaved.put(symbol, true);
	}

}
