package javaapplication23;

import java.util.Map;

public class ASTPrinter {

    private Map<String, Integer> context;

    public void print(ASTNode node, Map<String, Integer> context) {
        this.context = context;
        printNode(node, "", true);
    }

    private void printNode(ASTNode node, String prefix, boolean isLast) {

        String connector = "+-- ";
        String childPrefix = isLast ? "    " : "|   ";

        if (node instanceof FormulaNode f) {

            System.out.println(prefix + connector + "Formula");
            printNode(f.expression, prefix + childPrefix, true);

        } else if (node instanceof IfNode ifNode) {

            System.out.println(prefix + connector + "IF");
            printNode(ifNode.condition, prefix + childPrefix, false);
            printNode(ifNode.trueBranch, prefix + childPrefix, false);
            printNode(ifNode.falseBranch, prefix + childPrefix, true);

        } else if (node instanceof ComparisonNode c) {

            System.out.println(prefix + connector + "Comparison: " + c.operator);
            printNode(c.left, prefix + childPrefix, false);
            printNode(c.right, prefix + childPrefix, true);

        } else if (node instanceof ArthOpNode b) {

            System.out.println(prefix + connector + "ArthOp: " + b.operator);
            printNode(b.left, prefix + childPrefix, false);
            printNode(b.right, prefix + childPrefix, true);

        } else if (node instanceof FunctionCallNode fn) {

            System.out.println(prefix + connector + "FunctionCall: " + fn.functionName);

            for (int i = 0; i < fn.arguments.size(); i++) {
                boolean last = (i == fn.arguments.size() - 1);
                printNode(fn.arguments.get(i), prefix + childPrefix, last);
            }

        } else if (node instanceof RangeNode r) {

            System.out.println(prefix + connector + "Range");
            printNode(r.start, prefix + childPrefix, false);
            printNode(r.end, prefix + childPrefix, true);

        } else if (node instanceof CellRefNode c) {

            int value = context.getOrDefault(c.cellName, 0);
            System.out.println(prefix + connector + "CellRef: " /*+c.cellName + "=" */+ value);

        } else if (node instanceof NumberNode n) {

            System.out.println(prefix + connector + n.value);
        }
    }
}