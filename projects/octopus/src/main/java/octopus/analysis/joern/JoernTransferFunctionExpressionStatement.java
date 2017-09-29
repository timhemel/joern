package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Direction;
import octopus.analysis.Lattice;
import octopus.analysis.Evaluator;
import octopus.analysis.joern.JoernEvaluator;

public class JoernTransferFunctionExpressionStatement extends JoernTransferFunction {
	JoernTransferFunction expression;
	public JoernTransferFunctionExpressionStatement(Vertex v) {
		super(v);
		Vertex child = v.vertices(Direction.OUT,"IS_AST_PARENT").next();
		expression = JoernTransferFunctionFactory.create(child);
	}
	public Lattice eval(Evaluator e,Lattice v) {
		return expression.eval(e,v);
	}
}
