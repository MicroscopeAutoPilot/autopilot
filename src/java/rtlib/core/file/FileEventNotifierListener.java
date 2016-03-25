package rtlib.core.file;

import java.io.File;

import rtlib.core.file.FileEventNotifier.FileEventKind;

public interface FileEventNotifierListener
{

	void fileEvent(	FileEventNotifier pThis,
					File pFile,
					FileEventKind pEventKind);

}
