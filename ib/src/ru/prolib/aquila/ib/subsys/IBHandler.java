package ru.prolib.aquila.ib.subsys;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Counter;
import ru.prolib.aquila.ib.event.*;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.run.IBRunnableFactory;

/**
 * Обработчик данных IB.
 * <p>
 * Обрабатывает:
 * - Список портфелей и обновления портфелей (с запросом)
 * - Новые заявки и обновления состояния заявок (без запроса на автополучение)
 * - Обновление нумератора транзакций
 * - Контракты (запускает через локатор)
 * <p>
 * 2013-01-08<br>
 * $Id: IBHandler.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBHandler implements EventListener {
	private static final Logger logger;
	private final IBServiceLocator locator;
	
	static {
		logger = LoggerFactory.getLogger(IBHandler.class);
	}
	
	public IBHandler(IBServiceLocator locator) {
		super();
		this.locator = locator;
	}

	@Override
	public void onEvent(Event event) {
		IBClient client = locator.getApiClient();
		IBRunnableFactory frun = locator.getRunnableFactory();
		if ( event.isType(client.OnManagedAccounts()) ) {
			IBEventAccounts e = (IBEventAccounts) event;
			String account[] = StringUtils.split(e.getAccounts(), ',');
			for ( int i = 0; i < account.length; i ++ ) {
				// Здесь главное сделать запрос, а дальше уже общий
				// обработчик подцепит. Сам объект запроса после
				// старта становится ненужным.
				locator.getRequestFactory()
					.requestAccountUpdates(account[i])
					.start();
			}
		} else if ( event.isType(client.OnUpdateAccount()) ) {
			frun.createUpdateAccount((IBEventUpdateAccount) event).run();
		} else if ( event.isType(client.OnUpdatePortfolio()) ) {
			frun.createUpdatePosition((IBEventUpdatePortfolio) event).run();
		} else if ( event.isType(client.OnOpenOrder())
				 || event.isType(client.OnOrderStatus()) )
		{
			frun.createUpdateOrder((IBEventOrder) event).run();
		} else if ( event.isType(client.OnNextValidId()) ) {
			onNextValidId((IBEventRequest)event);
		}
	}
	
	private void onNextValidId(IBEventRequest event) {
		Counter sequence = locator.getTransactionNumerator();
		int current, offered = event.getReqId();
		synchronized ( sequence ) {
			current = sequence.get();
			if ( current < offered ) {
				sequence.set(offered);
			} else {
				logger.info("Skip offered ID {} cuz less than current ID: {}",
						new Object[] { offered, current });
			}
		}
	}

	public void start() {
		IBClient client = locator.getApiClient();
		client.OnManagedAccounts().addListener(this);
		client.OnUpdateAccount().addListener(this);
		client.OnUpdatePortfolio().addListener(this);
		client.OnOpenOrder().addListener(this);
		client.OnOrderStatus().addListener(this);
		locator.getContracts().start();
		client.OnNextValidId().addListener(this);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == IBHandler.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		IBHandler o = (IBHandler) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.isEquals();
	}

}
