/**
 * Represents a single cell in the spreadsheet.
 * Each cell can contain a string, a number, or a formula.
 */
public class SCell implements Cell {
    private String line; // The content of the cell
    private int type; // The type of the cell (TEXT, NUMBER, FORM, etc.)
    private int order; // The evaluation order of the cell

    public SCell(String s) {
        setData(s);
        setOrder(0); // Default order is 0
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        line = s;

        if (line == null || line.trim().isEmpty()) {
            type = Ex2Utils.TEXT; // Empty cell is considered text
        } else if (Ex2.isNumber(s)) {
            type = Ex2Utils.NUMBER; // Cell contains a valid number
            line = Double.valueOf(s).toString(); // Normalize number representation
        } else if (Ex2.isText(s)) {
            type = Ex2Utils.TEXT; // Valid text
        } else if (Ex2.isForm(s)) {
            type = Ex2Utils.FORM; // Valid formula starts with '='
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT; // Invalid format
        }
        // ERR_CYCLE_FORM should only be set explicitly, not here.
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
        // Prevent overwriting cycle errors unless explicitly set
        if (type == Ex2Utils.ERR_CYCLE_FORM && t != Ex2Utils.ERR_CYCLE_FORM) {
            return;
        }
        type = t;
    }


    @Override
    public void setOrder(int t) {
        order = t;
    }
}
