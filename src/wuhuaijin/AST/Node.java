package AST;

import Helper.Location;
import Frontend.*;

abstract public class Node {

    private Location location;

    public Node(Location location){
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public abstract void accept(ASTVisitor visitor);
}
