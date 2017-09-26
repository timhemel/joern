package tests.analysis;

import java.util.Map;
import java.util.HashMap;
import org.javatuples.Pair;
import java.util.Set;
import java.util.HashSet;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedEdge;

public class AGraph {

	Map<Object,Vertex> nodes;
	Set<Edge> edges;
	
	AGraph() {
		edges = new HashSet<Edge>();
		nodes = new HashMap<Object,Vertex>();
	}

	AGraph withNode(Vertex v) {
		nodes.put(v.id(),v);
		return this;
	}

	AGraph withEdge(String label, Object fromNodeId, Object toNodeId) {
		Pair<Object,String> outV = new Pair<Object,String>(fromNodeId,nodes.get(fromNodeId).label());
		Pair<Object,String> inV = new Pair<Object,String>(toNodeId,nodes.get(toNodeId).label());
		Edge e = new DetachedEdge(fromNodeId.toString()+"-["+label+"]->"+toNodeId.toString(), label, new HashMap<String,Object>(), outV, inV);
		edges.add(e);
		nodes.put(e.outVertex().id(),e.outVertex());
		nodes.put(e.inVertex().id(),e.inVertex());
		return this;
	}

	Set<Edge> edges() {
		return edges;
	}

	Map<Object,Vertex> nodes() {
		return nodes;
	}
}

