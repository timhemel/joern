package tests.analysis;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;


public class ACFG {

	Map<Object,Vertex> nodes;
	Set<Edge> edges;
	
	ACFG() {
		edges = new HashSet<Edge>();
	}

	ACFG withNode(Vertex v) {
		return this;
	}

	ACFG withEdge(String label, Object fromNodeId, Object toNodeId) {
		return this;
	}

	Set<Edge> edges() {
		return edges;
	}
}
