// generated with ast extension for cup
// version 0.8
// 16/1/2026 15:39:15


package rs.ac.bg.etf.pp1.ast;

public class EnumDeclList implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private EnumName EnumName;
    private EnumElemList EnumElemList;

    public EnumDeclList (EnumName EnumName, EnumElemList EnumElemList) {
        this.EnumName=EnumName;
        if(EnumName!=null) EnumName.setParent(this);
        this.EnumElemList=EnumElemList;
        if(EnumElemList!=null) EnumElemList.setParent(this);
    }

    public EnumName getEnumName() {
        return EnumName;
    }

    public void setEnumName(EnumName EnumName) {
        this.EnumName=EnumName;
    }

    public EnumElemList getEnumElemList() {
        return EnumElemList;
    }

    public void setEnumElemList(EnumElemList EnumElemList) {
        this.EnumElemList=EnumElemList;
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
        if(EnumName!=null) EnumName.accept(visitor);
        if(EnumElemList!=null) EnumElemList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumName!=null) EnumName.traverseTopDown(visitor);
        if(EnumElemList!=null) EnumElemList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumName!=null) EnumName.traverseBottomUp(visitor);
        if(EnumElemList!=null) EnumElemList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumDeclList(\n");

        if(EnumName!=null)
            buffer.append(EnumName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EnumElemList!=null)
            buffer.append(EnumElemList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumDeclList]");
        return buffer.toString();
    }
}
