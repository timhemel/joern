package octopus.analysis;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class JoernTransferFunctionCFGExitNode extends JoernTransferFunction {
	public JoernTransferFunctionCFGExitNode(Vertex v) {
		super(v);
	}
	public Lattice eval(Lattice v) {
		return v;
	}
}


