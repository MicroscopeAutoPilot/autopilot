package autopilot.main;

import java.io.File;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class CommandLine
{

	@Argument(required = true, index = 0, metaVar = "FILE", usage = "Tiff file to analyse or folder to run mComputeBenchmark on.")
	public String mFolderOrFilePath = ".";

	@Option(name = "-map", aliases =
	{ "--focus-map" }, usage = "computes a focus map for TIFF stack")
	public boolean mComputeFocusMap;

	@Option(name = "-psf", aliases =
	{ "--psf-support-diameter" }, usage = "PSF support diameter.")
	public double mPSFSupportDiameter = 3;

	@Option(name = "-dz", aliases =
	{ "--focus-step-size" }, usage = "focus step size.")
	public double mFocusZStep = 1;

	@Option(name = "-fp", aliases =
	{ "--fit-prob" }, usage = "Fit probability (p-value) threshold")
	public double mFitProbability;

	@Option(name = "-curve", aliases =
	{ "--focus-curve" }, usage = "Output focus curve")
	public boolean mComputeFocusCurve;

	@Option(name = "-VerboseLog", aliases =
	{ "--verbose-log" }, usage = "Verbose logging")
	public boolean mVerboseLog;

	@Option(name = "-wlog", aliases =
	{ "--window-log" }, usage = "Output log in dedicated window")
	public boolean mOpenLogWindow;

	@Option(name = "-b", aliases =
	{ "--computebenchmark" }, usage = "Benchmarks focus measures on folder full of TIFF files")
	public boolean mComputeBenchmark;
	
	@Option(name = "-sfft", aliases =
	{ "--stackfft" }, usage = "Computes the FFT power spectrum of each XY slices of a stack." )
	public boolean mXYStackFFT;

	@Option(name = "-ff", aliases =
	{ "--file-filter" }, usage = "Benchmark: file filter")
	public String mFileFilter = "";

	@Option(name = "-mf", aliases =
	{ "--measure-filter" }, usage = "Benchmark: measure filter")
	public String mMeasureFilter = "";

	@Option(name = "-a", aliases =
	{ "--compute-angles" }, usage = "Computes the lightsheet angle in defocus stack")
	public boolean mComputeAngles = false;

	@Option(name = "-vl", aliases =
	{ "--visualise-lightsheet" }, usage = "Visualize light-sheet in 3D")
	public boolean mVisualise = true;

	@Option(name = "-io", aliases =
	{ "--image-orientation" }, usage = "Benchmark: measure filter")
	public double mOrientation = 0;

	@Option(name = "-ka", aliases =
	{ "--keep-alive" }, usage = "Keep process alive")
	public boolean mKeepAlive = false;

	@Option(name = "-er", aliases =
	{ "--estimate-resolution" }, usage = "Estimate image resolution")
	public boolean mEstimateResolution = false;

	@Option(name = "-nb", aliases =
	{ "--number-bins" }, usage = "Number of bins")
	protected int mNumberOfbins = 256;

	@Option(name = "-lr", aliases =
	{ "--lateral-resolution" }, usage = "Lateral resolution (e.g. um)")
	protected double mLateralResolutionUm = 0.406;

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
