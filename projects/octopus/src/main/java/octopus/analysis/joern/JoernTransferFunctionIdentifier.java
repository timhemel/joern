package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;
import octopus.analysis.Evaluator;
import octopus.analysis.joern.JoernEvaluator;

public class JoernTransferFunctionIdentifier extends JoernTransferFunction {
	String varname;
	public JoernTransferFunctionIdentifier(Vertex v) {
		super(v);
		varname = v.value("code");
	}
	public Lattice eval(Evaluator e, Lattice analysis) {
		return ((JoernEvaluator)e).evalIdentifier(analysis, varname);
	}
}

