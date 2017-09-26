package octopus.analysis;

public interface Lattice<L> {
	public L join(L other);
	public boolean leq(L other);
}


