package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPc;
	private int ternaryFalseJumpAddr;   // PC of false jump (from CondFact)
	private int ternarySkipJumpAddr;    // PC of unconditional jump (skip false branch)
	public int getMainPc() {
		return mainPc;
	}
	
	@Override
	public void visit(MethodName MethodName) {
		MethodName.obj.setAdr(Code.pc);
		if(MethodName.getI1().equalsIgnoreCase("main"))
			mainPc = Code.pc;
		
		Code.put(Code.enter);
		Code.put(MethodName.obj.getLevel());//b1
		Code.put(MethodName.obj.getLocalSymbols().size());//b2
	}
	
	@Override
	public void visit(MethodDecl MethodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(StatementP1 StatementP1) {
		Code.loadConst(0);
		if(StatementP1.getExpr().struct.equals(Tab.charType))
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
	}
	@Override
	public void visit(StatementP2 StatementP2) {
		Code.loadConst(StatementP2.getN2());
		if(StatementP2.getExpr().struct.equals(Tab.charType))
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
	}
	
	@Override
	public void visit(FactorNum FactorNum) {
		Code.loadConst(FactorNum.getN1());
	}
	@Override
	public void visit(FactorChar FactorChar) {
		Code.loadConst(FactorChar.getC1());
	}
	@Override
	public void visit(FactorBool FactorBool) {
		Code.loadConst(FactorBool.getB1());
	}
	@Override
	public void visit(FactorD FactorD){
		Code.load(FactorD.getDesignator().obj);
	}
	@Override
	public void visit(FactorIdent FactorIdent) {
		Code.put(Code.newarray);
		if(FactorIdent.getType().struct.equals(Tab.charType)) {
			Code.put(0); 
		}else {
			Code.put(1); 
		}
	}
	
	
	@Override
	public void visit(AddopTermList1 AddopTermList1) {
		
		if(AddopTermList1.getAddop() instanceof AddopADD) {
			Code.put(Code.add);
		}else if(AddopTermList1.getAddop() instanceof AddopMINUS){
			Code.put(Code.sub);
		}
	}
	
	@Override
	public void visit(MulTerm MulTerm) {
		if(MulTerm.getMulop() instanceof MulopMUL) {
			Code.put(Code.mul);
		}else if(MulTerm.getMulop() instanceof MulopDIV){
			Code.put(Code.div);
		}else if(MulTerm.getMulop() instanceof MulopMOD) {
			Code.put(Code.rem);
		}
	}
	
	
	@Override
	public void visit(DesignatorName DesignatorName){
		//Code.load(DesignatorName.obj);
		if (DesignatorName.obj.getKind() != Obj.Type) {
	        Code.load(DesignatorName.obj);
	    }
	}
	
	@Override
	public void visit(DesignatorStatementASS DesignatorStatementASS){
		Code.store(DesignatorStatementASS.getDesignator().obj);
	}
	
	@Override
	public void visit(Factor Factor) {
		if(Factor.getOptMinus() instanceof NegativeExpr) {
			Code.put(Code.neg);
		}
	}
	
	@Override
	public void visit(DesignatorStatementINC DesignatorStatementINC) {
		if(DesignatorStatementINC.getDesignator().obj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(DesignatorStatementINC.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(DesignatorStatementINC.getDesignator().obj);
	}
	@Override
	public void visit(DesignatorStatementDEC DesignatorStatementDEC) {
		if(DesignatorStatementDEC.getDesignator().obj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(DesignatorStatementDEC.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(DesignatorStatementDEC.getDesignator().obj);
	}
	
	@Override
	public void visit(StatementRET StatementRET) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(StatementREAD StatementREAD) {
		if(StatementREAD.getDesignator().obj.getType().equals(Tab.charType))
			Code.put(Code.bread);
		else
			Code.put(Code.read);
		Code.store(StatementREAD.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatementACTP DesignatorStatementACTP) {
	    Obj methodObj = DesignatorStatementACTP.getDesignator().obj;
	    String name = methodObj.getName();
	    
	    if ("chr".equals(name) || "ord".equals(name)) {
	        // chr and ord are identity functions - value is already on stack
	        // As a statement, discard the result
	        Code.put(Code.pop);
	    } else if ("len".equals(name)) {
	        // len uses the arraylength instruction
	        Code.put(Code.arraylength);
	        // As a statement, discard the result
	        if (methodObj.getType() != Tab.noType) {
	            Code.put(Code.pop);
	        }
	    } else {
	        // User-defined method call
	        int offset = methodObj.getAdr() - Code.pc;
	        Code.put(Code.call);
	        Code.put2(offset);
	        
	        if (methodObj.getType() != Tab.noType) {
	            Code.put(Code.pop);
	        }
	    }
	}
	@Override
	public void visit(FactorDA FactorDA) {
	    Obj methodObj = FactorDA.getDesignator().obj;
	    String name = methodObj.getName();
	    
	    if ("chr".equals(name) || "ord".equals(name)) {
	        // chr and ord are identity functions - value already on stack
	        // Result stays on stack as the factor value
	    } else if ("len".equals(name)) {
	        // len uses the arraylength instruction
	        Code.put(Code.arraylength);
	    } else {
	        // User-defined method call
	        int offset = methodObj.getAdr() - Code.pc;
	        Code.put(Code.call);
	        Code.put2(offset);
	    }
	}
	
	@Override
	public void visit(CondFactBez CondFactBez) {
		// Bare boolean condition (e.g. bt, false)
		// Value is already on the stack from child traversal
		// Emit: if value == 0 (false), jump to false branch
		Code.loadConst(0);
		Code.putFalseJump(Code.ne, 0);
		ternaryFalseJumpAddr = Code.pc - 2;
	}
	
	@Override
	public void visit(CondFactRelop CondFactRelop) {
		// Comparison condition (e.g. bodovi > 10)
		// Both sides are already on the stack from child traversal
		int relopCode;
		if (CondFactRelop.getRelop() instanceof RelopEQ) {
			relopCode = Code.eq;
		} else if (CondFactRelop.getRelop() instanceof RelopNE) {
			relopCode = Code.ne;
		} else if (CondFactRelop.getRelop() instanceof RelopLT) {
			relopCode = Code.lt;
		} else if (CondFactRelop.getRelop() instanceof RelopLE) {
			relopCode = Code.le;
		} else if (CondFactRelop.getRelop() instanceof RelopGT) {
			relopCode = Code.gt;
		} else {
			relopCode = Code.ge;
		}
		Code.putFalseJump(relopCode, 0);
		ternaryFalseJumpAddr = Code.pc - 2;
	}
	
	@Override
	public void visit(TernaryMarker TernaryMarker) {
		// Fires after the true branch Expr, before the false branch Expr
		// 1. Emit unconditional jump to skip false branch (patched later)
		Code.putJump(0);
		ternarySkipJumpAddr = Code.pc - 2;
		// 2. Patch the false jump from CondFact to land here (start of false branch)
		Code.fixup(ternaryFalseJumpAddr);
	}
	
	@Override
	public void visit(TernaryExpr TernaryExpr) {
		// Fires after everything - patch the unconditional jump to land here
		Code.fixup(ternarySkipJumpAddr);
	}
	
}
