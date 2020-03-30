package AST;
import Helper.Location;

public class MainProgNode extends Node{
    private ProgNode prog;

    public MainProgNode(Location location, ProgNode prog) {
        super(location);
        this.prog = prog;
    }

    public ProgNode getProg() {
        return prog;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
