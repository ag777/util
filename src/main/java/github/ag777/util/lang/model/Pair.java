package github.ag777.util.lang.model;

import java.util.Objects;

public class Pair<K,V> {

	public K first;
	public V second;
	
	public Pair() {
	}

	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair<?, ?> p)) {
            return false;
        }
        return Objects.equals(p.first, first) && Objects.equals(p.second, second);
	}
}
