package AST;

import Helper.Location;

abstract public class DefNode extends Node {

    public DefNode(Location location) {
        super(location);
    }

    public abstract void accept(ASTVisitor visitor);
}
