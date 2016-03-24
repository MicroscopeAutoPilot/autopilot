package autopilot.measures.test;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import autopilot.image.DoubleArrayImage;
import autopilot.image.readers.TiffReader;
import autopilot.measures.FocusMeasures;
import autopilot.measures.FocusMeasures.FocusMeasure;

public class FocusMeasureTests
{

	private void format(String pFormatString, Object... pObjectList)
	{
		System.out.format(pFormatString, pObjectList);
	}

	@Test
	public void testMeasures() throws IOException
	{
		testFocusMeasuresOn("stacks/example1.tif");
	}

	public void testFocusMeasuresOn(final String pTiffFileRessourcePath) throws IOException
	{

		for (final FocusMeasure lFocusMeasure : FocusMeasure.values())
		{

			final InputStream lDefocussedImageResourceAsStream = FocusMeasureTests.class.getResourceAsStream(pTiffFileRessourcePath);
			final DoubleArrayImage lDefocussedDoubleArrayImage = TiffReader.read(	lDefocussedImageResourceAsStream,
																																						0,
																																						null);

			final InputStream lFocussedImageResourceAsStream = FocusMeasureTests.class.getResourceAsStream(pTiffFileRessourcePath);
			final DoubleArrayImage lInFocusDoubleArrayImage = TiffReader.read(lFocussedImageResourceAsStream,
																																				23,
																																				null);

			final double lDefocussedValue = FocusMeasures.computeFocusMeasure(lFocusMeasure,
																																				lDefocussedDoubleArrayImage);
			final double lFocussedValue = FocusMeasures.computeFocusMeasure(lFocusMeasure,
																																			lInFocusDoubleArrayImage);

			assertFalse(Double.isNaN(lDefocussedValue));
			assertFalse(Double.isNaN(lFocussedValue));
			// assertTrue(lDefocussedValue < lFocussedValue);

			format(	"%s\tdefocussed:%g\tfocussed:%g\n",
							lFocusMeasure,
							lDefocussedValue,
							lFocussedValue);

			if (lDefocussedValue > lFocussedValue)
			{
				System.err.println("BAD FOCUS MEASURE: " + lFocusMeasure);
			}

		}
		// System.out.println("");

	}

}
