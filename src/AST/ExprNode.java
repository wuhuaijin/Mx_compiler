package AST;
import Helper.Location;
import IR.Operand.*;
import SymbolTable.Symbol.*;
import SymbolTable.Type.*;

abstract public class ExprNode extends Node {
    private Boolean ifLvalue;
    private Type type;

    private Operand resultOpr;
    private Symbol symbol;

    public ExprNode(Location location){
        super(location);
        this.ifLvalue = null;
        this.type = null;
    }

    public Type getType() {
        return type;
    }

    public Boolean getIfLvalue() {
        return ifLvalue;
    }

    public void setIfLvalue(Boolean ifLvalue) {
        this.ifLvalue = ifLvalue;
    }

    public void setType(Type type) {
        this.type = type;
    }

    abstract public void accept(ASTVisitor visitor);

    public Operand getResultOpr() {
        return resultOpr;
    }

    public void setResultOpr(Operand resultOpr) {
        this.resultOpr = resultOpr;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
}
