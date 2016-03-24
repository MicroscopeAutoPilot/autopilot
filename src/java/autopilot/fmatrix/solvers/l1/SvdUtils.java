package autopilot.fmatrix.solvers.l1;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

/**
 * Class containing static methods for Singular Value Decomposition.
 * 
 * @author royer
 */
public class SvdUtils
{
	/**
	 * Return the diagonal matrix from the SVD of a given matrix.
	 * 
	 * @param pMatrix
	 *          matrix
	 * @return diagonal matrix
	 */
	public static final SimpleMatrix svddiag(final DenseMatrix64F pMatrix)
	{
		final SimpleSVD lSvd = SimpleMatrix.wrap(pMatrix).svd();

		return lSvd.getW();
	}

	/**
	 * Return the kernel matrix for a given SVD. The kernel is defined by the span
	 * of eigenvectors of eigenvalue norm below epsilon.
	 * 
	 * @param pSvd
	 *          SVD
	 * @param pEpsilon
	 *          below this value the eigenvalue is considered zero.
	 * @return kernel matrix
	 */
	public static final SimpleMatrix kernelMatrix(final SimpleSVD pSvd,
																								final double pEpsilon)
	{
		final SimpleMatrix lSVDDiagMatrix = pSvd.getW();
		final SimpleMatrix lTransformIn = pSvd.getV();

		final SimpleMatrix lSVDDiagonal = lSVDDiagMatrix.extractDiag();

		final int lKernelDim = kernelDim(pSvd, pEpsilon);

		final SimpleMatrix lKernelMatrix = new SimpleMatrix(lTransformIn.numRows(),
																												lKernelDim);

		for (int i = 0, j = 0; i < lSVDDiagonal.getNumElements(); i++)
		{
			final double lDiagValue = lSVDDiagonal.get(i);
			if (Math.abs(lDiagValue) < pEpsilon)
			{
				for (int k = 0; k < lTransformIn.numRows(); k++)
				{
					final double lNullSpaceVectorValue = lTransformIn.get(k, i);
					lKernelMatrix.set(k, j, lNullSpaceVectorValue);
				}
				j++;
			}
		}

		return lKernelMatrix;
	}

	/**
	 * Returns the dimension of the kernel of an SVD
	 * 
	 * @param pSvd
	 *          svd
	 * @param pEpsilon
	 *          below this value the eigenvalue is considered zero.
	 * @return kernel dimension
	 */
	public static final int kernelDim(final SimpleSVD pSvd,
																		final double pEpsilon)
	{
		final SimpleMatrix lSVDDiagMatrix = pSvd.getW();

		final SimpleMatrix lSVDDiagonal = lSVDDiagMatrix.extractDiag();

		int lNumberOfZeros = 0;
		for (int i = 0; i < lSVDDiagonal.getNumElements(); i++)
		{
			final double lValue = lSVDDiagonal.get(i);
			if (Math.abs(lValue) < pEpsilon)
			{
				lNumberOfZeros++;
			}
		}

		return lNumberOfZeros;
	}
}
