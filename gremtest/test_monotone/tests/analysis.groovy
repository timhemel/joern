
println "test_monotone/tests/analysis.groovy"

class BaseAnalysis {
	public bottom() { return 0; }
	public top() { return 0; }

	public join(v1,v2) {
		println "BaseAnalysis.join(${v1},${v2}) = 0"
		return 0
	}

	public joinCollection(values){
		values.inject( bottom() ) { result, v -> join(result,v) }
	}
}

class SignAnalysis extends BaseAnalysis {
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

	public bottom() { return sb; }
	public top() { return st; }

	public join(v1,v2) {
		println "SignAnalysis::join(${v1},${v2})"
		println sign_join_table[v1][v2]
		return sign_join_table[v1][v2]
	}

}

public class MappingAnalysis extends BaseAnalysis {

	BaseAnalysis analysis;

	MappingAnalysis(BaseAnalysis a) {
		analysis = a
	}

	public bottom() { return [ : ] }

	public join(map1, map2) {
		println "MappingAnalysis.join(${map1},${map2})"
		def ret = map1.clone()
		for ( mapitem in map2.iterator()) {
			def v = analysis.join(ret.get(mapitem.key,analysis.bottom()),mapitem.value);
			ret[mapitem.key] = v
		}
		println ret
		return ret
	}

/*
	static joinInfos(infos) {
		def ret = [:];
		for(info in infos) {
			println info
			for(var in info.iterator()) {
				def v = A.join(ret.get(var.key,A.sb),var.value);
				ret[var.key] = v
			}
		}
		println ret;
		return ret;
	}
*/

}


