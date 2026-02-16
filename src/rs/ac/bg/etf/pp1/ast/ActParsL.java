// generated with ast extension for cup
// version 0.8
// 16/1/2026 15:39:15


package rs.ac.bg.etf.pp1.ast;

public class ActParsL extends ActPars {

    private ActList ActList;

    public ActParsL (ActList ActList) {
        this.ActList=ActList;
        if(ActList!=null) ActList.setParent(this);
    }

    public ActList getActList() {
        return ActList;
    }

    public void setActList(ActList ActList) {
        this.ActList=ActList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActList!=null) ActList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActList!=null) ActList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActList!=null) ActList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParsL(\n");

        if(ActList!=null)
            buffer.append(ActList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ActParsL]");
        return buffer.toString();
    }
}
