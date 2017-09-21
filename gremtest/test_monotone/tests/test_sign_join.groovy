!include("gremtest.groovy")
!include("test_monotone/tests/analysis.groovy")

class MFPAlgorithm {
	MFPAlgorithm(nodes, analysis) {
		println nodes
	}

	void init() {
		println 1
	}

	def getValue(node) {
		return 1;
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
	println cfgNodes
	def mfp = new MFPAlgorithm(cfgNodes, SignAnalysis);
	mfp.init()
	assertEquals(mfp.getValue(cfgNodes[2]), [:])
	
})



run_tests()
