package IR;

public class Lable {
    static private Integer cnt = 0;

    static public Lable getLable() {
        return new Lable((cnt++).toString());
    }

    @Override
    public String toString() {
        return "b_" + lable;
    }

    private String lable;

    public Lable(String lable) {
        this.lable = lable;
    }

//    @Override
//    public String toString() {
//        return "#" + lable;
//    }
//
//    public String getName() {
//        boolean isNum = lable.matches("[0-9]+");
//        if (isNum) return "L_" + lable;
//        else return lable;
//    }
}
