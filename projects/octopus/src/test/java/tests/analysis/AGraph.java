package tests.analysis;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;

public class AGraph {

	Map<Object,Vertex> nodes;
	Set<Edge> edges;
	
	AGraph() {
		edges = new HashSet<Edge>();
	}

	AGraph withNode(Vertex v) {
		return this;
	}

	AGraph withEdge(String label, Object fromNodeId, Object toNodeId) {
		return this;
	}

	Set<Edge> edges() {
		return edges;
	}
}

