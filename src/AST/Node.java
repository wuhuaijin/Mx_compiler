package AST;

import Helper.Location;
import Frontend.*;

abstract public class Node {

    private Scope scope;

    private Location location;

    public Node(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public abstract void accept(ASTVisitor visitor);

    //for IR to get the var

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}
