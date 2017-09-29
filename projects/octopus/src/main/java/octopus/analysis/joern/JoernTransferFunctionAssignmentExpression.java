package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;
import octopus.analysis.Evaluator;
import octopus.analysis.joern.JoernEvaluator;

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
	/**
	 * Assignment expression assumes no side-effects in expressions,
	 * i.e. when evaluating the expression, no variables are changed.
	 */
	public Lattice eval(Evaluator e,Lattice analysis) {
		Lattice value = expression.eval(e,analysis);
		return ((JoernEvaluator)e).evalAssignmentExpression(analysis, varname, value);
	}
}

