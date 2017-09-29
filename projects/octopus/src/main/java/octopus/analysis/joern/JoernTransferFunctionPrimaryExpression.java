package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;
import octopus.analysis.Evaluator;
import octopus.analysis.joern.JoernEvaluator;

public class JoernTransferFunctionPrimaryExpression extends JoernTransferFunction {
	String value;
	public JoernTransferFunctionPrimaryExpression(Vertex v) {
		super(v);
		value = v.value("code");
	}
	public Lattice eval(Evaluator e, Lattice analysis) {
		return ((JoernEvaluator)e).evalPrimaryExpression(value);
	}
}


