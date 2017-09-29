package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;
import octopus.analysis.Evaluator;
import octopus.analysis.joern.JoernEvaluator;

public class JoernTransferFunctionAdditiveExpression extends JoernTransferFunction {
	String varname;
	JoernTransferFunction expression1;
	JoernTransferFunction expression2;
	public JoernTransferFunctionAdditiveExpression(Vertex v) {
		super(v);
		Vertex child1 = getChildWithNumber(v,"0");
		Vertex child2 = getChildWithNumber(v,"1");
		expression1 = JoernTransferFunctionFactory.create(child1);
		expression2 = JoernTransferFunctionFactory.create(child2);
	}
	public Lattice eval(Evaluator e,Lattice analysis) {
		Lattice value1 = expression1.eval(e,analysis);
		Lattice value2 = expression2.eval(e,analysis);
		return ((JoernEvaluator)e).evalAdditiveExpression(value1, value2);
	}
}


