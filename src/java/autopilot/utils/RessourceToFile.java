package autopilot.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class RessourceToFile
{
	private File copyRessourceToTempFile(	final Class pClass,
																				final String pRessource) throws IOException
	{
		final URL lResourceFile = pClass.getResource(pRessource);
		final File lTempFile = File.createTempFile(	FilenameUtils.getBaseName(lResourceFile.getFile()),
																								FilenameUtils.getExtension(lResourceFile.getFile()));
		IOUtils.copy(	lResourceFile.openStream(),
									FileUtils.openOutputStream(lTempFile));
		return lTempFile;
	}
}
