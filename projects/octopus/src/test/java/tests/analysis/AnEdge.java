package tests.analysis;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;

public class AnEdge {
	String label;
	Edge edge;
	Vertex inV, outV;

	AnEdge() {
	}

	AnEdge withLabel(String label) {
		this.label = label;
		return this;
	}
	AnEdge from(Vertex outV) {
		this.outV = outV;
		return this;
	}
	AnEdge to(Vertex inV) {
		this.inV = inV;
		return this;
	}

	AnEdge inGraph(Graph g) {
		outV.addEdge(label,inV);
		return this;
	}

	Edge get() {
		return edge;
	}
}
