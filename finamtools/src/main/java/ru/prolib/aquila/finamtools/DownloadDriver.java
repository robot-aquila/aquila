package ru.prolib.aquila.finamtools;

import java.io.File;

public interface DownloadDriver {
	
	public File download(FinamDownloader downloader, String attachmentFileName);

}
