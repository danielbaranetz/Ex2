// Add your documentation below:

public class SCell implements Cell {
    private String line;
    private int type;
    private int order;
    // Add your code here

    public SCell(String s) {
        // Add your code here
        setData(s);
    }

    @Override
    public int getOrder() {
        if (type == Ex2Utils.TEXT || type == Ex2Utils.NUMBER) {
            return 0; // text or numbers dont have dependencies
        }
        // נוסחה: ערך ברירת מחדל עד שחישוב תלות יבוצע
        return order;
    }

    //@Override
    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        line = s;
        if (s == null || s.isEmpty()) {
            type = Ex2Utils.TEXT;
            return;
        }
        if (Ex2.isForm(s)) {
            type = Ex2Utils.FORM;
        } else if (Ex2.isNumber(s)) {
            type = Ex2Utils.NUMBER;
        } else {
            type = Ex2Utils.TEXT;
        }
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
    this.order = t;
    }
}