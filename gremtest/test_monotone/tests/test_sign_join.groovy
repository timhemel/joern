!include("gremtest.groovy")
!include("test_monotone/tests/analysis.groovy")

class MFPAlgorithm {
	BaseAnalysis analysis
	def edges;
	def results;
	def worklist;

	MFPAlgorithm(edges, BaseAnalysis a) {
		this.analysis = a
		this.edges = edges;
		this.results = [:]
		this.worklist = [];
	}

	void init() {
		this.edges.each{ edge ->
			this.results[ edge.inVertex().id()] = analysis.bottom()
			this.results[ edge.outVertex().id()] = analysis.bottom()
		}
		// println "init = ${this.results}"
		this.worklist = this.edges;
	}

	def run() {
		while (!this.worklist.isEmpty()) {
			edge = this.worklist.pop()
			(n1,n2) = getEdgeNodes(edge)
		}
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
	assert  a.lt(a.sz,a.szp) ;
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
	def mfp = new MFPAlgorithm(cfgEdges, a);
	mfp.init()
	assertEquals(mfp.getValue(cfgNodes[2]), [:])
	assertEquals(mfp.worklist, cfgEdges)	
})



run_tests()
