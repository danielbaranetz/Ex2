import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Ex2Test {

    @Test
    void testIsNumber() {
        assertTrue(Ex2.isNumber("123"));
        assertTrue(Ex2.isNumber("3.14"));
        assertFalse(Ex2.isNumber("abc"));
        assertFalse(Ex2.isNumber(null));
        assertFalse(Ex2.isNumber(""));
    }

    @Test
    void testIsText() {
        assertTrue(Ex2.isText("hello"));
        assertFalse(Ex2.isText("123"));
        assertFalse(Ex2.isText("=A1+B1"));
        assertFalse(Ex2.isText("=SUM(A1:B1)"));
    }

    @Test
    void testIsForm() {
        assertTrue(Ex2.isForm("=A1+10"));
        assertFalse(Ex2.isForm("123"));
        assertFalse(Ex2.isForm("hello"));
        assertFalse(Ex2.isForm(null));
        assertFalse(Ex2.isForm(""));
        assertFalse(Ex2.isForm("=A1++B1")); // Invalid formula
        assertFalse(Ex2.isForm("=(A1+B1")); // Unbalanced parentheses
    }

    @Test
    void testComputeForm() {
        Ex2Sheet mockSheet = new Ex2Sheet() {
            @Override
            public int[] parseCoordinates(String ref) {
                if (ref.equals("A1")) return new int[]{0, 0};
                if (ref.equals("B1")) return new int[]{0, 1};
                throw new IllegalArgumentException("Unknown cell reference: " + ref);
            }

            @Override
            public String eval(int x, int y) {
                if (x == 0 && y == 0) return "5";
                if (x == 0 && y == 1) return "10";
                return "0";
            }
        };

        assertEquals(15.0, Ex2.computeForm("=A1+B1", mockSheet));
        assertEquals(50.0, Ex2.computeForm("=A1*B1", mockSheet));
        assertThrows(IllegalArgumentException.class, () -> Ex2.computeForm("=A1+/B1", mockSheet));
        assertThrows(IllegalArgumentException.class, () -> Ex2.computeForm("=A1+", mockSheet));
        assertThrows(IllegalArgumentException.class, () -> Ex2.computeForm("=A1+B1+C1", mockSheet)); // Invalid references
    }
}
