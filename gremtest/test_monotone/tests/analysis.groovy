
println "test_monotone/tests/analysis.groovy"

class BaseAnalysis {
	public bottom() { return 0; }
	public top() { return 0; }

	public join(v1,v2) {
		// println "BaseAnalysis.join(${v1},${v2}) = 0"
		return 0
	}

	public joinCollection(values){
		values.inject( bottom() ) { result, v -> join(result,v) }
	}

	public leq(v1,v2) {
		println join(v1,v2)
		return join(v1,v2) == v2;
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
		// println "SignAnalysis::join(${v1},${v2})"
		// println sign_join_table[v1][v2]
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
		// println "MappingAnalysis.join(${map1},${map2})"
		def ret = map1.clone()
		for ( mapitem in map2.iterator()) {
			def v = analysis.join(ret.get(mapitem.key,analysis.bottom()),mapitem.value);
			ret[mapitem.key] = v
		}
		// println ret
		return ret
	}

}

// translate ast to abstraction function tree

class SignAnalysisAbsFuncBase {
	def eval(var_context) { }
}

class SignAnalysisAbsFuncConstant extends SignAnalysisAbsFuncBase {
	def constant;
	SignAnalysisAbsFuncConstant(n) {
		constant = n;
	}
	def eval(var_context) {
		if (constant < 0) return SignAnalysis.sn;
		if (constant > 0) return SignAnalysis.sp;
		return SignAnalysis.sz;
	}
}

class SignAnalysisAbsFuncVariable extends SignAnalysisAbsFuncBase {
	String varname;
	SignAnalysisAbsFuncVariable(String varname) {
		this.varname = varname;
	}
	def eval(var_context) {
		// lookup variable's sign and return it
		return var_context[varname];
	}
}

class SignAnalysisAbsFuncBinaryPlus extends SignAnalysisAbsFuncBase {
	def operand1, operand2;
	static sign_xfer_table = [
		[ SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb ],
		[ SignAnalysis.sb, SignAnalysis.sn, SignAnalysis.sn, SignAnalysis.st, SignAnalysis.sn, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st ],
		[ SignAnalysis.sb, SignAnalysis.sn, SignAnalysis.sz, SignAnalysis.sp, SignAnalysis.szn, SignAnalysis.szp, SignAnalysis.snp, SignAnalysis.st ],
		[ SignAnalysis.sb, SignAnalysis.st, SignAnalysis.sp, SignAnalysis.sp, SignAnalysis.st, SignAnalysis.sp, SignAnalysis.st, SignAnalysis.st ],
		[ SignAnalysis.sb, SignAnalysis.sn, SignAnalysis.szn, SignAnalysis.st, SignAnalysis.szn, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st ],
		[ SignAnalysis.sb, SignAnalysis.st, SignAnalysis.szp, SignAnalysis.sp, SignAnalysis.st, SignAnalysis.szp, SignAnalysis.st, SignAnalysis.st ],
		[ SignAnalysis.sb, SignAnalysis.st, SignAnalysis.snp, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st, SignAnalysis.snp, SignAnalysis.st ],
		[ SignAnalysis.sb, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st ]
	] 

	SignAnalysisAbsFuncBinaryPlus(op1, op2) {
		operand1 = op1;
		operand2 = op2;
	}
	def eval(var_context) {
		println "BinaryPlus::eval($operand1,$operand2)"
		def r1 = operand1.eval(var_context)
		def r2 = operand2.eval(var_context)
		return sign_xfer_table[r1][r2]
	}
}

class SignAnalysisAbsFuncBinaryTimes extends SignAnalysisAbsFuncBase {

	static sign_xfer_table = [
		// [ sb,              sn,                    sz,           sp,             szn,            szp,              snp,              st ],
		[ SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sz, SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb, SignAnalysis.sb], // sb
		[ SignAnalysis.sb, SignAnalysis.sp, SignAnalysis.sz, SignAnalysis.sn, SignAnalysis.szp, SignAnalysis.szn, SignAnalysis.snp, SignAnalysis.st ], // sn
		[ SignAnalysis.sz, SignAnalysis.sz, SignAnalysis.sz, SignAnalysis.sz, SignAnalysis.sz, SignAnalysis.sz, SignAnalysis.sz, SignAnalysis.sz ], // sz
		[ SignAnalysis.sb, SignAnalysis.sn, SignAnalysis.sz, SignAnalysis.sp, SignAnalysis.szn, SignAnalysis.szp, SignAnalysis.snp, SignAnalysis.st ], // sp
		[ SignAnalysis.sb, SignAnalysis.szp, SignAnalysis.sz, SignAnalysis.szn, SignAnalysis.szp, SignAnalysis.szn, SignAnalysis.st, SignAnalysis.st ], // szn
		[ SignAnalysis.sb, SignAnalysis.szn, SignAnalysis.sz, SignAnalysis.szp, SignAnalysis.szn, SignAnalysis.szp, SignAnalysis.st, SignAnalysis.st ], // szp
		[ SignAnalysis.sb, SignAnalysis.snp, SignAnalysis.sz, SignAnalysis.snp, SignAnalysis.st, SignAnalysis.st, SignAnalysis.snp, SignAnalysis.st ], // snp
		[ SignAnalysis.sb, SignAnalysis.st, SignAnalysis.sz, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st, SignAnalysis.st ] // st
	] 

	def operand1, operand2;

	SignAnalysisAbsFuncBinaryTimes(op1, op2) {
		operand1 = op1;
		operand2 = op2;
	}

