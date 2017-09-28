package tests.analysis;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Spliterators;
import java.util.Spliterator;
import java.util.Iterator;

import octopus.analysis.Lattice;
import octopus.analysis.SignLattice;
import octopus.analysis.MapLattice;
import static octopus.analysis.SignLattice.*;
import octopus.analysis.TransferFunction;
import octopus.analysis.joern.*;
// import octopus.analysis.joern.JoernTransferFunctionFactory;

public class AnalysisTest {

	@Test
	public void testBuildVertexWithoutProperties() {
		Graph g = TinkerGraph.open();
		Vertex vertex = new AVertex().inGraph(g).build().get();
		assertEquals(0L,vertex.id());
	}

	@Test
	public void testBuildVertexWithProperties() {
		Graph g = TinkerGraph.open();
		Vertex vertex = new AVertex()
			.with("code","a = 1;")
			.with("type","AssignmentExpression")
			.inGraph(g)
			.build()
			.get();
		assertEquals("AssignmentExpression",vertex.value("type"));
		assertEquals("a = 1;",vertex.value("code"));
	}

	@Test
	public void testBuildVertexWithPropertyLists() {
		Graph g = TinkerGraph.open();
		Vertex vertex = new AVertex()
			.with("code",new ArrayList<String>(Arrays.asList("a = 1;","b=2;")))
			.inGraph(g)
			.build()
			.get();
		assertEquals(Arrays.asList("a = 1;","b=2;"),vertex.value("code"));
	}

	public static <T> ArrayList<T> toArrayList(final Iterator<T> iterator) {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
			false)
			.collect(Collectors.toCollection(ArrayList::new));
	}

	@Test
	public void testBuildAST() {
		Graph g = TinkerGraph.open();
		Vertex v0 = new AVertex().with("code","a = 1;").with("type","AssignmentExpression").inGraph(g).build().get();
		Vertex v1 = new AVertex().with("code","a").with("type","Identifier").with("childNum","0").inGraph(g).build().get();
		Vertex v2 = new AVertex().with("code","1").with("type","PrimaryExpression").with("childNum","1").inGraph(g).build().get();
		Edge e1 = new AnEdge().withLabel("IS_AST_PARENT").from(v0).to(v1).inGraph(g).build().get();
		Edge e2 = new AnEdge().withLabel("IS_AST_PARENT").from(v0).to(v2).inGraph(g).build().get();

		assertEquals(3,toArrayList(g.vertices()).size());
		assertEquals("a",g.vertices(v1.id()).next().value("code"));
		assertEquals(2,toArrayList(g.edges()).size());
		assertEquals(2,toArrayList(g.vertices(v0.id()).next().edges(Direction.OUT)).size());
		assertEquals(v1.id(),g.vertices(v0.id()).next().edges(Direction.OUT).next().inVertex().id());
	}

	@Test
	public void testBuildJoernASTExpressionStatement() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex root = b.ExpressionStatement(
				b.AssignmentExpression(
					b.Identifier("x"),
					b.PrimaryExpression("3")
				)
			);
		assertEquals("x",root.vertices(Direction.OUT).next().vertices(Direction.OUT).next().value("code"));
	}

	@Test
	public void testTransferFunctionCFGEntry() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex entry = b.CFGEntryNode();
		TransferFunction f = JoernTransferFunctionFactory.create(entry);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x", POS);
		assertEquals(l, f.eval(l));
	}

	@Test
	public void testTransferFunctionCFGExit() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex exit = b.CFGExitNode();
		TransferFunction f = JoernTransferFunctionFactory.create(exit);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x", POS);
		assertEquals(l, f.eval(l));
	}

	@Test
	public void testTransferFunctionCondition() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex cond = b.Condition(
			b.RelationalExpression(
				b.Identifier("z"),">",b.PrimaryExpression("0")
			)
		);
		TransferFunction f = JoernTransferFunctionFactory.create(cond);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x", POS);
		assertEquals(l, f.eval(l));
	}

	@Test
	public void testTransferFunctionAssignmentSameVariable() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex stat = b.ExpressionStatement(
			b.AssignmentExpression(
				b.Identifier("x"),b.PrimaryExpression("0")
			)
		);
		TransferFunction f = JoernTransferFunctionFactory.create(stat);
		MapLattice<Lattice<SignLattice>> expected = new MapLattice<Lattice<SignLattice>>();
		expected.put("x", ZER);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x", POS);
		assertEquals(expected, f.eval(l));
	}

	@Test
	public void testTransferFunctionAssignmentOtherVariable() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex stat = b.ExpressionStatement(
			b.AssignmentExpression(
				b.Identifier("x"),b.PrimaryExpression("0")
			)
		);
		TransferFunction f = JoernTransferFunctionFactory.create(stat);
		MapLattice<Lattice<SignLattice>> expected = new MapLattice<Lattice<SignLattice>>();
		expected.put("z", POS);
		expected.put("x", ZER);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("z", POS);
		assertEquals(expected, f.eval(l));
	}



}
