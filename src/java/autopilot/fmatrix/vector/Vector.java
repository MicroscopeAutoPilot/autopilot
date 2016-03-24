package autopilot.fmatrix.vector;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;

/**
 * Vector superclass of which state and observation vectors are subclasses.
 * provides basic machinery for constructing a vector setting and getting its
 * coefficient values.
 * 
 * @author royer
 */
public abstract class Vector
{
	final private SimpleMatrix mSimpleMatrix;

	protected int mNumberOfWavelengths;
	protected int mNumberOfPlanes;
	protected int mNumberOfCameras;
	protected int mNumberOfLightSheets;
	protected int mSingleColorAndPlaneStateVectorLength;

	/**
	 * Constructs a vector given number of wavelengths, planes, and number of DOFs
	 * per plane. This is mostly useful for extra orthogonal DOFS and is really
	 * meant as a convenience constructor for that particlar case.
	 * 
	 * @param pNumberOfWavelengths
	 *          number of colors
	 * @param pNumberOfPlanes
	 *          number of planes
	 * @param pNumberOfDOFs
	 *          number of DOFs
	 */
	public Vector(int pNumberOfWavelengths,
								int pNumberOfPlanes,
								int pNumberOfDOFs)
	{
		mNumberOfWavelengths = pNumberOfWavelengths;
		mNumberOfPlanes = pNumberOfPlanes;
		mNumberOfCameras = 0;
		mNumberOfLightSheets = 0;

		mSingleColorAndPlaneStateVectorLength = pNumberOfDOFs;

		mSimpleMatrix = new SimpleMatrix(	mNumberOfWavelengths * mNumberOfPlanes
																					* mSingleColorAndPlaneStateVectorLength,
																			1);
	}

	/**
	 * Constructs a vector for the given number of wavelengths, planes, cameras,
	 * and light-sheets as well as number of DOF for illumination and detections
	 * axis.
	 * 
	 * @param pNumberOfWavelengths
	 *          number of colors
	 * @param pNumberOfPlanes
	 *          number of planes per color
	 * @param pNumberOfCameras
	 *          number of cameras
	 * @param pNumberOfLightSheets
	 *          number of light-sheets
	 * @param pDOFDetection
	 *          number of degrees of freedom for each detection arm
	 * @param pDOFIllumination
	 *          number of degrees of freedom for each illumination arm
	 */
	public Vector(final int pNumberOfWavelengths,
								final int pNumberOfPlanes,
								final int pNumberOfCameras,
								final int pNumberOfLightSheets,
								final int pDOFDetection,
								final int pDOFIllumination)
	{
		super();
		mNumberOfWavelengths = pNumberOfWavelengths;
		mNumberOfPlanes = pNumberOfPlanes;
		mNumberOfCameras = pNumberOfCameras;
		mNumberOfLightSheets = pNumberOfLightSheets;

		mSingleColorAndPlaneStateVectorLength = computeSingleColorAndPlaneStateVectorLength(pNumberOfCameras,
																																												pNumberOfLightSheets,
																																												pDOFDetection,
																																												pDOFIllumination);
		mSimpleMatrix = new SimpleMatrix(	mNumberOfWavelengths * mNumberOfPlanes
																					* mSingleColorAndPlaneStateVectorLength,
																			1);

	}
	
	
	public int getNumberOfWavelengths()
	{
		return mNumberOfWavelengths;
	}
	
	public int getNumberOfPlanes()
	{
		return mNumberOfPlanes;
	}
	
	public int getNumberOfCameras()
	{
		return mNumberOfCameras;
	}
	
	public int getNumberOfLightsheets()
	{
		return mNumberOfLightSheets;
	}

	protected abstract int computeSingleColorAndPlaneStateVectorLength(	int pNumberOfCameras,
																																			int pNumberOfLightSheets,
																																			int pDOFDetection,
																																			int pDOFIllumination);

	/**
	 * Sets the vector's coefficient value at position {@code pIndex}
	 * 
	 * @param pIndex
	 *          index
	 * @param pValue
	 *          value
	 */
	public void setValueAt(final int pIndex, final double pValue)
	{
		mSimpleMatrix.set(pIndex, 0, pValue);
	}

	/**
	 * Returns the vector's coefficient value at position {@code pIndex}
	 * 
	 * @param pIndex
	 *          index
	 * @return value
	 */
	public double getValueAt(final int pIndex)
	{
		return mSimpleMatrix.get(pIndex, 0);
	}

	/**
	 * Sets this vector to the values given in the array {@code pVectorAsArray}
	 * 
	 * @param pVectorAsArray
	 *          vector as array
	 */
	public void getFrom(final double[] pVectorAsArray)
	{
		mSimpleMatrix.setColumn(0, 0, pVectorAsArray);
	}

	/**
	 * Gets the values from this vector into the given array
	 * 
	 * @param pVectorAsArray
	 *          vector as array
	 */
	public void copyTo(final double[] pVectorAsArray)
	{
		final int lNumberOfRows = mSimpleMatrix.numRows();
		for (int r = 0; r < lNumberOfRows; r++)
		{
			pVectorAsArray[r] = mSimpleMatrix.get(r, 0);
		}
	}

	/**
	 * Returns the corresponding {@code SimpleMatrix}
	 * 
	 * @return 'simple' matrix
	 */
	public final SimpleMatrix getSimpleMatrix()
	{
		return mSimpleMatrix;
	}

	/**
	 * Returns the corresponding {@code DenseMatrix64F}
	 * 
	 * @return 'dense' matrix
	 */
	public final DenseMatrix64F getMatrix()
	{
		return mSimpleMatrix.getMatrix();
	}

	/**
	 * Returns the number of elements in the vector
	 * 
	 * @return number of elements
	 */
	public int getNumElements()
	{
		return mSimpleMatrix.getNumElements();
	}

	@Override
	public String toString()
	{
		return String.format("Vector [mSimpleMatrix=%s]", mSimpleMatrix);
	}
}
