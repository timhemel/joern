package octopus.analysis.joern;

import java.util.HashMap;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import octopus.analysis.Analyzer;
import octopus.analysis.Lattice;
import octopus.analysis.SignLattice;
import octopus.analysis.MapLattice;
import octopus.analysis.TransferFunction;

public class JoernSignAnalyzer extends Analyzer {
	HashMap<Vertex,TransferFunction> xferFunction;
	JoernSignEvaluator evaluator;

	public JoernSignAnalyzer() {
		super();
		xferFunction = new HashMap<Vertex,TransferFunction>();
		evaluator = new JoernSignEvaluator();
	}

	public void addVertex(Vertex v) {
		super.addVertex(v);
		if (!xferFunction.containsKey(v)) {
			xferFunction.put(v, JoernTransferFunctionFactory.create(v));
		}
	}

	protected Lattice bottom() {
		return new MapLattice<Lattice<SignLattice>>();
	}

	public Lattice join(Lattice a, Lattice b) {
		return ((MapLattice<Lattice<SignLattice>>)a).join((MapLattice<Lattice<SignLattice>>)b);
	}

	public Lattice trans(Vertex v) {
		TransferFunction f = xferFunction.get(v);
		Lattice a = results.get(v);
		return f.eval(evaluator,a);
	}

}
