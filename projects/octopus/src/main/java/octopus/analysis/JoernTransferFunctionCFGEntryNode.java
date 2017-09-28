package octopus.analysis;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class JoernTransferFunctionCFGEntryNode extends JoernTransferFunction {
	public JoernTransferFunctionCFGEntryNode(Vertex v) {
		super(v);
	}
	public Lattice eval(Lattice v) {
		return v;
	}
}

