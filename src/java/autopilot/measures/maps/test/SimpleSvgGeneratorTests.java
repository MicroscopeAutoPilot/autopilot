package autopilot.measures.maps.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import autopilot.image.DoubleArrayImage;
import autopilot.utils.svg.SimpleSVGGenerator;

public class SimpleSvgGeneratorTests
{

	@Test
	public void testSVGFileGeneration() throws IOException
	{
		try {
			File lTempFile = File.createTempFile(	this.getClass()
                                                                                                .getSimpleName(),
                                                                                        "testSVGFileGeneration");

			final SimpleSVGGenerator lSimpleSVGGenerator = new SimpleSVGGenerator(lTempFile,
                                                                                                                                                        256,
                                                                                                                                                        256);

			final DoubleArrayImage lDoubleArrayImage = new DoubleArrayImage(256,
                                                                                                                                            256);
			lSimpleSVGGenerator.addPngImage("demo.png",
                                                                            lDoubleArrayImage,
                                                                            0,
                                                                            0,
                                                                            256,
                                                                            256);
			lSimpleSVGGenerator.addRectangle(	10,
                                                                                10,
                                                                                100,
                                                                                100,
                                                                                "blue",
                                                                                "red",
                                                                                2,
                                                                                0.5);

			lSimpleSVGGenerator.close();

			assertTrue(lTempFile.exists());
		} catch (java.awt.HeadlessException e)
		{

		}

	}

}
