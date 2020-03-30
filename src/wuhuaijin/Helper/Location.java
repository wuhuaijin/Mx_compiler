package Helper;

import org.antlr.v4.runtime.Token;

public class Location {
    private int line;
    private int column;

    public Location(Token token) {
        this.line = token.getLine();
        this.column = token.getCharPositionInLine();
    }

    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getInfo() {
        return "Line info： " + line + ":" + column;
    }


}
