package ru.prolib.aquila.probe.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.finam.CsvTickIteratorStorage;
import ru.prolib.aquila.core.utils.IdUtils;

public class PROBEDataStorage extends DataStorageImpl {
	private static final String DISPLAY_NAME = "DisplayName";
	private static final String LOT_SIZE = "LotSize";
	private static final String MIN_STEP_SIZE = "MinStepSize";
	private static final String PRICE_PRECISION = "PricePrecision";
	private static final String INIT_MARGIN_BASE = "InitialMarginCalcBase";
	private static final String STEP_PRICE_BASE = "MinStepPriceCalcBase";
	private static final String INI_FILE_SUFFIX = ".ini";
	private final File root;
	private final IdUtils idUtils;
	
	public PROBEDataStorage(File root, IdUtils idUtils) {
		super();
		this.root = root;
		this.idUtils = idUtils;
		setIteratorStorage(new CsvTickIteratorStorage(root));
	}
	
	public PROBEDataStorage(File root) {
		this(root, new IdUtils());
	}
	
	public SecurityProperties getSecurityProperties(SecurityDescriptor descr)
		throws DataException
	{
		try {
			String pfx =idUtils.getSafeId(descr);
			Props props = new Props();
			props.load(new FileReader(new File(root,
					pfx + File.separator + pfx + INI_FILE_SUFFIX)));
			SecurityProperties sp = new SecurityProperties();
			sp.setDisplayName(props.getString(DISPLAY_NAME));
			sp.setInitialMarginCalcBase(props.getDouble(INIT_MARGIN_BASE, 0d));
			sp.setLotSize(props.getInteger(LOT_SIZE));
			sp.setMinStepSize(props.getDouble(MIN_STEP_SIZE));
			sp.setPricePrecision(props.getInteger(PRICE_PRECISION));
			sp.setStepPriceCalcBase(props.getDouble(STEP_PRICE_BASE, 0d));
			return sp;
		} catch ( IOException e ) {
			throw new DataException(e);
		}
	}

}
