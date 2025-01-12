import java.io.*;
import java.util.*;

public class Ex2Sheet implements Sheet {
    private final Cell[][] table;
    private final int width;
    private final int height;

    // Constructor to initialize the sheet with a given width and height.
    public Ex2Sheet(int width, int height) {
        this.width = width;
        this.height = height;
        this.table = new Cell[width][height]; // 2D array to store cells.
        initializeCells();
    }

    // Default constructor using predefined dimensions from Ex2Utils.
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    // Initializes all cells in the table to empty cells.
    private void initializeCells() {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                table[col][row] = new SCell(Ex2Utils.EMPTY_CELL); // Default empty cell.
            }
        }
    }

    // Checks if the given coordinates are within the bounds of the sheet.
    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Returns the width of the sheet.
    @Override
    public int width() {
        return width;
    }

    // Returns the height of the sheet.
    @Override
    public int height() {
        return height;
    }

    // Sets the value of a cell at the given coordinates and reevaluates the sheet.
    @Override
    public void set(int x, int y, String value) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Invalid cell coordinates"); // Validate input.
        }
        System.out.println("cell " + convertCoordinatesToCellName(x, y) + " is: " + value);
        table[x][y] = new SCell(value); // Update cell.
        eval(); // Reevaluate all cells to handle dependencies.
    }

    // Retrieves the cell at the given coordinates.
    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null; // Return null if out of bounds.
    }

    // Retrieves a cell by its reference (e.g., "A1").
    @Override
    public Cell get(String cellReference) {
        int[] coordinates = parseCoordinates(cellReference);
        return get(coordinates[0], coordinates[1]);
    }

    // Returns the value of a cell at the given coordinates, evaluating it if necessary.
    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        return (cell == null) ? Ex2Utils.EMPTY_CELL : eval(x, y); // Return empty if cell is null.
    }

    // Set to track cells currently being evaluated to prevent cycles.
    private final Set<String> evaluatingCells = new HashSet<>();

    // Evaluates the value of a cell at the given coordinates.
    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        String cellName = convertCoordinatesToCellName(x, y);

        if (evaluatingCells.contains(cellName)) { // Detect cyclic dependencies.
            cell.setType(Ex2Utils.ERR_CYCLE_FORM); // Set type to cycle error.
            return Ex2Utils.ERR_CYCLE; // Return the error text.
        }

        // Add to the set of currently evaluating cells.
        evaluatingCells.add(cellName);

        if (cell == null || cell.getData().isEmpty()) { // Empty cell handling.
            evaluatingCells.remove(cellName);
            return Ex2Utils.EMPTY_CELL;
        }

        if (cell.getType() == Ex2Utils.FORM) { // Formula evaluation.
            try {
                double result = Ex2.computeForm(cell.getData(), this); // Compute the formula.
                evaluatingCells.remove(cellName);
                return Double.toString(result); // Return the computed result as a string.
            } catch (IllegalArgumentException e) { // Catch formula errors.
                cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                evaluatingCells.remove(cellName);
                return Ex2Utils.ERR_FORM; // Return the error text.
            }
        }

        if (cell.getType() == Ex2Utils.ERR_FORM_FORMAT) { // Invalid formula format.
            evaluatingCells.remove(cellName);
            return Ex2Utils.ERR_FORM; // Return the error text.
        }

        if (cell.getType() == Ex2Utils.ERR_CYCLE_FORM) { // Cycle error handling.
            evaluatingCells.remove(cellName);
            return Ex2Utils.ERR_CYCLE; // Return the cycle error text.
        }

        // For numbers or text, return the raw data.
        evaluatingCells.remove(cellName);
        return cell.getData();
    }



    // Reevaluates the entire sheet.
    @Override
    public void eval() {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                eval(col, row);
            }
        }
    }

    // Calculates the depth of dependencies for each cell in the sheet.
    @Override
    public int[][] depth() {
        int[][] depths = new int[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                depths[col][row] = computeDepth(col, row, new HashSet<>());
            }
        }
        return depths;
    }

    // Computes the depth of dependencies for a specific cell.
    private int computeDepth(int x, int y, Set<String> visited) {
        if (!isIn(x, y)) return 0; // Out-of-bounds cells have no depth

        Cell cell = get(x, y);
        if (cell == null || cell.getType() != Ex2Utils.FORM) return 0; // Non-formulas have no depth

        String cellKey = x + "," + y;
        if (visited.contains(cellKey)) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM); // Mark as part of a cycle
            return -1; // Indicate cycle
        }

        visited.add(cellKey);

        int maxDepth = calculateMaxDepth(cell, visited);

        visited.remove(cellKey);

        if (maxDepth == -1) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM); // Mark as part of a cycle
        }

        return maxDepth == -1 ? -1 : maxDepth + 1; // Propagate cycle detection
    }


    // Helper method to calculate the maximum depth of dependencies in a formula.
    private int calculateMaxDepth(Cell cell, Set<String> visited) {
        String formula = cell.getData().substring(1); // Remove '=' marker
        int maxDepth = 0;

        for (String ref : formula.split("[^A-Za-z0-9]+")) {
            if (ref.matches("[A-Z][0-9]+")) { // Valid cell reference
                int[] coords = parseCoordinates(ref);
                int depth = computeDepth(coords[0], coords[1], visited);
                if (depth == -1) {
                    cell.setType(Ex2Utils.ERR_CYCLE_FORM); // Mark this cell as part of a cycle
                    return -1; // Propagate cycle detection
                }
                maxDepth = Math.max(maxDepth, depth);
            }
        }
        return maxDepth;
    }



    // Saves the current state of the sheet to a file.
    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment - this line should be ignored\n");
            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    Cell cell = table[col][row];
                    if (cell != null && !cell.getData().trim().isEmpty()) {
                        writer.write(col + "," + row + "," + cell.getData() + "\n");
                    }
                }
            }
        }
    }

    // Loads the sheet state from a file.
    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Ignore the header line.
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    try {
                        set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2]);
                    } catch (NumberFormatException ignored) { // Skip invalid entries.
                    }
                }
            }
        }
        eval(); // Reevaluate after loading.
    }

    // Parses a cell reference (e.g., "A1") into coordinates.
    public int[] parseCoordinates(String cellRef) {
        if (cellRef == null || cellRef.trim().isEmpty()) {
            throw new IllegalArgumentException("Cell reference cannot be null or empty.");
        }

        char col = cellRef.charAt(0);
        if (col < 'A' || col > 'Z') {
            throw new IllegalArgumentException("Invalid column in cell reference: " + cellRef);
        }

        try {
            int row = Integer.parseInt(cellRef.substring(1));
            return new int[]{col - 'A', row}; // Convert column to index.
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in cell reference: " + cellRef);
        }
    }

    // Converts coordinates to a cell reference (e.g., "A1").
    public static String convertCoordinatesToCellName(int x, int y) {
        return (char) ('A' + x) + Integer.toString(y);
    }
}