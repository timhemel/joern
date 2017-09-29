package octopus.analysis;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import octopus.analysis.Lattice;

public class MapLattice<T extends Lattice> extends HashMap<String,T> implements Lattice<MapLattice> {
	public MapLattice join(MapLattice other) {
		MapLattice<T> ret = new MapLattice<T>();
		ret.putAll(this);
		for(String key: (Set<String>) other.keySet()) {
			T currentValue = (T) ret.get(key);
			T newValue = (T) other.get(key);
			if (currentValue != null) {
				ret.put(key, (T) (currentValue.join(newValue)) );
			} else {
				ret.put(key, newValue);
			}
		}
		return ret;
	}

	public boolean leq(MapLattice other) {
		return this.join(other).equals(other);
		// return this.join(other) == other;
	}

	public MapLattice<T> bottom() {
		return new MapLattice<T>();
	}
}

