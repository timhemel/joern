package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Direction;
import octopus.analysis.Lattice;

public class JoernTransferFunctionExpressionStatement extends JoernTransferFunction {
	JoernTransferFunction expression;
	public JoernTransferFunctionExpressionStatement(Vertex v) {
		super(v);
		Vertex child = v.vertices(Direction.OUT,"IS_AST_PARENT").next();
		expression = JoernTransferFunctionFactory.create(child);
	}
	public Lattice eval(Lattice v) {
		return expression.eval(v);
	}
}
