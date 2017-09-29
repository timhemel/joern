package octopus.analysis;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Direction;
import octopus.analysis.Analyzer;

public class MFPAlgorithm {

	Set<Edge> edges;
	Analyzer analyzer;
	
	public MFPAlgorithm(Set<Edge> edges, Analyzer analyzer) {
		this.analyzer = analyzer;
		this.edges = edges;
		for (Edge edge : edges) {
			this.analyzer.addVertex(edge.inVertex());
			this.analyzer.addVertex(edge.outVertex());
		}
	}

	Vertex getComputingVertex(Edge edge) {
		// depends on direction
		// if analyzer.direction() ...
		return edge.inVertex(); // forward analysis
	}

	Vertex getDependentVertex(Edge edge) {
		// depends on direction
		// if analyzer.direction() ...
		return edge.outVertex(); // forward analysis
	}

	Iterator<Edge> getSuccessorEdges(Vertex vertex) {
		// depends on direction
		// if analyzer.direction() ...
		return vertex.edges(Direction.OUT,"FLOWS_TO");
	}

	public void run() {
		analyzer.init();
		Stack<Edge> worklist = new Stack<Edge>();
		worklist.addAll(edges);
		while (!worklist.empty()) {
			Edge edge = worklist.pop();
			Vertex computingVertex = getComputingVertex(edge);
			Vertex dependentVertex = getDependentVertex(edge);
			Lattice transValue = analyzer.trans(dependentVertex);
			Lattice currentValue = analyzer.get(computingVertex);
			Lattice joinValue = analyzer.join(transValue,currentValue);
			if (!joinValue.equals(currentValue)) {
				// i.e.  !(transValue <= currentValue)
				analyzer.set(computingVertex, joinValue);
				Iterator<Edge> successorEdges = getSuccessorEdges(computingVertex);
				while (successorEdges.hasNext()) {
					Edge successorEdge = successorEdges.next();
					worklist.push(successorEdge);
				}
			}
		}
	}
}
