import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class Ex2 {

    static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static boolean isText(String s) {
        return !isNumber(s) && !isForm(s);
    }

    static boolean isForm(String s) {
        return s != null && s.startsWith("=");
    }


    static Double computeForm(String s) {
        if (isForm(s)) {
            String natureForm = s.substring(1); // Remove '='
            try {
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                Object result = engine.eval(natureForm);
                if (result instanceof Number) {
                    return ((Number) result).doubleValue(); // Ensure it's a Double
                } else {
                    System.out.println(Ex2Utils.ERR_FORM); // Log or handle error
                    return null; // Represent error
                }
            } catch (ScriptException e) {
                System.out.println(Ex2Utils.ERR_FORM); // Log or handle error
                return null;
            }
        }
        System.out.println(Ex2Utils.ERR_FORM_FORMAT); // Log or handle invalid format
        return null; // Not a formula
    }


}