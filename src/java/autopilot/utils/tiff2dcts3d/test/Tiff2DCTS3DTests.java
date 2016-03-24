package autopilot.utils.tiff2dcts3d.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import autopilot.utils.tiff2dcts3d.Tiff2DCTS3D;

public class Tiff2DCTS3DTests
{

	File getStackFile() throws IOException
	{
		URL lURL = Tiff2DCTS3DTests.class.getResource("stack/test.stack.tif");
		File lTempFile = File.createTempFile(	Tiff2DCTS3DTests.class.getSimpleName(),
																					"dcts3d");
		FileUtils.copyURLToFile(lURL, lTempFile);
		return lTempFile;
	}

	@Test
	public void dcts3d() throws IOException
	{
		File lTempFile = getStackFile();

		final double lDcts3d = Tiff2DCTS3D.dcts3d(lTempFile, 3, 3);
		checkValue(lDcts3d);
	}

	@Test
	public void dcts2d() throws IOException
	{
		File lTempFile = getStackFile();

		final double[] lDcts2d = Tiff2DCTS3D.dcts2d(lTempFile, 3);
		for (int i = 4; i < lDcts2d.length; i++)
		{
			final double lValue = lDcts2d[i];
			// System.out.println(lValue);
			checkValue(lValue);
		}
	}

	void checkValue(final double lValue)
	{
		assertTrue(lValue > 0);
		assertFalse(Double.isNaN(lValue));
		assertTrue(Double.isFinite(lValue));
	}
}
