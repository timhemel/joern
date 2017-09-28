package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;
import octopus.analysis.TransferFunction;

public class JoernTransferFunction extends TransferFunction {
	// base class
	public JoernTransferFunction(Vertex v) {
		super(v);
	}

	public Lattice eval(Lattice v) {
		return v;
	}
}

