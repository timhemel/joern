package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;

public class JoernTransferFunctionPrimaryExpression extends JoernTransferFunction {
	int value;
	public JoernTransferFunctionPrimaryExpression(Vertex v) {
		super(v);
		// Joern does not store type information in the AST
		// assume that we only work with integers
		value = new Integer(v.value("code")).intValue();
	}
	public Lattice eval(Lattice v) {
		// return v.bottom();
		return v;
	}
}


