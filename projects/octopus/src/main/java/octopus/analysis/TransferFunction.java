package octopus.analysis;


import org.apache.tinkerpop.gremlin.structure.Vertex;

import octopus.analysis.Evaluator;

public abstract class TransferFunction {
	public TransferFunction(Vertex v) { }
	public abstract Lattice eval(Evaluator e,Lattice v);
}
