package ru.prolib.aquila.core.timetable;

import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

/**
 * Фасад-конвертер периодов.
 */
public class PeriodConverter implements Converter {
	
	public PeriodConverter() {
		super();
	}

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
		return clazz == HMPeriod.class || clazz == DOWPeriod.class;
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		writer.addAttribute("value", value.toString());
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		String type = reader.getAttribute("class");
		String value = reader.getAttribute("value");
		try {
			if ( "DayOfWeekPeriod".equals(type) ) {
				return DOWPeriod.parse(value);
			} else if ( "HourMinutePeriod".equals(type) ) {
				return HMPeriod.parse(value);
			} else {
				throw new ConversionException("Unknown period type: " + type);
			}			
		} finally {

		}
	}

}