	def eval(var_context) {
		println "BinaryTimes::eval($operand1,$operand2)"
		def r1 = operand1.eval(var_context)
		def r2 = operand2.eval(var_context)
		return sign_xfer_table[r1][r2]
	}
}

class SignAnalysisAbsFuncId extends SignAnalysisAbsFuncBase {
	def child;
	SignAnalysisAbsFuncId(child) {
		this.child = child;
	}
	def eval(var_context) {
		return child.eval(var_context);
	}
}

class SignAnalysisXferFuncBase {
	def eval(analysis) {
		return analysis;
	}
}


class SignAnalysisXferFuncCFGEntryNode extends SignAnalysisXferFuncBase {
}

class SignAnalysisXferFuncCFGExitNode extends SignAnalysisXferFuncBase {
}

class SignAnalysisXferFuncCondition extends SignAnalysisXferFuncBase {
}

class SignAnalysisXferFuncIdentifierDeclStatement extends SignAnalysisXferFuncBase {
	def children;
	SignAnalysisXferFuncIdentifierDeclStatement(children) {
		this.children = children;
	}
	def eval(analysis) {
		println "identifierdeclstatement::eval"
		this.children.each{ println it }
		this.children.inject( analysis ) { result, v -> v.eval(result) }
	}
}

class SignAnalysisXferFuncIdentifierDecl extends SignAnalysisXferFuncBase {
	String varname;
	SignAnalysisXferFuncIdentifierDecl(varname) {
		this.varname = varname;
	}
	def eval(analysis) {
		println " Initialize $varname"
		// assume non-initialized values
		def new_analysis = analysis.clone()
		new_analysis[varname] = SignAnalysis.st;
		return new_analysis;
	}
}

class SignAnalysisXferFuncExpressionStatement extends SignAnalysisXferFuncBase {
	def expression;
	SignAnalysisXferFuncExpressionStatement(expression) {
		this.expression = expression;
	}
	def eval(analysis) {
		return expression.eval(analysis);
	}
}

class SignAnalysisXferFuncAssignmentExpression extends SignAnalysisXferFuncBase {
	String varname;
	def expression;
	SignAnalysisXferFuncAssignmentExpression(varname,expression) {
		this.varname = varname;
		this.expression = expression;
	}
	def eval(analysis) {
		println " assignment expression $varname = $expression"
		def r = this.expression.eval(analysis)
		println " Assign $varname to value $r"
		def new_analysis = analysis.clone()
		new_analysis[varname] = r;
		return new_analysis;
	}
}

class XferFuncFactory {
	static create(cfgnode) {
		println "get xfer func for ${cfgnode} type ${cfgnode.value('type')}"
		switch(cfgnode.value('type')) {
			case 'CFGEntryNode':
				return new SignAnalysisXferFuncCFGEntryNode()
				break;
			case 'CFGExitNode':
				return new SignAnalysisXferFuncCFGExitNode()
				break;
			case 'Condition':
				return new SignAnalysisXferFuncCondition()
				break;
			case 'IdentifierDeclStatement':
				def nodes = cfgnode.vertices(Direction.OUT,'IS_AST_PARENT').collect{ create(it) }
				return new SignAnalysisXferFuncIdentifierDeclStatement(nodes)
				break;
			case 'IdentifierDecl':
				def nodes = cfgnode.vertices(Direction.OUT,'IS_AST_PARENT').collect()
				nodes.each{ println "    ${it.value('type')}" }
				def varnode = nodes.find{it.value('type') == 'Identifier'}
				def expr = nodes.find{it.value('type') == 'AssignmentExpression'}
				if (expr) {
					return create(expr)
				}
				return new SignAnalysisXferFuncIdentifierDecl(varnode.value('code'))
				
				break;
			case 'ExpressionStatement':
				def node = cfgnode.vertices(Direction.OUT,'IS_AST_PARENT').next()
				def expr = create(node)
				return new SignAnalysisXferFuncExpressionStatement(expr)
				break;
			case 'AssignmentExpression':
				def varnode = cfgnode.vertices(Direction.OUT,'IS_AST_PARENT').find{ it.value('childNum')=='0' }
				def exprnode = cfgnode.vertices(Direction.OUT,'IS_AST_PARENT').find{ it.value('childNum')=='1' }
				def expr = create(exprnode)
				return new SignAnalysisXferFuncAssignmentExpression(varnode.value('code'),expr)

				break;
			case 'PrimaryExpression':
				println "primary expression ${cfgnode.value('code')}"
				return new SignAnalysisAbsFuncConstant(cfgnode.value('code').toDouble())
				break;
			case 'Identifier':
				return new SignAnalysisAbsFuncVariable(cfgnode.value('code'))
				break;
			case 'AdditiveExpression':
				def children = cfgnode.vertices(Direction.OUT,'IS_AST_PARENT').collect()
				def operand1 = children.find{ it.value('childNum') == '0' }
				def operand2 = children.find{ it.value('childNum') == '1' }
				return new SignAnalysisAbsFuncBinaryPlus(create(operand1), create(operand2))
				break;
			case 'MultiplicativeExpression':
				def children = cfgnode.vertices(Direction.OUT,'IS_AST_PARENT').collect()
				def operand1 = children.find{ it.value('childNum') == '0' }
				def operand2 = children.find{ it.value('childNum') == '1' }
				return new SignAnalysisAbsFuncBinaryTimes(create(operand1), create(operand2))
				break;
		}
		return 1;
	}
}
