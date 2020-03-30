package SymbolTable.Symbol;

import Helper.Location;

import Helper.Location;

public abstract class Symbol {
    protected String id;
    private boolean referred;
    private Location location;

    public Symbol(String id, Location location){
        this.id = id;
        this.referred = false;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isReferred() {
        return referred;
    }

    public void setReferred(boolean referred) {
        this.referred = referred;
    }
}
