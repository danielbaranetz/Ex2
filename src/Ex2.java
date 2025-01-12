import java.util.*;

public class Ex2 {

    public Ex2() {}

    // Check if input string can be parsed as a number
    public static boolean isNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Validate if input is text according to specified rules
    static boolean isText(String s) {
        return !isNumber(s) && !isForm(s) && !s.startsWith("=");
    }


    // Validate formula format
    public static boolean isForm(String input) {
        if (input == null || !input.startsWith("=")) {
            return false;
        }

        String formula = input.substring(1).trim();
        if (formula.contains("=")) { return false; }
        return !formula.isEmpty() &&
                areParenthesesBalanced(formula) &&
                !formula.matches(".*([+\\-*/]{2,}).*");
    }

    // Compute formula value using sheet data
    public static double computeForm(String form, Ex2Sheet sheet) {
        if (form == null || !form.startsWith("=")) {
            throw new IllegalArgumentException("Invalid formula: " + form);
        }

        try {
            String formula = form.substring(1).trim();
            String replacedFormula = replaceCellReferences(formula, sheet);
            return evalFormula(replacedFormula);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error computing formula: " + form);
        }
    }

    // Replace cell references with their values
    public static String replaceCellReferences(String formula, Ex2Sheet sheet) {
        StringBuilder result = new StringBuilder();
        int index = 0;

        while (index < formula.length()) {
            char currentChar = formula.charAt(index);

            if (Character.isLetter(currentChar)) {
                int endIndex = index;
                while (endIndex < formula.length() &&
                        Character.isLetterOrDigit(formula.charAt(endIndex))) {
                    endIndex++;
                }

                String cellRef = formula.substring(index, endIndex).toUpperCase();
                int[] coords = sheet.parseCoordinates(cellRef);
                String cellValue = sheet.eval(coords[0], coords[1]);

                if (cellValue == null || cellValue.trim().isEmpty()) {
                    throw new IllegalArgumentException(
                            "Formula contains a reference to an empty cell: " + cellRef);
                }

                result.append(cellValue);
                index = endIndex;
            } else {
                result.append(currentChar);
                index++;
            }
        }

        return result.toString();
    }

    // Check if parentheses are properly balanced
    private static boolean areParenthesesBalanced(String formula) {
        int count = 0;

        for (char c : formula.toCharArray()) {
            if (c == '(') count++;
            else if (c == ')') count--;

            if (count < 0) return false;
        }

        return count == 0;
    }

    // Evaluate formula and return result
    public static double evalFormula(String formula) {
        try {
            List<String> tokens = tokenizeFormula(formula.trim());
            return evaluateTokens(tokens);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }
    }

    // Convert formula string to tokens
    private static List<String> tokenizeFormula(String formula) {
        List<String> tokens = new ArrayList<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            // Handle negative sign at the beginning or after an operator or opening parenthesis
            if (c == '-' && (i == 0 || isOperator(formula.charAt(i - 1)) || formula.charAt(i - 1) == '(')) {
                numberBuffer.append(c); // Treat the '-' as part of the number
            } else if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c); // Append digits and decimal points to the current number
            } else {
                // When a complete number is found, add it to tokens
                if (numberBuffer.length() > 0) {
                    tokens.add(numberBuffer.toString());
                    numberBuffer.setLength(0);
                }
                // Handle operators and parentheses
                if (!Character.isWhitespace(c)) {
                    tokens.add(String.valueOf(c));
                }
            }
        }

        // If there's a number left in the buffer, add it as the last token
        if (numberBuffer.length() > 0) {
            tokens.add(numberBuffer.toString());
        }

        return tokens;
    }


    // Evaluate tokenized formula
    private static double evaluateTokens(List<String> tokens) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                numbers.push(Double.parseDouble(token));
            } else if (token.equals("(")) {
                operators.push('(');
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    processTopOperator(numbers, operators);
                }
                if (operators.isEmpty() || operators.peek() != '(') {
                    throw new IllegalArgumentException("Mismatched parentheses in formula.");
                }
                operators.pop();
            } else if (isOperator(token.charAt(0))) {
                while (!operators.isEmpty() &&
                        precedence(operators.peek()) >= precedence(token.charAt(0))) {
                    processTopOperator(numbers, operators);
                }
                operators.push(token.charAt(0));
            }
        }

        while (!operators.isEmpty()) {
            processTopOperator(numbers, operators);
        }

        if (numbers.size() != 1) {
            throw new IllegalArgumentException("Invalid formula structure.");
        }

        return numbers.pop();
    }

    // Check if character is a valid operator
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    // Get operator precedence
    private static int precedence(char operator) {
        return switch (operator) {
            case '*', '/' -> 2;
            case '+', '-' -> 1;
            default -> -1;
        };
    }

    // Process operator from stack
    private static void processTopOperator(Stack<Double> numbers, Stack<Character> operators) {
        if (numbers.size() < 2) {
            throw new IllegalArgumentException("Invalid formula: missing operands.");
        }

        double b = numbers.pop();
        double a = numbers.pop();
        char op = operators.pop();
        numbers.push(applyOperator(op, a, b));
    }

    // Apply operator to operands
// Apply operator to operands
    private static double applyOperator(char operator, double left, double right) {
        return switch (operator) {
            case '+' -> left + right;
            case '-' -> left - right;
            case '*' -> left * right;
            case '/' -> left / right; // Direct division without checking for zero
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

}