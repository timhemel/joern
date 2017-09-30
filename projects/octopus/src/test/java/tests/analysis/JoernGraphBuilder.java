package tests.analysis;

import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Direction;

public class JoernGraphBuilder {
	Graph graph;

	JoernGraphBuilder(Graph graph) {
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

	Vertex AdditiveExpression(Vertex child1, Vertex child2) {
		Vertex vertex = graph.addVertex();
		addChild("IS_AST_PARENT",vertex,child1,"0");
		addChild("IS_AST_PARENT",vertex,child2,"1");
		vertex.property("type","AdditiveExpression");
		vertex.property("code",child1.value("code")+" + "+child2.value("code"));
		return vertex;
	}

	Vertex MultiplicativeExpression(Vertex child1, Vertex child2) {
		Vertex vertex = graph.addVertex();
		addChild("IS_AST_PARENT",vertex,child1,"0");
		addChild("IS_AST_PARENT",vertex,child2,"1");
		vertex.property("type","MultiplicativeExpression");
		vertex.property("code",child1.value("code")+" * "+child2.value("code"));
		return vertex;
	}

	Vertex AssignmentExpression(Vertex lhs, Vertex rhs) {
		Vertex vertex = graph.addVertex();
		addChild("IS_AST_PARENT",vertex,lhs,"0");
		addChild("IS_AST_PARENT",vertex,rhs,"1");
		vertex.property("type","AssignmentExpression");
		vertex.property("code",lhs.value("code")+" = "+rhs.value("code"));
		return vertex;
	}

	Vertex ExpressionStatement(Vertex child) {
		Vertex vertex = graph.addVertex();
		addChild("IS_AST_PARENT",vertex,child,"0");
		vertex.property("type","ExpressionStatement");
		vertex.property("code",child.value("code"));
		return vertex;
	}

	Vertex RelationalExpression(Vertex expr1, String operator, Vertex expr2) {
		Vertex vertex = graph.addVertex();
		addChild("IS_AST_PARENT",vertex,expr1,"0");
		addChild("IS_AST_PARENT",vertex,expr2,"1");
		vertex.property("type", "RelationalExpression");
		vertex.property("code", expr1.value("code") + " " + operator + " " + expr2.value("code"));
		return vertex;
	}

	public Vertex Condition(Vertex expression) {
		Vertex vertex = graph.addVertex();
		addChild("IS_AST_PARENT",vertex,expression,"0");
		vertex.property("type","Condition");
		vertex.property("isCFGNode","True");
		vertex.property("code",expression.value("code"));
		return vertex;
	}

	public Vertex CFGEntryNode() {
		Vertex vertex = graph.addVertex();
		vertex.property("type","CFGEntryNode");
		vertex.property("isCFGNode","True");
		vertex.property("code","ENTRY");
		return vertex;
	}

	public Vertex CFGExitNode() {
		Vertex vertex = graph.addVertex();
		vertex.property("type","CFGExitNode");
		vertex.property("isCFGNode","True");
		vertex.property("code","EXIT");
		return vertex;
	}

	public Vertex IdentifierDeclType(String type) {
		Vertex vertex = graph.addVertex();
		vertex.property("type","IdentifierDeclType");
		vertex.property("code",type);
		return vertex;
	}
	public Vertex IdentifierDecl(Vertex vartype, Vertex varname, Vertex assignment) {
		Vertex vertex = graph.addVertex();
		vertex.property("type","IdentifierDecl");
		addChild("IS_AST_PARENT",vertex,vartype,"0");
		addChild("IS_AST_PARENT",vertex,varname,"1");
		String declCode;
		if (assignment != null) {
			declCode = assignment.value("code");
			addChild("IS_AST_PARENT",vertex,assignment,"2");
		} else {
			declCode = varname.value("code");
		}
		vertex.property("code",declCode);
		return vertex;
	}

	public Vertex IdentifierDecl(Vertex vartype, Vertex varname) {
		return IdentifierDecl(vartype,varname,null);
	}
	public Vertex IdentifierDeclStatement(Vertex decl) {
		Vertex vertex = graph.addVertex();
		vertex.property("type","IdentifierDeclStatement");
		vertex.property("isCFGNode","True");
		decl.property("childNum","0");
		vertex.addEdge("IS_AST_PARENT",decl);
		Vertex declType = findChildWithProperty(decl,Direction.OUT,"IS_AST_PARENT","type","IdentifierDeclType");
		vertex.property("code",declType.value("code") + " "+decl.value("code")+" ;");
		return vertex;
	}

	public Edge addChild(String label, Vertex fromVertex, Vertex toVertex, String childNum) {
		toVertex.property("childNum",childNum);
		Edge e = fromVertex.addEdge(label,toVertex);
		return e;
	}

	protected Vertex findChildWithProperty(Vertex parent,Direction direction, String label, String key,String value) {
		Iterator<Vertex> children = parent.vertices(direction,label);
		while (children.hasNext()) {
			Vertex child = children.next();
			if (child.value(key).equals(value)) {
				return child;
			}
		}
		return null;
	}

	public Edge connect(String label, Vertex fromVertex, Vertex toVertex) {
		Edge e = fromVertex.addEdge(label, toVertex);
		return e;
	}
}
