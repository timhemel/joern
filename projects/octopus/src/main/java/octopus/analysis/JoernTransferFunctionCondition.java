package octopus.analysis;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class JoernTransferFunctionCondition extends JoernTransferFunction {
	public JoernTransferFunctionCondition(Vertex v) {
		super(v);
	}
	public Lattice eval(Lattice v) {
		return v;
	}
}
