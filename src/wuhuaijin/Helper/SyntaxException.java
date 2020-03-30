package Helper;


public class SyntaxException extends RuntimeException{

    private Location location;
    private String info;

    public SyntaxException(){}

    public SyntaxException(Location location, String info) {
        this.location = location;
        this.info = info;
    }

    @Override
    public String getMessage() {
//        if (location == null) System.out.println("???? ridiculous ??");
//        return "location: "   + " SyntaxError " + info;
        if (location != null ) return "location: "  + location.getInfo() + " SyntaxError " + info;
        else return "nothing";
    }
}

