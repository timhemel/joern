package tests.analysis;

import java.util.Map;
import java.util.HashMap;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerVertex;

public class AVertex {
	Object id;
	Map<String,Object> properties;
	Vertex vertex;
	Graph graph;

	AVertex() {
		id = null;
		properties = new HashMap<String,Object>();
	}

	AVertex withId(Object id) {
		this.id = id;
		return this;
	}
	AVertex with(String key, Object value) {
		properties.put(key,value);
		return this;
	}

	AVertex inGraph(Graph graph) {
		this.graph = graph;
		return this;
	}

	AVertex build() {
		vertex = graph.addVertex();
		for(Object key: properties.keySet()) {
			vertex.property((String)key, properties.get(key));
		}
		return this;
	}

	Vertex get() {
		return vertex;
	}
}

