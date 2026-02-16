package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;


public class SemAnalyzer extends VisitorAdaptor {

	private boolean errorDetected = false;
	
	Logger log = Logger.getLogger(getClass());

	private Obj currentProgram;

	private Struct currentType;

	private int constant;

	private Struct constType;
	
	private Struct boolType = Tab.find("bool").getType();

	private boolean mainHappened = false;

	private Obj currentMethod;
	
	private Struct currentEnumType;
	private int currentEnumValue;
	private String currentEnumName;
	
	private List<Struct> actualParamTypes = new ArrayList<>();

	int nVars;

	private void checkMethodCall(Obj methodObj, SyntaxNode callNode) {
	    if (methodObj == Tab.noObj || methodObj.getKind() != Obj.Meth) {
	        actualParamTypes.clear();
	        return;
	    }
	    int formalCount = methodObj.getLevel();
	    int actualCount = actualParamTypes.size();
	    if (formalCount != actualCount) {
	        report_error("Broj stvarnih argumenata (" + actualCount + ") ne odgovara broju formalnih parametara (" + formalCount + ") za metodu: " + methodObj.getName(), callNode);
	        actualParamTypes.clear();
	        return;
	    }
	    // Get formal parameters - first formalCount locals are the parameters
	    int i = 0;
	    for (Obj formal : methodObj.getLocalSymbols()) {
	        if (i >= formalCount) break;
	        if (!actualParamTypes.get(i).assignableTo(formal.getType())) {
	            report_error("Tip stvarnog argumenta na poziciji " + (i + 1) + " nije kompatibilan sa formalnim parametrom '" + formal.getName() + "' za metodu: " + methodObj.getName(), callNode);
	        }
	        i++;
	    }
	    actualParamTypes.clear();
	}
	
	private boolean isEnumIntCompatible(Struct src, Struct dest) {
	    // int -> enum
	    if (src.equals(Tab.intType) && dest.getKind() == Struct.Enum) return true;
	    // enum -> int
	    if (src.getKind() == Struct.Enum && dest.equals(Tab.intType)) return true;
	    // same enum type
	    if (src.getKind() == Struct.Enum && src.equals(dest)) return true;
	    return false;
	}
	
	private boolean isIntCompatible(Struct type) {
	    return type.equals(Tab.intType) || type.getKind() == Struct.Enum;
	}
	
	private void reportSymbolUsage(Obj obj, SyntaxNode node) {
	    if (obj == null || obj == Tab.noObj) return;

	    String kindStr;
	    switch (obj.getKind()) {
	        case Obj.Con:  kindStr = "Upotreba simbolicke konstante"; break;
	        case Obj.Var:
	            if (obj.getLevel() == 0)
	                kindStr = "Upotreba globalne promenljive";
	            else
	                kindStr = "Upotreba lokalne promenljive";
	            break;
	        case Obj.Elem: kindStr = "Pristup elementu niza"; break;
	        case Obj.Fld:  kindStr = "Pristup polju"; break;
	        case Obj.Meth: kindStr = "Poziv metode"; break;
	        default: return; // Type, Prog  not usage
	    }

	    report_info(kindStr + " " + obj.getName()
	        + ", Obj node: [kind=" + objKindName(obj.getKind())
	        + ", name=" + obj.getName()
	        + ", type=" + structKindName(obj.getType())
	        + ", adr=" + obj.getAdr()
	        + ", level=" + obj.getLevel()
	        + "]", node);
	}

	private String objKindName(int kind) {
	    switch (kind) {
	        case Obj.Con:  return "Con";
	        case Obj.Var:  return "Var";
	        case Obj.Type: return "Type";
	        case Obj.Meth: return "Meth";
	        case Obj.Fld:  return "Fld";
	        case Obj.Elem: return "Elem";
	        case Obj.Prog: return "Prog";
	        default:       return "Unknown";
	    }
	}

