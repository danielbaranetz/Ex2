public class Ex2Test {
    public static void main(String[] args) {
        testIsNumber();
        testIsText();
        testIsForm();
        testComputeForm();
    }

    public static void testIsNumber() {
        System.out.println("Testing isNumber...");
        assert Ex2.isNumber("123") : "Failed on 123";
        assert Ex2.isNumber("-45.67") : "Failed on -45.67";
        assert !Ex2.isNumber("1a") : "Failed on 1a";
        assert !Ex2.isNumber("hello") : "Failed on hello";
        assert !Ex2.isNumber("") : "Failed on empty string";
        assert !Ex2.isNumber(null) : "Failed on null";
        System.out.println("isNumber passed all tests!");
    }

    public static void testIsText() {
        System.out.println("Testing isText...");
        assert Ex2.isText("hello") : "Failed on hello";
        assert Ex2.isText("A1") : "Failed on A1";
        assert !Ex2.isText("123") : "Failed on 123";
        assert !Ex2.isText("=1+2") : "Failed on =1+2";
        assert Ex2.isText("=A1+B2") : "Failed on =A1+B2 (should not be text)";
        assert Ex2.isText("word123") : "Failed on word123";
        System.out.println("isText passed all tests!");
    }

    public static void testIsForm() {
        System.out.println("Testing isForm...");
        assert Ex2.isForm("=1+2") : "Failed on =1+2";
        assert Ex2.isForm("=5+5*(5+5)") : "Failed on this";
        assert Ex2.isForm("=(1+2)*3") : "Failed on =(1+2)*3";
        assert Ex2.isForm("=(A1+B2)") : "Failed on =(A1+B2)";
        assert !Ex2.isForm("123") : "Failed on 123";
        assert !Ex2.isForm("hello") : "Failed on hello";
        assert !Ex2.isForm("") : "Failed on empty string";
        System.out.println("isForm passed all tests!");
    }

    public static void testComputeForm() {
        System.out.println("Testing computeForm...");
        assert Ex2.computeForm("=1+2") == 3.0 : "Failed on =1+2";
        assert Ex2.computeForm("=2*3+4") == 10.0 : "Failed on =2*3+4";
        assert Ex2.computeForm("=(1+2)*3") == 9.0 : "Failed on =(1+2)*3";
        assert Ex2.computeForm("=((1+2)*3)-4") == 5.0 : "Failed on =((1+2)*3)-4";
        assert Ex2.computeForm("=1+2*3") == 7.0 : "Failed on =1+2*3";

        try {
            Ex2.computeForm("=1/0");
            assert false : "Failed to handle division by zero";
        } catch (ArithmeticException e) {
            System.out.println("Passed division by zero test.");
        }

        try {
            Ex2.computeForm("=(A1+B2)");
            assert false : "Failed to handle invalid Ex2 reference";
        } catch (IllegalArgumentException e) {
            System.out.println("Passed invalid Ex2 reference test.");
        }

        System.out.println("computeForm passed all tests!");
    }
}
