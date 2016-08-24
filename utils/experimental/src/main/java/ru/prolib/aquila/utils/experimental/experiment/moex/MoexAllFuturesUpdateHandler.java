package ru.prolib.aquila.utils.experimental.experiment.moex;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.moex.Moex;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;

public class MoexAllFuturesUpdateHandler implements UpdateHandler {
	private static final Logger logger;
	private static final int CONTRACTS_PER_EXECUTION = 15; 
	
	static {
		logger = LoggerFactory.getLogger(MoexAllFuturesUpdateHandler.class);
	}
	
	private final CountDownLatch globalExit;
	private final Moex moex;
	private final MoexContractFileStorage storage;
	private final Instant updatePlannedTime;
	private final Set<Integer> expectedChangedTokens;
	private final Map<Symbol, MoexContractUpdateHandler> handlers;
	private boolean done = false, firstTime = true;
	
	/**
	 * Constructor.
	 * <p>
	 * @param globalExit - global exit signal
	 * @param moex - the MOEX facade
	 * @param storage - storage of delta-updates
	 * @param updatePlannedTime - this is planned time of delta-update to write
	 * @param expectedChangedTokens - the set of tokens which should be changed
	 * to make an update. This set may be empty.  
	 */
	public MoexAllFuturesUpdateHandler(CountDownLatch globalExit, Moex moex,
			MoexContractFileStorage storage, Instant updatePlannedTime,
			Set<Integer> expectedChangedTokens)
	{
		this.globalExit = globalExit;
		this.moex = moex;
		this.storage = storage;
		this.updatePlannedTime = updatePlannedTime;
		this.expectedChangedTokens = expectedChangedTokens;
		this.handlers = new HashMap<>();
	}
	
	@Override
	public boolean execute() throws DataStorageException, WUWebPageException {
		if ( done ) {
			return true;
		}
		if ( firstTime ) {
			for ( String symbolString : moex.getActiveFuturesList() ) {
				Symbol symbol = new Symbol(symbolString);
				handlers.put(symbol, createHandler(symbol));
			}
			firstTime = false;
		}
		List<Symbol> symbols = new ArrayList<>(handlers.keySet());
		Collections.shuffle(symbols);
		symbols = symbols.subList(0, Math.min(symbols.size(), CONTRACTS_PER_EXECUTION));
		logger.debug("Selected symbols for update: {}", symbols);
		for ( Symbol symbol : symbols ) {
			if ( globalExit.getCount() == 0 ) {
				logger.debug("Global exit signal. Stop the task.");
				return false;
			}
			MoexContractUpdateHandler handler = handlers.get(symbol);
			handler.execute();
			IOUtils.closeQuietly(handler);
			handlers.remove(symbol);
			logger.debug("Handler removed: {}", symbol);
		}
		if ( handlers.size() == 0 ) {
			done = true;
		} else {
			logger.debug("Count of open handlers: {}", handlers.size());
		}
		return done;
	}

	@Override
	public void close() throws IOException {
		if ( ! done ) {
			done = true;
			for ( MoexContractUpdateHandler handler : handlers.values() ) {
				handler.close();
			}
			handlers.clear();			
		}
	}
	
	private MoexContractUpdateHandler createHandler(Symbol symbol) {
		logger.debug("Handler created: {}", symbol);
		return new MoexContractUpdateHandler(moex, storage,
				symbol, updatePlannedTime, expectedChangedTokens);
	}
	
}
