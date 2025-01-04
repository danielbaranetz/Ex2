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
        if (text == null || text.isEmpty() || text.charAt(0) != '=') {
            return false;
        }

        // Remove the '=' and validate the expression
        return isValidExpression(text.substring(1));
    }

    private static boolean isValidExpression(String expression) {
        // Base case: empty expression is invalid
        if (expression.isEmpty()) return false;

        int balance = 0; // To track parentheses balance
        boolean lastWasOperator = true; // Track last character to ensure valid sequences

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c)) {
                lastWasOperator = false; // Digit found, not an operator
            } else if (c == '(') {
                balance++;
                lastWasOperator = true;
            } else if (c == ')') {
                balance--;
                if (balance < 0) return false; // Unbalanced parentheses
                lastWasOperator = false;
            } else if ("+-*/".indexOf(c) >= 0) {
                if (lastWasOperator) return false; // Two operators in a row
                lastWasOperator = true;
            } else {
                return false; // Invalid character
            }
        }

        return balance == 0 && !lastWasOperator; // Parentheses must be balanced, end not on operator
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