package rtlib.core.math.kdtree;

/**
 * Min Heap
 *
 * @param <T>
 *            type stored in Heap
 */
public interface MinHeap<T>
{
	public int size();

	public void offer(double key, T value);

	public void replaceMin(double key, T value);

	public void removeMin();

	public T getMin();

	public double getMinKey();
}
