package autopilot.stackanalysis.main;

import java.io.File;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class CommandLine
{

	@Argument(required = true, index = 0, metaVar = "FILE", usage = "File or folder to analyse.")
	public String mFolderOrFilePath = ".";

	@Option(name = "-w", aliases =
	{ "--stripe-width" }, usage = "stripe width.")
	public int mStripeWidth = 32;

	@Option(name = "-o", aliases =
	{ "--stripe-overlap" }, usage = "stripe overlapp.")
	public int mStripeOverlap = 16;

	@Option(name = "-p", aliases =
	{ "--pixel-size" }, usage = "lateral pixel size (preferably microns).")
	public double mPixelSizeInMicrons = 0.406;

	@Option(name = "-t", aliases =
	{ "--probability-threshold" }, usage = "probability threshold for fit.")
	public double mProbabilityThreshold = 0.99;

	@Option(name = "-d", aliases =
	{ "--max-depth" }, usage = "max depth penetration for light-sheet.")
	public double mMaxLightSheetPenetrationDepthInMicrons = 70;

	@Option(name = "-s", aliases =
	{ "--psf-support-diameter" }, usage = "PSF support diameter.")
	public double mPSFSupportDiameter = 3;

	@Option(name = "-z", aliases =
	{ "--focus-step-size" }, usage = "focus step size.")
	public double mFocusZStep = 1;

	public boolean isFolder()
	{
		if (mFolderOrFilePath == null)
			return false;
		final File lFile = getFile();
		return lFile.isDirectory();
	}

	public File getFile()
	{
		final File lFile = new File(mFolderOrFilePath);
		return lFile;
	}

}
