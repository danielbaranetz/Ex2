// Add your documentation below:

public class CellEntry  implements Index2D {
    private int x;
    private int y;
    public CellEntry(int xx, int yy) {
        this.x = xx;
        this.y = yy;

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public int getX() {return this.x;}

    @Override
    public int getY() {return this.y;}

    public String toString() {
        return Ex2Utils.ABC[this.x] + this.y;
    }
}
