package tests.analysis;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;

public class JoernASTBuilder {
	Graph graph;

	JoernASTBuilder(Graph graph) {
		this.graph = graph;
	}

	Vertex Identifier(String code) {
		Vertex vertex = graph.addVertex();
		vertex.property("type","Identifier");
		vertex.property("code",code);
		return vertex;
	}

	Vertex PrimaryExpression(String code) {
		Vertex vertex = graph.addVertex();
		vertex.property("type","PrimaryExpression");
		vertex.property("code",code);
		return vertex;
	}

	Vertex AssignmentExpression(Vertex lhs, Vertex rhs) {
		Vertex vertex = graph.addVertex();
		lhs.property("childNum","0");
		rhs.property("childNum","1");
		vertex.property("type","AssignmentExpression");
		vertex.property("code",lhs.value("code")+" = "+rhs.value("code"));
		vertex.addEdge("IS_AST_PARENT",lhs);
		vertex.addEdge("IS_AST_PARENT",rhs);
		return vertex;
	}

	Vertex ExpressionStatement(Vertex child) {
		Vertex vertex = graph.addVertex();
		child.property("childNum","0");
		vertex.property("type","ExpressionStatement");
		vertex.property("code",child.value("code"));
		vertex.addEdge("IS_AST_PARENT",child);
		return vertex;
	}

	public Vertex CFGEntryNode() {
		Vertex vertex = graph.addVertex();
		vertex.property("type","CFGEntryNode");
		vertex.property("isCFGNode","True");
		vertex.property("code","ENTRY");
		return vertex;
	}
}
