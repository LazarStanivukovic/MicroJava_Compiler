// generated with ast extension for cup
// version 0.8
// 16/1/2026 15:39:15


package rs.ac.bg.etf.pp1.ast;

public class Factor implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Struct struct = null;

    private OptMinus OptMinus;
    private FactorList FactorList;

    public Factor (OptMinus OptMinus, FactorList FactorList) {
        this.OptMinus=OptMinus;
        if(OptMinus!=null) OptMinus.setParent(this);
        this.FactorList=FactorList;
        if(FactorList!=null) FactorList.setParent(this);
    }

    public OptMinus getOptMinus() {
        return OptMinus;
    }

    public void setOptMinus(OptMinus OptMinus) {
        this.OptMinus=OptMinus;
    }

    public FactorList getFactorList() {
        return FactorList;
    }

    public void setFactorList(FactorList FactorList) {
        this.FactorList=FactorList;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptMinus!=null) OptMinus.accept(visitor);
        if(FactorList!=null) FactorList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptMinus!=null) OptMinus.traverseTopDown(visitor);
        if(FactorList!=null) FactorList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptMinus!=null) OptMinus.traverseBottomUp(visitor);
        if(FactorList!=null) FactorList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Factor(\n");

        if(OptMinus!=null)
            buffer.append(OptMinus.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FactorList!=null)
            buffer.append(FactorList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Factor]");
        return buffer.toString();
    }
}
