package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;

public class JoernTransferFunctionCondition extends JoernTransferFunction {
	public JoernTransferFunctionCondition(Vertex v) {
		super(v);
	}
	public Lattice eval(Lattice v) {
		return v;
	}
}
