public class Ex2 {
    static boolean isNumber(String s) {
        try {
            if (s.matches("\\d+")) {
                Double.parseDouble(s);
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    static boolean isText(String s) {
        return !isNumber(s) && !isForm(s) && !s.startsWith("=");
    }

    public static boolean isForm(String text) {
        if (text == null || !text.startsWith("=") || text.endsWith("=")) {
            return false; // It must start with '='
        }
        String formula = text.substring(1);
        for (int i = 0; i < formula.length(); i++) {
            char currentChar = formula.charAt(i);
            if (isOperator(currentChar)) {
                if (i == formula.length() - 1 || isOperator(formula.charAt(i + 1)) || isOperator(formula.charAt(0))) {
                    return false;
                }
            }
            // Check if it's a cell reference (e.g., A1, B5)
            if (Character.isLetter(currentChar)) {
                // Ensure the letter is followed by one or more digits
                int j = i + 1;
                while (j < formula.length() && Character.isDigit(formula.charAt(j))) {
                    j++;
                }
                if (j == i + 1 || j > i + 2) { // cell must have exactly one letter and at least one digit
                    return false;
                }
                i = j - 1; // Skip the digits part for the next iteration
            }
        }
        return true;
    }

    public static boolean isOperator(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/');
    }

    public static Double computeForm(String text) {
        if (text == null || !text.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula: " + text);
        }

        // הסרת סימן "=" מהתחלה
        String formula = text.substring(1);

        // חישוב הנוסחה
        return evaluateExpression(formula);
    }

    private static Double evaluateExpression(String expression) {
        expression = expression.replaceAll(" ", ""); // הסרת רווחים מיותרים
        int openIndex = expression.lastIndexOf('(');
        if (openIndex != -1) {
            int closeIndex = expression.indexOf(')', openIndex);
            if (closeIndex == -1) {
                throw new IllegalArgumentException("Mismatched parentheses in formula: " + expression);
            }

            String innerExpression = expression.substring(openIndex + 1, closeIndex);
            double innerResult = evaluateExpression(innerExpression);
            expression = expression.substring(0, openIndex) + innerResult + expression.substring(closeIndex + 1);
            return evaluateExpression(expression);
        }

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '*' || c == '/') {
                int leftIndex = findLeftOperand(expression, i);
                int rightIndex = findRightOperand(expression, i);

                double leftOperand = Double.parseDouble(expression.substring(leftIndex, i));
                double rightOperand = Double.parseDouble(expression.substring(i + 1, rightIndex + 1));
                double result = (c == '*') ? leftOperand * rightOperand : leftOperand / rightOperand;

                expression = expression.substring(0, leftIndex) + result + expression.substring(rightIndex + 1);
                return evaluateExpression(expression);
            }
        }

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '+' || c == '-') {
                int leftIndex = findLeftOperand(expression, i);
                int rightIndex = findRightOperand(expression, i);

                double leftOperand = Double.parseDouble(expression.substring(leftIndex, i));
                double rightOperand = Double.parseDouble(expression.substring(i + 1, rightIndex + 1));
                double result = (c == '+') ? leftOperand + rightOperand : leftOperand - rightOperand;

                expression = expression.substring(0, leftIndex) + result + expression.substring(rightIndex + 1);
                return evaluateExpression(expression);
            }
        }

        return Double.parseDouble(expression);
    }

    private static int findLeftOperand(String expression, int index) {
        int i = index - 1;
        while (i >= 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
            i--;
        }
        return i + 1;
    }

    private static int findRightOperand(String expression, int index) {
        int i = index + 1;
        while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
            i++;
        }
        return i - 1;
    }
}