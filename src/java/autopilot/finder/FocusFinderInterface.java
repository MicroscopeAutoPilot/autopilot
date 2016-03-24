package autopilot.finder;

import autopilot.image.DoubleArrayImage;

/**
 * Interface for all focus finder classes. You add images and their 'position'
 * to a focus finder and then query the best 'position' (e.g. the z position)
 * according to to how 'sharp' i.e. focused are the images. A focus finder can
 * be reused by calling clear. A focus finder should cache the result of the
 * sharpness computations by keeping track of the addition of new images with
 * respect to calls to geBestposition or getBestFocusMeasure or
 * getFocusAmplitude.
 * 
 * @author royer
 * 
 * @param <O>
 *          Class representing the type of 'position'
 */
public interface FocusFinderInterface<O>
{

	/**
	 * Clears this focus finder from all added images. The state of the focus
	 * finder is the same as when it was just created.
	 */
	void clear();

	/**
	 * Adds an image at a given position. The position can be a Double or another
	 * object that represents some degree(s) of freedom.
	 * 
	 * @param pPosition
	 *          position
	 * @param pDoubleArrayImage
	 *          image
	 * @return computed measure
	 */
	double addImage(O pPosition, DoubleArrayImage pDoubleArrayImage);

	/**
	 * Removes an image from the focus finder.
	 * 
	 * @param pPosition
	 */
	void removeImage(O pPosition);

	/**
	 * Returns the position that corresponds to the sharpest image.
	 * 
	 * @return position corresponding to sharpest image
	 */
	O getBestPosition();

	/**
	 * Returns the focus measure corresponding to the sharpest image.
	 * 
	 * @return computed measure
	 */
	double getBestFocusMeasure();

	/**
	 * Returns the amplitude of variation of the focus measure values over all
	 * added images.
	 * 
	 * @return ficus amplitude
	 */
	double getFocusAmplitude();

	/**
	 * Returns the name of this focus finder.
	 * 
	 * @return name
	 */
	String getName();

}
