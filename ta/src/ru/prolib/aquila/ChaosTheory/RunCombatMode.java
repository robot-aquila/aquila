package ru.prolib.aquila.ChaosTheory;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ipc.IEvent;
import ru.prolib.aquila.ipc.IPrimitive;
import ru.prolib.aquila.ipc.ISession;
import ru.prolib.aquila.rxltdde.Receiver.ReceiverService;

public class RunCombatMode {
	private static Logger logger = LoggerFactory.getLogger(RunCombatMode.class);
	private static ServiceLocator locator;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if ( args.length < 1 ) {
			System.err.println("Usage: <config> [log4j-config]");
			return;
		}
		if ( args.length >= 2 ) {
			PropertyConfigurator.configure(args[1]);
		}
		locator = ServiceLocatorImpl.getInstance(new File(args[0])); 
		
		IPrimitive[] signals = new IPrimitive[3];
		ISession ipc = locator.getIpcSession();
		ReceiverService receiver = locator.getRXltDdeReceiver();
		Props props = locator.getProperties();
		// сигнал о наличии обновлений в таблице котировок 
		signals[0] = ipc.createEvent(props.getString(RobotImpl.PROPNAME_UPDATE_SIGNAL));
		// сигнал о наличии подключения к серверу экспорта данных
		signals[1] = ipc.wrapSelector(receiver.getSelector());
		// сигнал на выход (не используется)
		signals[2] = ipc.createEvent("exit");
		
		Robot robot = new RobotImpl(locator);
		boolean initialized = false;
		while ( !((IEvent)signals[2]).isSignaled() ) {
			switch ( ipc.waitForMultiple(signals) ) {
			case 0:
				try {
					if ( initialized == false ) {
						robot.init();
						initialized = true;
					}
					robot.pass();
				} catch ( Exception e ) {
					logger.error("Unhandled ROBOT exception", e);
				}
				break;
			case 1:
				receiver.processSelectedKeys();
				break;
			case 2:
				logger.info("Exit signal");
				break;
			}
		}
		robot.clean();
		receiver.close();
	}

}
