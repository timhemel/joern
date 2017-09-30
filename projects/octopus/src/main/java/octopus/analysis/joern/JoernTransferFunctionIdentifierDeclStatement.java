package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;
import octopus.analysis.Evaluator;
import octopus.analysis.joern.JoernEvaluator;

public class JoernTransferFunctionIdentifierDeclStatement extends JoernTransferFunction {
	JoernTransferFunction decl;
	public JoernTransferFunctionIdentifierDeclStatement(Vertex v) {
		super(v);
		Vertex child = getChildWithNumber(v,"0");
		
		decl = JoernTransferFunctionFactory.create(child);
	}
	public Lattice eval(Evaluator e,Lattice analysis) {
		return decl.eval(e,analysis);
	}
}


