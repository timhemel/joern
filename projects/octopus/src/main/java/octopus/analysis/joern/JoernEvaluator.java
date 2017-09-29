package octopus.analysis.joern;

import octopus.analysis.Evaluator;
import octopus.analysis.Lattice;

public abstract class JoernEvaluator<L extends Lattice> extends Evaluator<L> {
	public abstract L evalCFGEntryNode(L analysis);
	public abstract L evalCFGExitNode(L analysis);
	public abstract L evalCondition(L analysis);
	public abstract L evalAssignmentExpression(L analysis, String varname, Lattice value);
	public abstract Lattice evalIdentifier(L analysis, String varname);
	public abstract Lattice evalPrimaryExpression(String value);
}
