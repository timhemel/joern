package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;

public class JoernTransferFunctionAssignmentExpression extends JoernTransferFunction {
	String varname;
	JoernTransferFunction expression;
	public JoernTransferFunctionAssignmentExpression(Vertex v) {
		super(v);
		Vertex varnode = getChildWithNumber(v,"0");
		varname = varnode.value("code");
		Vertex child = getChildWithNumber(v,"1");
		expression = JoernTransferFunctionFactory.create(child);
	}
	public Lattice eval(Lattice v) {
		return expression.eval(v);
	}
}

