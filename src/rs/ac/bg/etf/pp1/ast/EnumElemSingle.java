// generated with ast extension for cup
// version 0.8
// 16/1/2026 15:39:15


package rs.ac.bg.etf.pp1.ast;

public class EnumElemSingle extends EnumElemList {

    private EnumElem EnumElem;

    public EnumElemSingle (EnumElem EnumElem) {
        this.EnumElem=EnumElem;
        if(EnumElem!=null) EnumElem.setParent(this);
    }

    public EnumElem getEnumElem() {
        return EnumElem;
    }

    public void setEnumElem(EnumElem EnumElem) {
        this.EnumElem=EnumElem;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(EnumElem!=null) EnumElem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumElem!=null) EnumElem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumElem!=null) EnumElem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumElemSingle(\n");

        if(EnumElem!=null)
            buffer.append(EnumElem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumElemSingle]");
        return buffer.toString();
    }
}
