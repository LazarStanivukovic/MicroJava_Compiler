// generated with ast extension for cup
// version 0.8
// 16/1/2026 15:39:15


package rs.ac.bg.etf.pp1.ast;

public class EnumElemListMore extends EnumElemList {

    private EnumElemList EnumElemList;
    private EnumElem EnumElem;

    public EnumElemListMore (EnumElemList EnumElemList, EnumElem EnumElem) {
        this.EnumElemList=EnumElemList;
        if(EnumElemList!=null) EnumElemList.setParent(this);
        this.EnumElem=EnumElem;
        if(EnumElem!=null) EnumElem.setParent(this);
    }

    public EnumElemList getEnumElemList() {
        return EnumElemList;
    }

    public void setEnumElemList(EnumElemList EnumElemList) {
        this.EnumElemList=EnumElemList;
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
        if(EnumElemList!=null) EnumElemList.accept(visitor);
        if(EnumElem!=null) EnumElem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumElemList!=null) EnumElemList.traverseTopDown(visitor);
        if(EnumElem!=null) EnumElem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumElemList!=null) EnumElemList.traverseBottomUp(visitor);
        if(EnumElem!=null) EnumElem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumElemListMore(\n");

        if(EnumElemList!=null)
            buffer.append(EnumElemList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EnumElem!=null)
            buffer.append(EnumElem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumElemListMore]");
        return buffer.toString();
    }
}
