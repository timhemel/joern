package tests.analysis;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import java.util.Set;
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
import octopus.analysis.Analyzer;
import octopus.analysis.MFPAlgorithm;
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
	public void testBuildJoernASTIdentifierDeclStatement() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex decl1 = b.IdentifierDeclStatement(
			b.IdentifierDecl(
				b.IdentifierDeclType("int"),
				b.Identifier("x"),
				b.AssignmentExpression(
					b.Identifier("x"),b.PrimaryExpression("0")
				)
			)
		);
		Vertex decl2 = b.IdentifierDeclStatement(
			b.IdentifierDecl(
				b.IdentifierDeclType("int"),
				b.Identifier("y")
			)
		);

		assertEquals("int y ;",decl2.value("code"));
		assertEquals("int x = 0 ;",decl1.value("code"));
		assertEquals("IdentifierDeclStatement",decl1.value("type"));
	}


	@Test
	public void testBuildJoernCFG() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex stat1 = b.ExpressionStatement(
				b.AssignmentExpression(
					b.Identifier("x"),
					b.PrimaryExpression("3")
				)
			);
		Vertex stat2 = b.ExpressionStatement(
				b.AssignmentExpression(
					b.Identifier("y"),
					b.Identifier("x")
				)
			);
		Vertex entry = b.CFGEntryNode();
		Vertex exit = b.CFGExitNode();
		b.connect("FLOWS_TO", entry, stat1);
		b.connect("FLOWS_TO", stat1, stat2);
		b.connect("FLOWS_TO", stat2, exit);
		assertEquals(1, toArrayList( stat1.vertices(Direction.OUT,"FLOWS_TO")).size());
		assertEquals(stat2, stat1.vertices(Direction.OUT,"FLOWS_TO").next());
	}


	@Test
	public void testTransferFunctionCFGEntry() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex entry = b.CFGEntryNode();
		TransferFunction f = JoernTransferFunctionFactory.create(entry);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x", POS);
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(l, f.eval(e,l));
	}

	@Test
	public void testTransferFunctionCFGExit() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex exit = b.CFGExitNode();
		TransferFunction f = JoernTransferFunctionFactory.create(exit);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x", POS);
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(l, f.eval(e,l));
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
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(l, f.eval(e,l));
	}

	@Test
	public void testTransferFunctionAssignmentToSameVariable() {
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
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(expected, f.eval(e,l));
	}

	@Test
	public void testTransferFunctionAssignmentToOtherVariable() {
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
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(expected, f.eval(e,l));
	}

	@Test
	public void testTransferFunctionAssignmentFromVariable() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex stat = b.ExpressionStatement(
			b.AssignmentExpression(
				b.Identifier("x"),b.Identifier("z")
			)
		);
		TransferFunction f = JoernTransferFunctionFactory.create(stat);
		MapLattice<Lattice<SignLattice>> expected = new MapLattice<Lattice<SignLattice>>();
		expected.put("z", POS);
		expected.put("x", POS);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("z", POS);
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(expected, f.eval(e,l));
	}

	@Test
	public void testAbstractionFunctionVariable() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex identifier = b.Identifier("x");
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x",POS);
		TransferFunction f = JoernTransferFunctionFactory.create(identifier);
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(POS, f.eval(e,l));
	}

	@Test
	public void testAbstractionFunctionPrimaryExpression() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex constant = b.PrimaryExpression("-4");
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x",POS);
		TransferFunction f = JoernTransferFunctionFactory.create(constant);
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(NEG, f.eval(e,l));
	}

	@Test
	public void testAbstractionFunctionAdditiveExpression() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex expr = b.AdditiveExpression(
				b.Identifier("x"),
				b.PrimaryExpression("-4")
			);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x",POS);
		TransferFunction f = JoernTransferFunctionFactory.create(expr);
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(TOP, f.eval(e,l));
	}

	@Test
	public void testAbstractionFunctionMultiplicativeExpression() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex expr = b.MultiplicativeExpression(
				b.Identifier("x"),
				b.PrimaryExpression("-4")
			);
		MapLattice<Lattice<SignLattice>> l = new MapLattice<Lattice<SignLattice>>();
		l.put("x",NEG);
		TransferFunction f = JoernTransferFunctionFactory.create(expr);
		JoernSignEvaluator e = new JoernSignEvaluator();
		assertEquals(POS, f.eval(e,l));
	}

	@Test
	public void testMFPAlgorithmEmptyCode() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex entry = b.CFGEntryNode();
		Vertex exit = b.CFGExitNode();
		Edge edge = b.connect("FLOWS_TO", entry, exit);
		Analyzer analyzer = new JoernSignAnalyzer();
		Set<Edge> edges = new HashSet<Edge>();
		edges.add(edge);
		MFPAlgorithm mfp = new MFPAlgorithm(edges,analyzer);
		mfp.run();
		MapLattice<Lattice<SignLattice>> expected = new MapLattice<Lattice<SignLattice>>();
		assertEquals(expected, analyzer.get(exit));
	}

	@Test
	public void testMFPAlgorithmSingleAssignment() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex entry = b.CFGEntryNode();
		Vertex stat = b.ExpressionStatement(
			b.AssignmentExpression(
				b.Identifier("x"),b.PrimaryExpression("0")
			)
		);
		Vertex exit = b.CFGExitNode();
		Edge edge1 = b.connect("FLOWS_TO", entry, stat);
		Edge edge2 = b.connect("FLOWS_TO", stat, exit);
		Analyzer analyzer = new JoernSignAnalyzer();
		Set<Edge> edges = new HashSet<Edge>();
		edges.add(edge1);
		edges.add(edge2);
		MFPAlgorithm mfp = new MFPAlgorithm(edges,analyzer);
		mfp.run();
		MapLattice<Lattice<SignLattice>> expected = new MapLattice<Lattice<SignLattice>>();
		expected.put("x",ZER);
		// assertEquals(expected, analyzer.get(stat));
		assertEquals(expected, analyzer.get(exit));
	}

	@Test
	public void testMFPAlgorithmMultipleAssignments() {
		Graph graph = TinkerGraph.open();
		JoernGraphBuilder b = new JoernGraphBuilder(graph);
		Vertex entry = b.CFGEntryNode();
		Vertex stat1 = b.ExpressionStatement(
			b.AssignmentExpression(
				b.Identifier("x"),b.PrimaryExpression("0")
			)
		);
		Vertex stat2 = b.ExpressionStatement(
			b.AssignmentExpression(
				b.Identifier("y"),b.Identifier("x")
			)
		);
		Vertex stat3 = b.ExpressionStatement(
			b.AssignmentExpression(
				b.Identifier("x"),b.PrimaryExpression("-10")
			)
		);
		Vertex exit = b.CFGExitNode();
		Edge edge1 = b.connect("FLOWS_TO", entry, stat1);
		Edge edge2 = b.connect("FLOWS_TO", stat1, stat2);
		Edge edge3 = b.connect("FLOWS_TO", stat2, stat3);
		Edge edge4 = b.connect("FLOWS_TO", stat3, exit);
		Analyzer analyzer = new JoernSignAnalyzer();
		Set<Edge> edges = new HashSet<Edge>();
		edges.add(edge1);
		edges.add(edge2);
		edges.add(edge3);
		edges.add(edge4);
		MFPAlgorithm mfp = new MFPAlgorithm(edges,analyzer);
		mfp.run();
		MapLattice<Lattice<SignLattice>> expectedAtExit = new MapLattice<Lattice<SignLattice>>();
		expectedAtExit.put("x",NEG);
		expectedAtExit.put("y",ZER);
		// assertEquals(expected, analyzer.get(stat));
		assertEquals(expectedAtExit, analyzer.get(exit));
		MapLattice<Lattice<SignLattice>> expectedAtStat3 = new MapLattice<Lattice<SignLattice>>();
		expectedAtStat3.put("x",ZER);
		expectedAtStat3.put("y",ZER);
		assertEquals(expectedAtStat3, analyzer.get(stat3));
	}




}