	private String structKindName(Struct type) {
	    if (type == null) return "null";
	    switch (type.getKind()) {
	        case Struct.None:  return "notype";
	        case Struct.Int:   return "int";
	        case Struct.Char:  return "char";
	        case Struct.Array: return "Arr of " + structKindName(type.getElemType());
	        case Struct.Class: return "Class";
	        case Struct.Bool:  return "bool";
	        case Struct.Enum:  return "Enum";
	        default:           return "Unknown";
	    }
	}

	
	public void report_error(String message, SyntaxNode info) {
		errorDetected  = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info ==null) ? 0: info.getLine();
		if(line!=0) {
			msg.append(" na liniji ").append(line);
			
		}
		log.error(msg.toString());
	}
	
	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info ==null) ? 0: info.getLine();
		if(line!=0) {
			msg.append(" na liniji ").append(line);
		}
		log.info(msg.toString());
	}
	
	
	public boolean passed() {
		return !errorDetected;
	}
	
	
	// SEM PASS
	@Override
	public void visit(Program program) {
		nVars =  Tab.currentScope().getnVars();
		Tab.chainLocalSymbols(currentProgram);
		Tab.closeScope();
		currentProgram = null;
		
		if(!mainHappened) {
			report_error("Nema maina", program);
		}
	}
	
	@Override
	public void visit(ProgramName pn) {
		currentProgram = Tab.insert(Obj.Prog, pn.getI1(), Tab.noType);
		Tab.openScope();
		
	}
	// ENUM DECL
	@Override
	public void visit(EnumName EnumName) {
	    currentEnumName = EnumName.getI1();
	    Obj enumObj = Tab.find(currentEnumName);
	    
	    if (enumObj != Tab.noObj) {
	        report_error("Dvostruka definicija enuma: " + currentEnumName, EnumName);
	        currentEnumType = null;
	    } else {
	        // Create a new Enum type
	        currentEnumType = new Struct(Struct.Enum);
	        Tab.insert(Obj.Type, currentEnumName, currentEnumType);
	    }
	    currentEnumValue = 0; // Initialize enum counter
	}
	@Override
	public void visit(EnumDeclList EnumDeclList) {
	    // Enum declaration finished, cleanup
	    currentEnumType = null;
	    currentEnumName = null;
	}
	@Override
	public void visit(EnumElemNoValue EnumElemNoValue) {
	    String elemName = EnumElemNoValue.getI1();
	    
	    if (currentEnumType == null) {
	        return; // Error already reported in EnumName
	    }
	    
	    // Check if element already exists in this enum
	    Obj existingElem = currentEnumType.getMembersTable().searchKey(elemName);
	    
	    if (existingElem != null) {
	        report_error("Dvostruka definicija enum konstante: " + elemName, EnumElemNoValue);
	    } else {
	        // Create enum constant and add to enum's member table
	        Obj elemObj = new Obj(Obj.Con, elemName, Tab.intType);
	        elemObj.setAdr(currentEnumValue);
	        currentEnumType.getMembersTable().insertKey(elemObj);
	        
	        report_info("Definisana enum konstanta " + currentEnumName + "." + elemName + " = " + currentEnumValue, EnumElemNoValue);
	    }
	    currentEnumValue++; // Increment for next element
	}
	@Override
	public void visit(EnumElemWithValue EnumElemWithValue) {
	    String elemName = EnumElemWithValue.getI1();
	    int value = EnumElemWithValue.getN2();
	    
	    if (currentEnumType == null) {
	        return; // Error already reported in EnumName
	    }
	    
	    // Check if element already exists in this enum
	    Obj existingElem = currentEnumType.getMembersTable().searchKey(elemName);
	    
	    if (existingElem != null) {
	        report_error("Dvostruka definicija enum konstante: " + elemName, EnumElemWithValue);
	    } else {
	        // Create enum constant with explicit value and add to enum's member table
	        Obj elemObj = new Obj(Obj.Con, elemName, Tab.intType);
	        elemObj.setAdr(value);
	        currentEnumType.getMembersTable().insertKey(elemObj);
	        
	        report_info("Definisana enum konstanta " + currentEnumName + "." + elemName + " = " + value, EnumElemWithValue);
	    }
	    currentEnumValue = value + 1; // Next element continues from this value
	}
	
	// CONST DECL
	@Override
	public void visit(ConDecl ConDecl) {
		Obj conObj = Tab.find(ConDecl.getI1());
		if(conObj != Tab.noObj) {
			report_error("Dvostruka definicija konstante: " +  ConDecl.getI1(), ConDecl);
		}
		else {
			if(constType.assignableTo(currentType)) {
				conObj = Tab.insert(Obj.Con, ConDecl.getI1(), currentType);
				conObj.setAdr(constant);
			}else { report_error("Neadekvatna dodela konstanti: " +  ConDecl.getI1(), ConDecl);  }
			
		}
		
	}
	
	@Override
	public void visit(ConstantN ConstantN) {
		constant = ConstantN.getN1();
		constType = Tab.intType;
	}
	@Override
	public void visit(ConstantC ConstantC) {
		constant = ConstantC.getC1();
		constType = Tab.charType;
	}
	@Override
	public void visit(ConstantB ConstantB) {
		constant = ConstantB.getB1();
		constType = boolType;
	}
	
	@Override
	public void visit(Type Type) {
		Obj typeObj = Tab.find(Type.getI1());
		if(typeObj == Tab.noObj) {
			report_error("Nepostojeci tip podatka: " +  Type.getI1(), Type);
			Type.struct = currentType = Tab.noType;
		}
		else if(typeObj.getKind()!= Obj.Type) {
			report_error("Neadekvatan tip podataka: " + Type.getI1(), Type);
			Type.struct = currentType = Tab.noType;
		}
		else {
			Type.struct = currentType = typeObj.getType();
		}
	}
	
	
	// GLOBAL VARIABLE
	@Override
	public void visit(VarDeclVar VarDeclVar) {
		Obj varObj = null;
		if(currentMethod==null) {
			varObj = Tab.find(VarDeclVar.getI1());
		}else {
			varObj = Tab.currentScope().findSymbol(VarDeclVar.getI1());
		}
		 
		if(varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var, VarDeclVar.getI1(), currentType);
		}
		else {
			report_error("Dvostruka definicija promenljive: " +  VarDeclVar.getI1(), VarDeclVar);
		}
	}
	
	@Override
	public void visit(VarDeclArray VarDeclArray) {
		Obj varObj = null;
		if(currentMethod==null) {
			varObj = Tab.find(VarDeclArray.getI1());
		}else {
			varObj = Tab.currentScope().findSymbol(VarDeclArray.getI1());
		}
		if(varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var, VarDeclArray.getI1(), new Struct(Struct.Array, currentType));
		}else {
			report_error("Dvostruka definicija promenljive: " +  VarDeclArray.getI1(), VarDeclArray);
		}
	}
	
	
	//METHOD
	@Override
	public void visit(MethodName MethodName) {
		if(MethodName.getI1().equalsIgnoreCase("main")) {
			mainHappened = true;
		}
		MethodName.obj = currentMethod = Tab.insert(Obj.Meth, MethodName.getI1(), Tab.noType);
		Tab.openScope();
	}
	
	@Override
	public void visit(MethodDecl MethodDecl) {
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		currentMethod = null;
	}
	
	
	
	// CONTEXTS CONDITION
	
	//designator
	@Override
	public void visit(DesignatorVar designatorVar) {
		 Obj varObj = Tab.find(designatorVar.getI1());// trazenje da li postoji promenljiva u kodu
		 if(varObj == Tab.noObj) {
			 report_error("Pristup nedefinisanoj promenljivoj: " + designatorVar.getI1(), designatorVar);
			 designatorVar.obj = Tab.noObj;
		 }
		 else if(varObj.getKind()!= Obj.Var && varObj.getKind()!= Obj.Con && varObj.getKind()!= Obj.Meth) {
			 report_error("Neadekvatna promenljiva: "+designatorVar.getI1(), designatorVar);
			 designatorVar.obj = Tab.noObj;
		 }
		 else {
			 designatorVar.obj = varObj;
			 reportSymbolUsage(varObj, designatorVar);
		 }
	}
	
	@Override
	public void visit(DesignatorName designatorName) {
	    Obj obj = Tab.find(designatorName.getI1());
	    if (obj == Tab.noObj) {
	        report_error("Pristup nedefinisanom imenu: " + designatorName.getI1(), designatorName);
	        designatorName.obj = Tab.noObj;
	    }
	    else if (obj.getKind() == Obj.Var && obj.getType().getKind() == Struct.Array) {
	        // Array variable  valid for DesignatorElem
	        designatorName.obj = obj;
	    }
	    else if (obj.getKind() == Obj.Type && obj.getType().getKind() == Struct.Enum) {
	        // Enum type  valid for DesignatorField
	        designatorName.obj = obj;
	    }
	    else {
	        report_error("Neadekvatan designator: " + designatorName.getI1(), designatorName);
	        designatorName.obj = Tab.noObj;
	    }
	}
	
	@Override
	public void visit(DesignatorElem designatorElem) {
	    Obj arrObj = designatorElem.getDesignatorName().obj;
	    if(arrObj == Tab.noObj) {
	        designatorElem.obj = Tab.noObj;
	    }
	    else if(!isIntCompatible(designatorElem.getExpr().struct)) {
	        report_error("indeksiranje sa ne int vrednosti", designatorElem);
	        designatorElem.obj = Tab.noObj;
	    }
	    else {
	        designatorElem.obj = new Obj(Obj.Elem, arrObj.getName() + "[$]", arrObj.getType().getElemType());
	        reportSymbolUsage(arrObj, designatorElem);
	    }
	}
	
	@Override
	public void visit(DesignatorField designatorField) {
		Obj nameObj = designatorField.getDesignatorName().obj;
	    
	    if (nameObj == Tab.noObj) {
	        designatorField.obj = Tab.noObj;
	        return;
	    }
	    
	    // Check that DesignatorName refers to an enum type
	    if (nameObj.getKind() != Obj.Type || nameObj.getType().getKind() != Struct.Enum) {
	        report_error("Pristup polju nad ne-enum tipom: " + nameObj.getName(), designatorField);
	        designatorField.obj = Tab.noObj;
	        return;
	    }
	    
	    // Look up the field name in the enum's member table
	    String fieldName = designatorField.getI2();
	    Obj memberObj = nameObj.getType().getMembersTable().searchKey(fieldName);
	    
	    if (memberObj == null) {
	        report_error("Enum " + nameObj.getName() + " nema konstantu: " + fieldName, designatorField);
	        designatorField.obj = Tab.noObj;
	    } else {
	        designatorField.obj = memberObj;
	        report_info("Pristup enum konstanti: " + nameObj.getName() + "." + fieldName, designatorField);
	        // TREBA DA VRATI CONST KOJA JE VREDNOST TOG ENUMA
	        reportSymbolUsage(memberObj, designatorField);
	    }
	}
	
	@Override
	public void visit(DesignatorLength designatorLength) {
		Obj nameObj = designatorLength.getDesignatorName().obj;
	    
	    if (nameObj == Tab.noObj) {
	        designatorLength.obj = Tab.noObj;
	        return;
	    }
	    
	    // Check that DesignatorName refers to an array
	    if (nameObj.getKind() != Obj.Var || nameObj.getType().getKind() != Struct.Array) {
	        report_error("Pristup length nad ne-nizovnom promenljivom: " + nameObj.getName(), designatorLength);
	        designatorLength.obj = Tab.noObj;
	    } else {
	        // .length returns an int, use Fld to prevent assignment (not Var, not Elem)
	        designatorLength.obj = new Obj(Obj.Fld, nameObj.getName() + ".length", Tab.intType);
	        report_info("Pristup duzini niza: " + nameObj.getName() + ".length", designatorLength);
	        reportSymbolUsage(nameObj, designatorLength);
	    }
	}
	
	@Override
	public void visit(DesignatorStatementACTP designatorStatementACTP) {
		Obj desObj = designatorStatementACTP.getDesignator().obj;
		if (desObj == Tab.noObj) {
			actualParamTypes.clear();
			return;
		}
		if (desObj.getKind() != Obj.Meth) {
			report_error("Poziv ne-metode kao statement: " + desObj.getName(), designatorStatementACTP);
			actualParamTypes.clear();
		} else {
			report_info("Poziv funkcije kao statement: " + desObj.getName(), designatorStatementACTP);
			checkMethodCall(desObj, designatorStatementACTP);
		}
	}
	//factorlist
	@Override
	public void visit(FactorIdent factorIdent) {
		if(!factorIdent.getExpr().struct.equals(Tab.intType)) {
			report_error("velicina niza nije int tipa", factorIdent);
			factorIdent.struct = Tab.noType;
		}else {
			factorIdent.struct = new Struct(Struct.Array, currentType);
		}
	}
	@Override
	public void visit(FactorExpr factorExpr) {
		factorExpr.struct = factorExpr.getExpr().struct;
	}
	
	@Override
	public void visit(FactorDA factorDA) {
	    Obj desObj = factorDA.getDesignator().obj;
	    if (desObj == Tab.noObj) {
	        factorDA.struct = Tab.noType;
	        actualParamTypes.clear();
	    } else if (desObj.getKind() != Obj.Meth) {
	        report_error("Poziv funkcije nad designatorom koji nije metoda: " + desObj.getName(), factorDA);
	        factorDA.struct = Tab.noType;
	        actualParamTypes.clear();
	    } else {
	        // Designator je metoda - vracamo njen povratni tip
	        factorDA.struct = desObj.getType();
	        report_info("Poziv funkcije: " + desObj.getName(), factorDA);
	        checkMethodCall(desObj, factorDA);
	    }
	}
	
	@Override
	public void visit(FactorChar FactorChar) {
		FactorChar.struct = Tab.charType;
	}
	@Override
	public void visit(FactorNum FactorNum) {
		FactorNum.struct = Tab.intType;
	}
	@Override
	public void visit(FactorBool FactorBool) {
		FactorBool.struct = boolType;
	}
	@Override
	public void visit(FactorD factorD) {
		factorD.struct = factorD.getDesignator().obj.getType();
	}
	// factor
	@Override
	public void visit(Factor Factor) {
	    if(Factor.getOptMinus() instanceof NegativeExpr) {
	        if(isIntCompatible(Factor.getFactorList().struct)) {
	            Factor.struct = Tab.intType;
	        } else {
	            report_error("Negacija ne int vrednosti", Factor);
	            Factor.struct = Tab.noType;
	        }
	    } else {
	        Factor.struct = Factor.getFactorList().struct;
	    }
	}
	
	
	//Expr
	
	@Override
	public void visit(FactorTerm FactorTerm) {
		FactorTerm.struct = FactorTerm.getFactor().struct;
	}
	
	@Override
	public void visit(MulTerm MulTerm) {
	    Struct left = MulTerm.getTerm().struct;
	    Struct right = MulTerm.getFactor().struct;
	    if(isIntCompatible(left) && isIntCompatible(right)) {
	        MulTerm.struct = Tab.intType;
	    } else {
	        report_error("Mulop operacija ne int vrednosti", MulTerm);
	        MulTerm.struct = Tab.noType;
	    }
	}
	@Override
	public void visit(AddopTermListE AddopTermListE) {
		AddopTermListE.struct = Tab.nullType;
	}
	@Override
	public void visit(AddopTermList1 AddopTermList1) {
	    if( (AddopTermList1.getAddopTermList().struct.equals(Tab.nullType) || isIntCompatible(AddopTermList1.getAddopTermList().struct))
	        && isIntCompatible(AddopTermList1.getTerm().struct)) {
	        AddopTermList1.struct = Tab.intType;
	    } else {
	        report_error("Addop operacija ne int vrednosti [AddopTermList1]", AddopTermList1);
	        AddopTermList1.struct = Tab.noType;
	    }
	}
	@Override
	public void visit(AddExpr AddExpr) {
	    if (AddExpr.getAddopTermList().struct.equals(Tab.nullType)) {
	        AddExpr.struct = AddExpr.getTerm().struct;
	    } else if (isIntCompatible(AddExpr.getTerm().struct)
	            && AddExpr.getAddopTermList().struct.equals(Tab.intType)) {
	        AddExpr.struct = Tab.intType;
	    } else {
	        report_error("operacija ne int vrednosti [AddExpr]", AddExpr);
	        AddExpr.struct = Tab.noType;
	    }
	}
	
	
	// designator Statements
	
	@Override
	public void visit(DesignatorStatementASS designatorStatementASS) {
	    int kind = designatorStatementASS.getDesignator().obj.getKind();
	    if(kind != Obj.Var && kind != Obj.Elem) {
	        report_error("dodela u neadekvatnu promenljivu: " + designatorStatementASS.getDesignator().obj.getName(), designatorStatementASS);
	    } else if(!designatorStatementASS.getExpr().struct.assignableTo(designatorStatementASS.getDesignator().obj.getType())
	              && !isEnumIntCompatible(designatorStatementASS.getExpr().struct, designatorStatementASS.getDesignator().obj.getType())) {
	        report_error("neadekvatna dodela vrednosti u promenljivu: " + designatorStatementASS.getDesignator().obj.getName(), designatorStatementASS);
	    }
	}
	
	@Override
	public void visit(DesignatorStatementINC designatorStatementINC) {
		int kind = designatorStatementINC.getDesignator().obj.getKind();
		if(kind != Obj.Var && kind != Obj.Elem) {
			report_error("inkrement neadekvatne promenljive: " + designatorStatementINC.getDesignator().obj.getName(), designatorStatementINC);
		}else if(!designatorStatementINC.getDesignator().obj.getType().equals(Tab.intType)) {
			report_error("inkrement ne int promenljive: " + designatorStatementINC.getDesignator().obj.getName(), designatorStatementINC);
		}
	}
	@Override
	public void visit(DesignatorStatementDEC DesignatorStatementDEC) {
		int kind = DesignatorStatementDEC.getDesignator().obj.getKind();
		if(kind != Obj.Var && kind != Obj.Elem) {
			report_error("dekrement neadekvatne promenljive: " + DesignatorStatementDEC.getDesignator().obj.getName(), DesignatorStatementDEC);
		}else if(!DesignatorStatementDEC.getDesignator().obj.getType().equals(Tab.intType)) {
			report_error("dekrement ne int promenljive: " + DesignatorStatementDEC.getDesignator().obj.getName(), DesignatorStatementDEC);
		}
	}
	
	// STATEMENT
	@Override
	public void visit(StatementREAD StatementREAD) {
		int kind = StatementREAD.getDesignator().obj.getKind();
		Struct tip = StatementREAD.getDesignator().obj.getType();
		if(kind != Obj.Var && kind != Obj.Elem) {
			report_error("READ operacija neadekvatne promenljive: " + StatementREAD.getDesignator().obj.getName(), StatementREAD);
		}else if(!tip.equals(Tab.intType) && !tip.equals(Tab.charType) && !tip.equals(boolType) ) {
			report_error("READ operacija ne int/char/bool promenljive: " + StatementREAD.getDesignator().obj.getName(), StatementREAD);
		}
	}
	
	@Override
	public void visit(StatementP1 StatementP1) {
		
		Struct tip = StatementP1.getExpr().struct;
		if(!tip.equals(Tab.intType) && !tip.equals(Tab.charType) && !tip.equals(boolType)  && tip.getKind() != Struct.Enum ) {
			report_error("Print operacija ne int/char/bool izraza: " , StatementP1);
		}
	}
	
	@Override
	public void visit(StatementP2 StatementP2) {
		
		Struct tip = StatementP2.getExpr().struct;
		if(!tip.equals(Tab.intType) && !tip.equals(Tab.charType) && !tip.equals(boolType)  && tip.getKind() != Struct.Enum) {
			report_error("Print operacija ne int/char/bool izraza: " , StatementP2);
		}
	}
	
	// ACTPARS
	
	@Override
	public void visit(ActListExpr actListExpr) {
	    actualParamTypes.add(actListExpr.getExpr().struct);
	}
	@Override
	public void visit(ActListComma actListComma) {
	    actualParamTypes.add(actListComma.getExpr().struct);
	}
	
	
	// CONDFACT
	
	@Override
	public void visit(CondFactRelop condFactRelop) {  // moze bude bool ili int
		// Compute effective type of left side (Term AddopTermList)
	    Struct leftTerm = condFactRelop.getTerm().struct;
	    Struct leftAddop = condFactRelop.getAddopTermList().struct;
	    Struct left;
	    if (leftAddop.equals(Tab.nullType)) {                                              // MOZDA TREBA DA SE DODAJU JOS PROVERA
	        left = leftTerm;
	    } else {
	        left = leftAddop; // already validated as intType in AddopTermList1
	    }
	    // Compute effective type of right side (Term1 AddopTermList1)
	    Struct rightTerm = condFactRelop.getTerm1().struct;
	    Struct rightAddop = condFactRelop.getAddopTermList2().struct;
	    Struct right;
	    if (rightAddop.equals(Tab.nullType)) {                                              // MOZDA TREBA DA SE DODAJU JOS PROVERA
	        right = rightTerm;
	    } else {
	        right = rightAddop;
	    }
	   
	    // Check 1: Types must be compatible
	    if (!left.compatibleWith(right) && !isEnumIntCompatible(left, right)){
	        report_error("Tipovi izraza u relacionom izrazu nisu kompatibilni", condFactRelop);
	        condFactRelop.struct = Tab.noType;
	    }
	    // Check 2: For array types, only == and != are allowed
	    if (left.getKind() == Struct.Array || right.getKind() == Struct.Array) {
	        Relop relop = condFactRelop.getRelop();
	        if (!(relop instanceof RelopEQ) && !(relop instanceof RelopNE)) {
	            report_error("Uz promenljive tipa niza mogu se koristiti samo operatori != i ==", condFactRelop);
	            condFactRelop.struct = Tab.noType;
	        }
	    }
	    
	    condFactRelop.struct = Tab.intType;
	    
	}
	@Override
	public void visit(CondFactBez condFactBez) {//ok mora bude bool tipa
	    Struct termType = condFactBez.getTerm().struct;
	    Struct addopType = condFactBez.getAddopTermList().struct;
	    
	    if (addopType.equals(Tab.nullType)) {
	    	if(!isIntCompatible(termType) && !termType.equals(boolType)) {
	    		report_error("Term nije int vrednosti [condFactBez]", condFactBez);
	    		condFactBez.struct = Tab.noType;
	    	}else {
	    		condFactBez.struct = termType;//maybe treba bude bool
	    	}
	        
	    } else if (addopType.equals(Tab.noType)) {
	    	report_error("addop nije int vrednosti [condFactBez]", condFactBez);
    		condFactBez.struct = Tab.noType;
	    }else {
	    	if(termType.equals(Tab.noType)) {
	    		report_error("term nije int vrednosti [condFactBez]", condFactBez);
	    		condFactBez.struct = Tab.noType;
	    	}
	    	if(isIntCompatible(termType)) {
	    	    condFactBez.struct = Tab.intType;
	    	}else if(termType.equals(boolType)) {
	    		condFactBez.struct =boolType;
	    	}
	    	else {
	    		report_error("term nije int vrednosti [condFactBez]", condFactBez);
	    		condFactBez.struct = Tab.noType;
	    	}
	    }
	}
//	// EXPR CONDFACT

	@Override
	public void visit(TernaryExpr ternaryExpr) {//ok
		Struct condType = ternaryExpr.getCondFact().struct;
		if (!isIntCompatible(condType) && !condType.equals(boolType)) {
	        report_error("Uslov ternarnog izraza mora biti tipa int ili bool", ternaryExpr);
	        ternaryExpr.struct = Tab.noType;
	    }else {
			Struct trueType = ternaryExpr.getExpr().struct;
		    Struct falseType = ternaryExpr.getExpr1().struct;
		    if (!trueType.compatibleWith(falseType)) {
		        report_error("Drugi i treci izraz ternarnog operatora moraju biti istog tipa", ternaryExpr);
		        ternaryExpr.struct = Tab.noType;
		    } else {
		        ternaryExpr.struct = trueType;                              
		    }
	    }
	}
	

}
