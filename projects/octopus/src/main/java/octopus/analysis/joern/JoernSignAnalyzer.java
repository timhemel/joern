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
	public JoernSignAnalyzer() {
		super();
		xferFunction = new HashMap<Vertex,TransferFunction>();
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

}
