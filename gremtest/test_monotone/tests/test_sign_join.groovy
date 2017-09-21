!include("gremtest.groovy")

class SignAnalysis {
	static final int sb = 0;
	static final int sn = 1;
	static final int sz = 2;
	static final int sp = 3;
	static final int szn = 4;
	static final int szp = 5;
	static final int snp = 6;
	static final int st = 7;

	static final sign_join_table = [
		[ sb, sn, sz, sp, szn, szp, snp, st ],
		[ sn, sn, szn, snp, szn, st, snp, st ],
		[ sz, szn, sz, szp, szn, szp, st, st ],
		[ sp, snp, szp, sp, st, szp, snp, st ],
		[ szn, szn, szn, st, szn, st, st, st ],
		[ szp, st, szp, szp, st, szp, st, st ],
		[ snp, snp, st, snp, st, st, snp, st ],
		[ st, st, st, st, st, st, st, st ]
	] 

	static join(v1,v2) {
		return sign_join_table[v1][v2]
	}

	static joinInfos(infos) {
		def ret = [:];
		for(info in infos) {
			for(var in info.iterator()) {
				def v = join(ret.get(var.key,sb),var.value);
				ret[var.key] = v
			}
		}
		return ret;
	}
}


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
	assertEquals(SignAnalysis.join(SignAnalysis.szp,SignAnalysis.sb), SignAnalysis.szp )
})


test("sign join multiple variables", {
	p1 = [ x: SignAnalysis.sb, y: SignAnalysis.snp, z: SignAnalysis.st ]
	p2 = [ x: SignAnalysis.sb, y: SignAnalysis.sp, z: SignAnalysis.sn ]
	p3 = [ x: SignAnalysis.sb, y: SignAnalysis.sz, z: SignAnalysis.sb ]
	expected = [ x: SignAnalysis.sb, y: SignAnalysis.st, z: SignAnalysis.st ]

	infos = [ p1,p2,p3 ]

	assertEquals(SignAnalysis.joinInfos(infos), expected )
})

test("sign join no variables", {
	p1 = [:]
	expected = [:]

	infos = [ p1,p1,p1 ]

	assertEquals(SignAnalysis.joinInfos(infos), expected )
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
