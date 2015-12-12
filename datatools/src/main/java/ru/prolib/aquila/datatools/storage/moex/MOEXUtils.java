package ru.prolib.aquila.datatools.storage.moex;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.datatools.storage.SecuritySessionProperties;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SecuritySessionPropertiesEntity;

public class MOEXUtils {
	
	public MOEXUtils() {
		super();
	}
	
	public boolean isPropertiesEquals(SecuritySessionProperties p1,
			SecuritySessionProperties p2)
	{
		return new EqualsBuilder()
			.append(p1.getInitialMarginCost(), p2.getInitialMarginCost())
			.append(p1.getInitialPrice(), p2.getInitialPrice())
			.append(p1.getLotSize(), p2.getLotSize())
			.append(p1.getLowerPriceLimit(), p2.getLowerPriceLimit())
			.append(p1.getUpperPriceLimit(), p2.getUpperPriceLimit())
			.append(p1.getScale(), p2.getScale())
			.append(p1.getTickCost(), p2.getTickCost())
			.append(p1.getTickSize(), p2.getTickSize())
			.isEquals();
	}
	
	public void fillSessionProperties(Security security,
			SecuritySessionPropertiesEntity p)
	{
		p.setInitialMarginCost(security.getInitialMargin());
		p.setInitialPrice(security.getInitialPrice());
		p.setLotSize(security.getLotSize());
		p.setLowerPriceLimit(security.getMinPrice());
		p.setUpperPriceLimit(security.getMaxPrice());
		p.setScale(security.getPrecision());
		p.setTickCost(security.getMinStepPrice());
		p.setTickSize(security.getMinStepSize());
	}
	
	public void fillProperties(Security security, SecurityPropertiesEntity p) {
		p.setCurrencyOfCost(security.getCurrency());
		p.setDisplayName(security.getDisplayName());
	}
	
	public DateTime getClearingTime(Symbol symbol, DateTime time) {
		LocalTime dummyTime = time.toLocalTime(),
				clearing1 = new LocalTime(18, 45, 0),
				clearing2 = new LocalTime(23, 50, 0);
		LocalDate dummyDate = time.toLocalDate();
		if ( dummyTime.compareTo(clearing1) < 0 ) {
			return dummyDate.toDateTime(clearing1);
		}
		if ( symbol.getType() == SymbolType.FUT
		  || symbol.getType() == SymbolType.OPT )
		{
			return dummyDate.toDateTime(clearing2);
		} else {
			// This may work not good enough
			return dummyDate.plusDays(1).toDateTime(clearing1);
		}
	}

}
