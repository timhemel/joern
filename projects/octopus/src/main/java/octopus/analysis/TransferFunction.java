package octopus.analysis;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class TransferFunction {
	public static TransferFunction create(Vertex v) {
		return new TransferFunction(v);
	}
	public TransferFunction(Vertex v) {
	}
	public Lattice eval(Lattice v) {
		return v;
	}
}
