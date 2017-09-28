package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;

public class JoernTransferFunctionIdentifier extends JoernTransferFunction {
	String varname;
	public JoernTransferFunctionIdentifier(Vertex v) {
		super(v);
		varname = v.value("code");
	}
	public Lattice eval(Lattice v) {
		return v;
	}
}

