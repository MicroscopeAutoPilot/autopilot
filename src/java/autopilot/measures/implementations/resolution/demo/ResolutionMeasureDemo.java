package autopilot.measures.implementations.resolution.demo;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.image.readers.TiffReader.TiffStackCallBack;
import autopilot.measures.implementations.resolution.DFTIsotropicResolutionMeasure;
import autopilot.measures.implementations.resolution.DFTResolutionMeasure;
import autopilot.measures.implementations.spectral.NormDCTEntropyShannon;

public class ResolutionMeasureDemo
{

	@Test
	public void testIsoRes() throws IOException
	{
		final File lTiffFile1 = new File("/Users/royer/Projects/AutoPilot/datasets/astigmatism/20150310_181051.Wavelength5.Plane08.TM000_CAM0_LS0_DOF-TL1.tif");
		final File lTiffFile2 = new File("/Users/royer/Projects/AutoPilot/datasets/astigmatism/20150310_181051.Wavelength2.Plane34.TM000_CAM1_LS0_DOF-TL1.tif");
		final File lTiffFile3 = new File("/Users/royer/Projects/AutoPilot/datasets/astigmatism/20150310_181051.Wavelength2.Plane03.TM000_CAM0_LS0_DOF-TL1.tif");

		// 20150310_181051.Wavelength2.Plane03.TM000_CAM0_LS0_DOF-TL1.tif

		TiffReader.readTiffStack(lTiffFile3, new TiffStackCallBack()
		{
			@Override
			public boolean image(	final int pImageIndex,
														final DoubleArrayImage pDoubleArrayImage)
			{
				// System.out.format("ImageIndex=%d \n", pImageIndex);

				final double lIsotropicMeasure = DFTIsotropicResolutionMeasure.compute(	pDoubleArrayImage,
																																								3,
																																								12);

				final double lDCTSMeasure = NormDCTEntropyShannon.compute(pDoubleArrayImage,
																																	3);

				System.out.format("%g\t%g\n", lDCTSMeasure, lIsotropicMeasure);

				return true;
			}
		},
															null);
	}

	@Test
	public void testResolution() throws IOException
	{
		final File lTiffFileTEST = new File("/Users/royer/Projects/AutoPilot/datasets/mEstimateResolution/TESTLOWRES.tif");
		final File lTiffFile1 = new File("/Users/royer/Projects/AutoPilot/datasets/mEstimateResolution/MainFig2_TP299.tif");
		final File lTiffFile2 = new File("/Users/royer/Projects/AutoPilot/datasets/mEstimateResolution/MainFig2_TP602.tif");
		final File lTiffFile3 = new File("/Users/royer/Projects/AutoPilot/datasets/mEstimateResolution/MainFig1.tif");

		TiffReader.readTiffStack(lTiffFile3, new TiffStackCallBack()
		{
			@Override
			public boolean image(	final int pImageIndex,
														final DoubleArrayImage pDoubleArrayImage)
			{
				// System.out.format("ImageIndex=%d \n", pImageIndex);

				final double lIsotropicMeasure = DFTResolutionMeasure.compute(pDoubleArrayImage,
																																			3,
																																			256);


				System.out.format("%g\t%g\t%g\n",
													lIsotropicMeasure,
													2 * lIsotropicMeasure,
													0.408 / lIsotropicMeasure);

				return true;
			}
		},
															null);
	}

}
