package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;
import octopus.analysis.Evaluator;
import octopus.analysis.joern.JoernEvaluator;

public class JoernTransferFunctionCondition extends JoernTransferFunction {
	public JoernTransferFunctionCondition(Vertex v) {
		super(v);
	}
	public Lattice eval(Evaluator e,Lattice analysis) {
		return ((JoernEvaluator)e).evalCondition(analysis);
	}
}
