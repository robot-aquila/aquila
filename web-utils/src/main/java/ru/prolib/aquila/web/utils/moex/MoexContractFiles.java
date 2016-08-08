package ru.prolib.aquila.web.utils.moex;

import ru.prolib.aquila.data.storage.file.Files;

public class MoexContractFiles implements Files {

	@Override
	public String getRegularSuffix() {
		return "-moex-contract-details-daily.txt";
	}

	@Override
	public String getTemporarySuffix() {
		return "-moex-contract-details-daily.tmp";
	}

}
