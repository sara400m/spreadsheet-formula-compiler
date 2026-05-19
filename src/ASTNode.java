
import java.util.*;

public abstract class ASTNode {
    public abstract String toString();
    public abstract int evaluate(Map<String, Integer> context);
}

// ========== Formula ==========
class FormulaNode extends ASTNode {
    public ASTNode expression;

    public FormulaNode(ASTNode expression) {
        this.expression = expression;
    }

    public String toString() {
        return "Formula(" + expression + ")";
    }

    public int evaluate(Map<String, Integer> context) {
        return expression.evaluate(context);
    }
}

// ========== Number ==========
class NumberNode extends ASTNode {
    public int value;

    public NumberNode(int value) {
        this.value = value;
    }

    public String toString() {
        return "Number(" + value + ")";
    }

    public int evaluate(Map<String, Integer> context) {
        return value;
    }
}

// ========== Cell ==========
class CellRefNode extends ASTNode {
    public String cellName;

    public CellRefNode(String cellName) {
        this.cellName = cellName;
    }

    public String toString() {
        return "CellRef(" + cellName + ")";
    }

    public int evaluate(Map<String, Integer> context) {
        return context.getOrDefault(cellName, 0);
    }
}

// ========== Arth ==========
class ArthOpNode extends ASTNode {
    public String operator;
    public ASTNode left, right;

    public ArthOpNode(String operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "ArthOp(" + left + " " + operator + " " + right + ")";
    }

    public int evaluate(Map<String, Integer> context) {
        int l = left.evaluate(context);
        int r = right.evaluate(context);

        return switch (operator) {
            case "+" -> l + r;
            case "-" -> l - r;
            case "*" -> l * r;
            case "/" -> l / r;
            default -> 0;
        };
    }
}

class ComparisonNode extends ASTNode {
    public String operator;
    public ASTNode left, right;

    public ComparisonNode(String operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String toString() {
        return "Comparison(" + left + " " + operator + " " + right + ")";
    }

    public int evaluate(Map<String, Integer> context) {
        int l = left.evaluate(context);
        int r = right.evaluate(context);

        return switch (operator) {
            case ">" -> l > r ? 1 : 0;
            case "<" -> l < r ? 1 : 0;
            case "=" -> l == r ? 1 : 0;
            case ">=" -> l >= r ? 1 : 0;
            case "<=" -> l <= r ? 1 : 0;
            case "<>" -> l != r ? 1 : 0;
            default -> 0;
        };
    }
}

class IfNode extends ASTNode {
    public ASTNode condition, trueBranch, falseBranch;

    public IfNode(ASTNode c, ASTNode t, ASTNode f) {
        condition = c;
        trueBranch = t;
        falseBranch = f;
    }

    public String toString() {
        return "IF(" + condition + "," + trueBranch + "," + falseBranch + ")";
    }

    public int evaluate(Map<String, Integer> context) {
        return (condition.evaluate(context) != 0)
                ? trueBranch.evaluate(context)
                : falseBranch.evaluate(context);
    }
}

class FunctionCallNode extends ASTNode {
    public String functionName;
    public List<ASTNode> arguments;

    public FunctionCallNode(String fn, List<ASTNode> args) {
        functionName = fn;
        arguments = args;
    }

    public String toString() {
        return "FunctionCall(" + functionName + ")";
    }

    public int evaluate(Map<String, Integer> context) {
        switch (functionName) {
            case "SUM": {
                int res = 0;
                for (ASTNode a : arguments) {
                    res += a.evaluate(context);
                }
                return res;
            }
            case "MAX": {
                int max = Integer.MIN_VALUE;
                for (ASTNode a : arguments) {
                    int val = a.evaluate(context);
                    if (val > max) max = val;
                }
                return max;
            }
            case "MIN": {
                int min = Integer.MAX_VALUE;
                for (ASTNode a : arguments) {
                    int val = a.evaluate(context);
                    if (val < min) min = val;
                }
                return min;
            }
            default:
                return 0;
        }
    }
}
class RangeNode extends ASTNode {

    public CellRefNode start;
    public CellRefNode end;

    public RangeNode(CellRefNode start, CellRefNode end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "Range(" + start.cellName + ":" + end.cellName + ")";
    }

    @Override
    public int evaluate(Map<String, Integer> context) {
        String startCol = start.cellName.replaceAll("[0-9]", "");
        int startRow    = Integer.parseInt(start.cellName.replaceAll("[A-Z]", ""));
        String endCol   = end.cellName.replaceAll("[0-9]", "");
        int endRow      = Integer.parseInt(end.cellName.replaceAll("[A-Z]", ""));

        int sum = 0;
        if (startCol.equals(endCol)) {
            for (int i = startRow; i <= endRow; i++) {
                String cellName = startCol + i;
                sum += context.getOrDefault(cellName, 0);
            }
        }
        return sum;
    }
}