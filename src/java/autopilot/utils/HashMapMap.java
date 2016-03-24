package autopilot.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashMapMap<K1, K2, V> extends HashMap<K1, Map<K2, V>>
{

	public V put(final K1 pKey1, final K2 pKey2, final V pValue)
	{
		Map<K2, V> lMap = get(pKey1);
		if (lMap == null)
		{
			lMap = new HashMap<K2, V>();
			put(pKey1, lMap);
		}
		return lMap.put(pKey2, pValue);
	}

	public V remove(final String pKey1, final String pKey2)
	{
		final Map<K2, V> lMap = get(pKey1);
		if (lMap != null)
		{
			return lMap.remove(pKey2);
		}
		return null;
	}

	public V get(final K1 pKey1, final K2 pKey2)
	{
		final Map<K2, V> lMap = get(pKey1);
		if (lMap == null)
		{
			return null;
		}
		return lMap.get(pKey2);
	}

	public Set<K2> get2ndKeySet(final K1 pKey1)
	{
		final Map<K2, V> lMap = get(pKey1);

		if (lMap != null)
		{
			return lMap.keySet();
		}
		else
		{
			return Collections.emptySet();
		}
	}

}
