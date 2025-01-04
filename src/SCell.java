public class SCell implements Cell {
    private String line; // הנתון של התא
    private int type;    // הסוג של התא
    private int order;   // סדר החישוב של התא

    public SCell(String s) {
        setData(s); // אתחול הנתונים והסוג
    }

    @Override
    public int getOrder() {
        if (type == Ex2Utils.TEXT || type == Ex2Utils.NUMBER) {
            return 0;
        }
        return order;
    }

    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        line = s;

        // Check if the string is empty or null, or if it's a valid text value
        if (s == null || s.isEmpty()) {
            type = Ex2Utils.TEXT;  // Empty, null, or regular text string is treated as TEXT
        } else if (Ex2.isText(s)) {
            type = Ex2Utils.TEXT;
        } else if (Ex2.isNumber(s)) {
            type = Ex2Utils.NUMBER;  // If it's a number, set type to NUMBER
        } else if (Ex2.isForm(s)) {
            line = Ex2.computeForm(s).toString();
            // If it's a valid formula, set type to FORM
            type = Ex2Utils.FORM;
        } else {
            line = Ex2Utils.ERR_FORM;
            type = Ex2Utils.ERR_FORM_FORMAT;
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