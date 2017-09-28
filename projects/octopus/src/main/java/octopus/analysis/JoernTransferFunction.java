package octopus.analysis;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class JoernTransferFunction extends TransferFunction {
	// base class
	public JoernTransferFunction(Vertex v) {
		super(v);
	}

	public Lattice eval(Lattice v) {
		return v;
	}
}

