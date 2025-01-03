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
        if (text == null || !text.startsWith("=")) {
            return false; // It must start with '='
        }

        String formula = text.substring(1); // Remove the '=' at the start
        boolean lastWasOperator = false; // Track if the last character was an operator

        for (int i = 0; i < formula.length(); i++) {
            char currentChar = formula.charAt(i);

            // Check if the character is an operator
            if (isOperator(currentChar)) {
                // Invalid if an operator is the first character or if two operators are adjacent
                if (i == 0 || lastWasOperator) {
                    return false;
                }
                lastWasOperator = true; // Mark that the last character was an operator
            } else {
                lastWasOperator = false; // Mark that the last character was not an operator
            }

            // Check if it's a valid number or letter (for cell reference)
            if (Character.isDigit(currentChar) || Character.isLetter(currentChar)) {
                continue; // Valid number or letter, move on to the next character
            }

            // Check for cell reference (e.g., A1, B5, etc.)
            if (Character.isLetter(currentChar)) {
                int j = i + 1;
                // Ensure the letter is followed by one or more digits
                while (j < formula.length() && Character.isDigit(formula.charAt(j))) {
                    j++;
                }
                if (j == i + 1) { // Cell must have a number after the letter
                    return false;
                }
                i = j - 1; // Skip over the digits of the cell reference
                continue;
            }

            // Reject invalid characters like `%`, or unsupported characters
            if (!isOperator(currentChar) && !Character.isDigit(currentChar) && !Character.isLetter(currentChar)) {
                return false;
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

        String formula = text.substring(1);

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