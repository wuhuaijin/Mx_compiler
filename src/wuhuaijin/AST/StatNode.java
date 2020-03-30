package AST;

import Helper.Location;

abstract public class StatNode extends Node{
    public StatNode(Location location){
        super(location);
    }

    abstract public void accept(ASTVisitor visitor);
}
