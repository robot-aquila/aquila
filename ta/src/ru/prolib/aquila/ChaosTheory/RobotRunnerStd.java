package ru.prolib.aquila.ChaosTheory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ds.MarketData;

public class RobotRunnerStd implements RobotRunner {
	private static final Logger logger = LoggerFactory.getLogger(RobotRunnerStd.class);
	private final ServiceLocator locator;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public RobotRunnerStd(ServiceLocator locator) {
		super();
		this.locator = locator;
	}

	@Override
	public void run(Robot robot) throws Exception {
		Date end = getEndTime();
		robot.init();
		MarketData data = locator.getMarketData();
		Value<Date> time = data.getTime();
		
		int totalBars = time.getLength();
		if ( totalBars > 0 ) {
			logger.debug("Start from {}", time.get());
		}
		while ( true ) {
			robot.pass();
			
			if ( end != null
				&& ( end.equals(time.get()) || end.before(time.get())) )
			{
				break;
			}
			
			int currBars = time.getLength(); 
			if ( currBars == totalBars ) {
				logger.debug("No more data. Total bars {}", totalBars);
				break;
			} else {
				totalBars = currBars;
			}
		}
		robot.clean();
	}
	
	private Date getEndTime() throws Exception {
		Props props = locator.getProperties();
		Date end = null;
		if ( props.getString("EndTime", null) != null ) {
			end = df.parse(props.getString("EndTime"));
			Calendar c = Calendar.getInstance();
			c.setTime(end);
			c.set(Calendar.MILLISECOND, 0);
			end = c.getTime();
		}
		return end;
	}

}
