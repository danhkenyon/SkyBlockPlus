package uk.ac.bsfc.sbp.utils.strings;

public class Placeholder {
    private String val;
    private Object obj;

    private Placeholder(String val, Object obj) {
        this.val = val;
        this.obj = obj;
    }

    public static Placeholder of(String val, Object obj) {
        return new Placeholder(val, obj);
    }

    public String val() {
        return val;
    }
    public Object obj() {
        return obj;
    }
}
