package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;
import octopus.analysis.Evaluator;
import octopus.analysis.joern.JoernEvaluator;

public class JoernTransferFunctionIdentifierDecl extends JoernTransferFunction {
	String varname;
	String type;
	JoernTransferFunction assignment;
	public JoernTransferFunctionIdentifierDecl(Vertex v) {
		super(v);
		Vertex declType = getChildWithNumber(v,"0");
		Vertex declId = getChildWithNumber(v,"1");
		Vertex declAssignment = getChildWithNumber(v,"2");
		type = declType.value("code");
		varname = declId.value("code");
		if (declAssignment != null) {
			assignment = JoernTransferFunctionFactory.create(declAssignment);
		} else {
			assignment = null;
		}
	}

	public Lattice eval(Evaluator e,Lattice analysis) {
		if (assignment != null) {
			Lattice assignmentAnalysis;
			assignmentAnalysis = assignment.eval(e,analysis);
			return ((JoernEvaluator)e).evalIdentifierDecl(analysis, varname, assignmentAnalysis);
		} else {
			return ((JoernEvaluator)e).evalIdentifierDecl(analysis, varname);
		}
	}
}



