# Ex2 Spreadsheet System

## 🚀 Project Overview
#### This project is a Java-based implementation of a spreadsheet system. The spreadsheet supports cell types such as strings, numbers, and formulas. It includes features for evaluating cell formulas, detecting circular references, saving/loading spreadsheets, and computing dependency depths.

## 📦 Key Features
- **Cell Types**: Supports text, numeric, and formula cells.
- **Formula Evaluation**: Handles formulas with references to other cells.
- **Cycle Detection**: Prevents and flags cyclic dependencies between cells.
- **Dynamic Reevaluation**: Automatically updates dependent cells upon changes.
- **Persistence**: Save and load the spreadsheet state to/from a file.
- **Dependency Analysis**: Compute the depth of dependencies for each cell.

## 💡 How to Run the Program
### Option 1: Using the JAR file
1. Clone the repository:
   ```bash
   git clone https://github.com/danielbaranetz/Ex2
   ```
2. Run the JAR file:
   ```bash
   java -jar path/to/Ex2.jar
   ```

### Option 2: Using IntelliJ IDEA
1. Clone the repository:
   ```bash
   git clone https://github.com/danielbaranetz/Ex2
   ```
2. Open the project in IntelliJ IDEA.
3. Run the `Main` class in the `Ex2.Main` package.

## 📖 Example Usage
### Interactive Session:
```
Set cell A1 to "5":
> set A1 5

Set cell B1 to "=A1+10":
> set B1 =A1+10

Evaluate cell B1:
> value B1
15

Save the spreadsheet to a file:
> save spreadsheet.txt

Load the spreadsheet from a file:
> load spreadsheet.txt
```

## 🔧 Methods and Functionalities
### Core Methods in `Ex2Sheet` Class:
- **`set(int x, int y, String value)`**: Sets the value of a cell at coordinates `(x, y)`.
- **`get(int x, int y)`**: Retrieves the cell object at `(x, y)`.
- **`eval(int x, int y)`**: Evaluates the value of a cell at `(x, y)`, handling formulas and dependencies.
- **`save(String fileName)`**: Saves the current spreadsheet state to a file.
- **`load(String fileName)`**: Loads spreadsheet data from a file(the first line is title and will be ignored).
- **`depth()`**: Returns a 2D array of dependency depths for all cells.

## 📂 Project Structure
```
Ex2
├── src
│   ├── Ex2Sheet.java       # Main spreadsheet implementation
│   ├── Cell.java           # Cell interface definition
│   ├── SCell.java          # Concrete cell implementation
│   ├── Sheet.java          # Ex2sheet interface definiton
│   ├── Ex2.java            # Utility methods and calc methods
│   ├── Index2D.java        # CellEntry interface definiton
│   ├── stDrawEx2.java      # Spreadsheet gui functions
│   ├── Ex2Utils.java       # Utility methods and final values
│   ├── Ex2SheetTest.java   # Unit tests for Ex2 functions
│   ├── Ex2GUI.java         # Gui functions
│   └── CellEntry.java      # Concrete Sheet implementation
└── README.md               # Project README
```

## 🛠️ Tools and Technologies
- **Java**: Core programming language.
- **IntelliJ IDEA**: IDE for development.
## 🧑‍💻 Author
- 🌐 [Daniel Baranetz GitHub](https://github.com/danielbaranetz)

---
Version0.0.1