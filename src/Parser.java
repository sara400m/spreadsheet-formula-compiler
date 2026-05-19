import java.util.*;

public class Parser {

    private final List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    private Token peek() {
        return (pos < tokens.size()) ? tokens.get(pos) : null;
    }

    private Token consume() {
        return tokens.get(pos++);
    }

    private Token expect(TokenType type) {
        Token t = peek();
        if (t == null || t.getType() != type) {
            String found = (t == null) ? "nothing" : t.getLexeme();
            throw new RuntimeException("Syntax Error: expected " + type + " but found '" + found + "'");
        }
        return consume();
    }

    private boolean check(TokenType type) {
        Token t = peek();
        return t != null && t.getType() == type;
    }

    // Formula = "=" Expression
    public FormulaNode parseFormula() throws Exception {
        expect(TokenType.EQUAL);
        return new FormulaNode(parseExpression());
    }

    // Expression = Term { Op Term }
    private ASTNode parseExpression() throws Exception {
        ASTNode left = parseTerm();
        while (check(TokenType.OP)) {
            String op = consume().getLexeme();
            ASTNode right = parseTerm();
            left = new ArthOpNode(op, left, right);
        }
        return left;
    }

    // Term = Factor { HOp Factor }
    private ASTNode parseTerm() throws Exception {
        ASTNode left = parseFactor();
        while (check(TokenType.HOP)) {
            String op = consume().getLexeme();
            ASTNode right = parseFactor();
            left = new ArthOpNode(op, left, right);
        }
        return left;
    }

    // Factor = Number | Range | CellReference | FunctionCall | IfCondition | "(" Expression ")"
    private ASTNode parseFactor() throws Exception {
        Token t = peek();
        if (t == null) throw new RuntimeException("Syntax Error: unexpected end of input");

        // 1. "(" Expression ")"
        if (t.getType() == TokenType.LEFTPAREN) {
            consume();
            ASTNode expr = parseExpression();
            expect(TokenType.RIGHTPAREN);
            return expr;
        }

        if (t.getType() == TokenType.INTEGER) {
            return new NumberNode(Integer.parseInt(consume().getLexeme()));
        }

        if (t.getType() == TokenType.IF) {
            return parseIfCondition();
        }

        if (t.getType() == TokenType.FUNCTION) {
            return parseFunctionCall();
        }

        if (t.getType() == TokenType.CELL) {
            Token startCell = consume();
            if (check(TokenType.COLON)) {
                consume();
                Token endCell = expect(TokenType.CELL);
                if(!CellUtils.isValidRange(startCell.getLexeme(),endCell.getLexeme()))
                   throw new RuntimeException("semantic Error: startCell is greater than endCell");

                return new RangeNode(new CellRefNode(startCell.getLexeme()),
                        new CellRefNode(endCell.getLexeme()));
            }
            return new CellRefNode(startCell.getLexeme());
        }

        throw new RuntimeException("Syntax Error: unexpected token '" + t.getLexeme() + "'");
    }

    // FunctionCall = FnName "(" ArgumentList ")"
    private FunctionCallNode parseFunctionCall() throws Exception {
        String fnName = consume().getLexeme();
        expect(TokenType.LEFTPAREN);
        List<ASTNode> args = parseArgumentList();
        expect(TokenType.RIGHTPAREN);
        return new FunctionCallNode(fnName, args);
    }

    // ArgumentList = Expression { "," Expression }
    private List<ASTNode> parseArgumentList() throws Exception {
        List<ASTNode> args = new ArrayList<>();
        args.add(parseExpression());
        while (check(TokenType.COMMA)) {
            consume();
            args.add(parseExpression());
        }
        return args;
    }

    // IfCondition = "IF" "(" Comparison "," Expression "," Expression ")"
    private IfNode parseIfCondition() throws Exception {
        expect(TokenType.IF);
        expect(TokenType.LEFTPAREN);
        ASTNode condition = parseComparison();
        expect(TokenType.COMMA);
        ASTNode trueBranch = parseExpression();
        expect(TokenType.COMMA);
        ASTNode falseBranch = parseExpression();
        expect(TokenType.RIGHTPAREN);
        return new IfNode(condition, trueBranch, falseBranch);
    }

    // Comparison = Expression CompOp Expression
    private ComparisonNode parseComparison() throws Exception {
        ASTNode left = parseExpression();
        Token op = expect(TokenType.COMPARE);
        ASTNode right = parseExpression();
        return new ComparisonNode(op.getLexeme(), left, right);
    }
}