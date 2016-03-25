package rtlib.core.variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventPropagator
{
	private static final ThreadLocal<EventPropagator> sEventPropagatorThreadLocal = new ThreadLocal<EventPropagator>();

	public static final EventPropagator getEventPropagator()
	{
		EventPropagator lEventPropagator = sEventPropagatorThreadLocal.get();
		if (lEventPropagator == null)
		{
			lEventPropagator = new EventPropagator();
			sEventPropagatorThreadLocal.set(lEventPropagator);
		}
		return lEventPropagator;
	}

	public static final void clear()
	{
		getEventPropagator().mTraversedObjectList.clear();
	}

	public static final void add(final Object pObject)
	{
		getEventPropagator().mTraversedObjectList.add(pObject);
	}

	public static final boolean hasBeenTraversed(final Object pObject)
	{
		return getEventPropagator().mTraversedObjectList.contains(pObject);
	}

	public static final boolean hasNotBeenTraversed(final Object pObject)
	{
		return !getEventPropagator().mTraversedObjectList.contains(pObject);
	}

	public static final ArrayList<Object> getListOfTraversedObjects()
	{
		return getEventPropagator().mTraversedObjectList;
	}

	public static final ArrayList<Object> getCopyOfListOfTraversedObjects()
	{
		return new ArrayList<Object>(getEventPropagator().mTraversedObjectList);
	}

	public static void setListOfTraversedObjects(final List<Object> pListOfTraversedObjects)
	{
		final ArrayList<Object> lTraversedObjectList = getEventPropagator().mTraversedObjectList;
		lTraversedObjectList.clear();
		lTraversedObjectList.addAll(pListOfTraversedObjects);
	}

	public static void addAllToListOfTraversedObjects(final Collection<?> pListOfTraversedObjects)
	{
		final ArrayList<Object> lTraversedObjectList = getEventPropagator().mTraversedObjectList;
		lTraversedObjectList.addAll(pListOfTraversedObjects);
	}

	private final ArrayList<Object> mTraversedObjectList = new ArrayList<Object>();

	public EventPropagator()
	{
		super();
	}

}
