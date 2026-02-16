// generated with ast extension for cup
// version 0.8
// 16/1/2026 15:39:15


package rs.ac.bg.etf.pp1.ast;

public class CondFactRelop extends CondFact {

    private Term Term;
    private AddopTermList AddopTermList;
    private Relop Relop;
    private Term Term1;
    private AddopTermList AddopTermList2;

    public CondFactRelop (Term Term, AddopTermList AddopTermList, Relop Relop, Term Term1, AddopTermList AddopTermList2) {
        this.Term=Term;
        if(Term!=null) Term.setParent(this);
        this.AddopTermList=AddopTermList;
        if(AddopTermList!=null) AddopTermList.setParent(this);
        this.Relop=Relop;
        if(Relop!=null) Relop.setParent(this);
        this.Term1=Term1;
        if(Term1!=null) Term1.setParent(this);
        this.AddopTermList2=AddopTermList2;
        if(AddopTermList2!=null) AddopTermList2.setParent(this);
    }

    public Term getTerm() {
        return Term;
    }

    public void setTerm(Term Term) {
        this.Term=Term;
    }

    public AddopTermList getAddopTermList() {
        return AddopTermList;
    }

    public void setAddopTermList(AddopTermList AddopTermList) {
        this.AddopTermList=AddopTermList;
    }

    public Relop getRelop() {
        return Relop;
    }

    public void setRelop(Relop Relop) {
        this.Relop=Relop;
    }

    public Term getTerm1() {
        return Term1;
    }

    public void setTerm1(Term Term1) {
        this.Term1=Term1;
    }

    public AddopTermList getAddopTermList2() {
        return AddopTermList2;
    }

    public void setAddopTermList2(AddopTermList AddopTermList2) {
        this.AddopTermList2=AddopTermList2;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Term!=null) Term.accept(visitor);
        if(AddopTermList!=null) AddopTermList.accept(visitor);
        if(Relop!=null) Relop.accept(visitor);
        if(Term1!=null) Term1.accept(visitor);
        if(AddopTermList2!=null) AddopTermList2.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Term!=null) Term.traverseTopDown(visitor);
        if(AddopTermList!=null) AddopTermList.traverseTopDown(visitor);
        if(Relop!=null) Relop.traverseTopDown(visitor);
        if(Term1!=null) Term1.traverseTopDown(visitor);
        if(AddopTermList2!=null) AddopTermList2.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Term!=null) Term.traverseBottomUp(visitor);
        if(AddopTermList!=null) AddopTermList.traverseBottomUp(visitor);
        if(Relop!=null) Relop.traverseBottomUp(visitor);
        if(Term1!=null) Term1.traverseBottomUp(visitor);
        if(AddopTermList2!=null) AddopTermList2.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFactRelop(\n");

        if(Term!=null)
            buffer.append(Term.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AddopTermList!=null)
            buffer.append(AddopTermList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Relop!=null)
            buffer.append(Relop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Term1!=null)
            buffer.append(Term1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AddopTermList2!=null)
            buffer.append(AddopTermList2.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFactRelop]");
        return buffer.toString();
    }
}
