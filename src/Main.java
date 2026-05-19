
import java.io.File;
import java.util.*;

public class Main {

    private static void collectCells(ASTNode node, Set<String> cells, Map<String, Integer> context) {
        if (node instanceof CellRefNode c) {
            if (!context.containsKey(c.cellName)) {
                cells.add(c.cellName);
            }
        } else if (node instanceof RangeNode r) {
            String startCol = r.start.cellName.replaceAll("[0-9]", "");
            int startRow    = Integer.parseInt(r.start.cellName.replaceAll("[A-Z]", ""));
            String endCol   = r.end.cellName.replaceAll("[0-9]", "");
            int endRow      = Integer.parseInt(r.end.cellName.replaceAll("[A-Z]", ""));

            if (startCol.equals(endCol)) {
                for (int i = startRow; i <= endRow; i++) {
                    String cellName = startCol + i;
                    if (!context.containsKey(cellName)) {
                        cells.add(cellName);
                    }
                }
            }
        } else if (node instanceof FormulaNode) {
            collectCells(((FormulaNode) node).expression, cells, context);
        } else if (node instanceof ArthOpNode) {
            collectCells(((ArthOpNode) node).left, cells, context);
            collectCells(((ArthOpNode) node).right, cells, context);
        } else if (node instanceof FunctionCallNode) {
            for (ASTNode a : ((FunctionCallNode) node).arguments) {
                collectCells(a, cells, context);
            }
        } else if (node instanceof IfNode ifNode) {
            collectCells(ifNode.condition, cells, context);
            collectCells(ifNode.trueBranch, cells, context);
            collectCells(ifNode.falseBranch, cells, context);
        } else if (node instanceof ComparisonNode) {
            collectCells(((ComparisonNode) node).left, cells, context);
            collectCells(((ComparisonNode) node).right, cells, context);
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n======================");
            System.out.println("1) Console Input");
            System.out.println("2) File Input");
            System.out.println("3) Exit");
            System.out.print("Choose: ");

            String choiceStr = sc.nextLine().trim();
            if (choiceStr.equals("3")) break;

            List<String> inputs = new ArrayList<>();
            Map<String, Integer> context = new HashMap<>();

            try {
                // ===== INPUT =====
                if (choiceStr.equals("1")) {
                    System.out.println("Enter formulas (empty line to stop):");
                    while (true) {
                        String line = sc.nextLine().trim();
                        if (line.isEmpty()) break;
                        inputs.add(line);
                    }

                } else if (choiceStr.equals("2")) {
                    System.out.print("File name: ");
                    String fileName = sc.nextLine().trim();
                    Scanner fileScanner = new Scanner(new File(fileName));
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine().trim();
                        if (!line.isEmpty()) inputs.add(line);
                    }
                    fileScanner.close();

                } else {
                    System.out.println("Invalid choice!");
                    continue;
                }

                Set<String> allNeededCells = new LinkedHashSet<>();
                for (String input : inputs) {
                    try {
                        Lexer lexer = new Lexer(input);
                        List<Token> tokens = lexer.tokenize();
                        Parser parser = new Parser(tokens);
                        FormulaNode ast = parser.parseFormula();
                        collectCells(ast, allNeededCells, context);
                    } catch (Exception e) {

                    }
                }

                if (!allNeededCells.isEmpty()) {
                    System.out.println("\nEnter values for cells:");
                    for (String cell : allNeededCells) {
                        System.out.print(cell + " = ");
                        try {
                            int val = Integer.parseInt(sc.nextLine().trim());
                            context.put(cell, val);
                        } catch (Exception e) {
                            System.out.println("Invalid number, using 0");
                            context.put(cell, 0);
                        }
                    }
                }

                // ===== PROCESS =====
                for (String input : inputs) {
                    System.out.println("\n======================");
                    System.out.println("INPUT: " + input);

                    try {
                        // Lexer
                        Lexer lexer = new Lexer(input);
                        List<Token> tokens = lexer.tokenize();

                        System.out.println("\n=== TOKENS ===");
                        for (Token t : tokens) {
                            System.out.println(t.getType() + " -> " + t.getLexeme());
                        }

                        // Parser
                        Parser parser = new Parser(tokens);
                        FormulaNode ast = parser.parseFormula();

                        // AST
                        System.out.println("\n=== AST TEXT ===");
                        System.out.println(ast.toString());

                        System.out.println("\n=== AST TREE ===");
                        ASTPrinter printer = new ASTPrinter();
                        printer.print(ast, context);

                        // Result
                        System.out.println("\n=== RESULT ===");
                        System.out.println(ast.evaluate(context));

                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        sc.close();
        System.out.println("Goodbye!");
    }
}