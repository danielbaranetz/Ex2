import java.io.*;
import java.util.*;

public class Ex2Sheet implements Sheet {
    private final Cell[][] table;
    private final int width;
    private final int height;

    public Ex2Sheet(int width, int height) {
        this.width = width;
        this.height = height;
        this.table = new Cell[width][height];
        initializeCells();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    private void initializeCells() {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                table[col][row] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public void set(int x, int y, String value) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }
        System.out.println("cell " + convertCoordinatesToCellName(x, y) + " is: " + value);
        table[x][y] = new SCell(value);
        eval();
    }

    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null;
    }

    @Override
    public Cell get(String cellReference) {
        int[] coordinates = parseCoordinates(cellReference);
        return get(coordinates[0], coordinates[1]);
    }

    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        return (cell == null) ? Ex2Utils.EMPTY_CELL : eval(x, y);
    }

    private final Set<String> evaluatingCells = new HashSet<>();

    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);
        String cellName = convertCoordinatesToCellName(x, y);

        if (evaluatingCells.contains(cellName)) {
            cell.setType(Ex2Utils.ERR_CYCLE_FORM);
            return Ex2Utils.ERR_CYCLE;
        }
        evaluatingCells.add(cellName);

        if (cell == null || cell.getData().isEmpty()) {
            evaluatingCells.remove(cellName);
            return Ex2Utils.EMPTY_CELL;
        }

        if (cell.getType() == Ex2Utils.FORM) {
            try {
                double result = Ex2.computeForm(cell.getData(), this);
                evaluatingCells.remove(cellName);
                return Double.toString(result);
            } catch (IllegalArgumentException e) {
                handleInvalidFormula(cell, cellName);
                cell.setData(Ex2Utils.ERR_FORM);
                return Ex2Utils.ERR_FORM;
            }
        }
        evaluatingCells.remove(cellName);
        return cell.getData();
    }



    private void handleInvalidFormula(Cell cell, String cellName) {
        if (get(cellName) != cell) {
            System.out.println("Invalid formula in cell " + cellName);
            cell.setType(Ex2Utils.ERR_FORM_FORMAT);
            cell.setData(Ex2Utils.ERR_FORM);
            evaluatingCells.remove(cellName);
        }
    }

    @Override
    public void eval() {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                eval(col, row);
            }
        }
    }

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

    private int computeDepth(int x, int y, Set<String> visited) {
        if (!isIn(x, y)) return 0;

        Cell cell = get(x, y);
        if (cell == null || cell.getType() != Ex2Utils.FORM) return 0;

        String cellKey = x + "," + y;
        if (visited.contains(cellKey)) return -1;

        visited.add(cellKey);
        int maxDepth = calculateMaxDepth(cell, visited);
        visited.remove(cellKey);

        return maxDepth + 1;
    }

    private int calculateMaxDepth(Cell cell, Set<String> visited) {
        String formula = cell.getData().substring(1);
        int maxDepth = 0;

        for (String ref : formula.split("[^A-Za-z0-9]+")) {
            if (ref.matches("[A-Z][0-9]+")) {
                int[] coords = parseCoordinates(ref);
                int depth = computeDepth(coords[0], coords[1], visited);
                if (depth == -1) return -1;
                maxDepth = Math.max(maxDepth, depth);
            }
        }
        return maxDepth;
    }

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

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    try {
                        set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2]);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        eval();
    }

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
            return new int[]{col - 'A', row};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row in cell reference: " + cellRef);
        }
    }

    public static String convertCoordinatesToCellName(int x, int y) {
        return (char) ('A' + x) + Integer.toString(y);
    }
}