package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;

public class JoernTransferFunctionCFGExitNode extends JoernTransferFunction {
	public JoernTransferFunctionCFGExitNode(Vertex v) {
		super(v);
	}
	public Lattice eval(Lattice v) {
		return v;
	}
}


