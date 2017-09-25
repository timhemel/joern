!include("gremtest.groovy")
!include("test_monotone/tests/analysis.groovy")

class MFPAlgorithm {
	BaseAnalysis analysis
	def edges;
	def nodes;
	def results;
	def xferfunc;
	def worklist;
	def graph;

	MFPAlgorithm(graph, edges, BaseAnalysis a) {
		this.graph = graph;
		this.edges = edges;
		this.analysis = a
		this.nodes = [:];
		this.results = [:]
		this.xferfunc = [:]
		this.worklist = [];
	}

	void init() {
		this.edges.each{ edge ->
			this.results[ edge.inVertex().id()] = analysis.bottom()
			this.results[ edge.outVertex().id()] = analysis.bottom()
			this.nodes[ edge.inVertex().id()] = edge.inVertex()
			this.nodes[ edge.outVertex().id()] = edge.outVertex()
		}
		// println "init = ${this.results}"
		this.worklist = this.edges;
		this.nodes.values().each{ node ->
			this.xferfunc[ node.id() ] = XferFuncFactory.create(node);
		}
	}

	def run() {
		while(!worklist.isEmpty()) {
			def edge = worklist.pop();
			println edge
			def (n1,n2) = getEdgeNodeIds(edge)
			println "nodes ${n1}, ${n2}"
			def trans_n1 = trans(n1,this.results[n1])
			println "trans= ${trans_n1}"
			// if analysis.leq(trans_n1,this.results[n2]) {
			// }
		}
	}

	def getEdgeNodeIds(edge) {
		// forward analysis
		def n1 = edge.inVertex().id()
		def n2 = edge.outVertex().id()
		// println "getEdgeNodes = [${n1},${n2}]"
		return [n1,n2]
	}

	def trans(nodeId,value) {
		def node = nodes[nodeId]
		println "trans(${nodeId}[${node.value('code')}:${node.value('type')}], $value)"
		println this.xferfunc[nodeId]
		def result = this.xferfunc[nodeId].eval(value)
		println "result=$result"
		return result
	}

	def getValue(node) {
		return this.results[node.id()];
	}
}

test("sign_join", {
	a = new SignAnalysis();
	assertEquals(a.join(a.szp,a.sb), a.szp )
	assertEquals(a.join(a.sb,a.szp), a.szp )
})

test("sign_join_collection", {
	a = new SignAnalysis();
	l = [ a.sz, a.sp, a.sb ];
	assertEquals(a.joinCollection(l), a.szp)
})

test("sign less than order", {
	a = new SignAnalysis();
	assert  a.leq(a.sz,a.szp) ;
})


test("sign join multiple variables", {
	p1 = [ x: SignAnalysis.sb, y: SignAnalysis.snp, z: SignAnalysis.st ]
	p2 = [ x: SignAnalysis.sb, y: SignAnalysis.sp, z: SignAnalysis.sn ]
	p3 = [ x: SignAnalysis.sb, y: SignAnalysis.sz, z: SignAnalysis.sb ]
	expected = [ x: SignAnalysis.sb, y: SignAnalysis.st, z: SignAnalysis.st ]

	infos = [ p1,p2,p3 ]

	sa = new SignAnalysis();
	a = new MappingAnalysis(sa);

	assertEquals(a.joinCollection(infos), expected )
})

test("sign join no variables", {
	p1 = [:]
	expected = [:]

	infos = [ p1,p1,p1 ]

	sa = new SignAnalysis();
	a = new MappingAnalysis(sa);
	assertEquals(a.joinCollection(infos), expected )
})

test("mfp init", {
	// def cfgNodes;
	cfgNodes = g.V().has('type','Function')
		.has('code','sign_alg2')
		.functionToStatements().collect();
	cfgEdges = g.V().has('type','Function')
		.has('code','sign_alg2')
		.functionToStatements().outE('FLOWS_TO').collect();
	sa = new SignAnalysis();
	a = new MappingAnalysis(sa);
	def mfp = new MFPAlgorithm(g,cfgEdges, a);
	mfp.init()
	assertEquals(mfp.getValue(cfgNodes[2]), [:])
	assertEquals(mfp.worklist, cfgEdges)	
})

test("mfp trans", {
	cfgNodes = g.V().has('type','Function')
		.has('code','sign_alg2')
		.functionToStatements().collect();
	cfgEdges = g.V().has('type','Function')
		.has('code','sign_alg2')
		.functionToStatements().outE('FLOWS_TO').collect();
	sa = new SignAnalysis();
	a = new MappingAnalysis(sa);
	def mfp = new MFPAlgorithm(g,cfgEdges, a);
	mfp.init()
	cfgNodes.each{ println it.value('type') }
	// test CFGEntryNode
	cfgEntryNode = cfgNodes.find{ it.value('type') == 'CFGEntryNode' }
	println "cfgEntryNode: ${cfgEntryNode.value('type')}"
	println cfgEntryNode.value('type')
	assertEquals(mfp.trans(cfgEntryNode.id(), a.bottom()), a.bottom())
	// test CFGExitNode
	cfgExitNode = cfgNodes.find{ it.value('type') == 'CFGExitNode' }
	assertEquals(mfp.trans(cfgExitNode.id(), a.bottom()), a.bottom())
	// test Condition
	conditionNode = cfgNodes.find{ it.value('type') == 'Condition' }
	assertEquals(mfp.trans(conditionNode.id(), a.bottom()), a.bottom())
	// test IdentifierDeclStatement => AssignmentExpression or not
	identifierNode = cfgNodes.find{ it.value('code') == 'int a ;' }
	assertEquals(mfp.trans(identifierNode.id(), a.bottom()), ['a':sa.top()])
	// test ExpressionStatement => AssignmentExpression
	identifierNode = cfgNodes.find{ it.value('code') == 'int b , c = 2 , d ;' }
	assertEquals(mfp.trans(identifierNode.id(), a.bottom()), ['b':sa.top(),'c':sa.sp,'d':sa.top()])
	incrementnode = cfgNodes.find{ it.value('code') == 'y = y + 1' }
	assertEquals(mfp.trans(incrementnode.id(), ['y':sa.sn] ), ['y':sa.top()])
	multnode = cfgNodes.find{ it.value('code') == 'z = y * x' }
	assertEquals(mfp.trans(multnode.id(), ['x':sa.sz,'y':sa.sn,'z':sa.sp] ), ['x':sa.sz,'y':sa.sn,'z':sa.sz])
})

/*
test("mfp run", {
	// def cfgNodes;
	cfgNodes = g.V().has('type','Function')
		.has('code','sign_alg2')
		.functionToStatements().collect();
	cfgEdges = g.V().has('type','Function')
		.has('code','sign_alg2')
		.functionToStatements().outE('FLOWS_TO').collect();
	sa = new SignAnalysis();
	a = new MappingAnalysis(sa);
	def mfp = new MFPAlgorithm(g,cfgEdges, a);
	mfp.init()
	mfp.run()
	// assertEquals(mfp.getValue(cfgNodes[2]), [:])
	// assertEquals(mfp.worklist, cfgEdges)	
})
*/


run_tests()
