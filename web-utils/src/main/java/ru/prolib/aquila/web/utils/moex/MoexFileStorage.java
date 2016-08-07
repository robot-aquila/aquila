package ru.prolib.aquila.web.utils.moex;

import ru.prolib.aquila.data.storage.file.Files;

public class MoexFileStorage {
	private static final Files contractDetailsFiles;
	private static final String DEFAULT_CONTRACT_DETAILS_STORAGE_ID;
	
	static {
		DEFAULT_CONTRACT_DETAILS_STORAGE_ID = "MOEX_CONTRACT_DETAILS";
		contractDetailsFiles = new MoexContractDetailsFiles();
	}

}
