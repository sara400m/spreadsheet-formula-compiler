
import java.util.*;

public class Lexer {
    private String input;
    private int currentPosition = 0;
    private List<Token> tokens = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {

        while (currentPosition < input.length()) {
            char c = input.charAt(currentPosition);

            if (Character.isWhitespace(c)) {
                currentPosition++;
                continue;
            }

            if (c == '=') {
                if (currentPosition + 1 < input.length() &&
                        input.charAt(currentPosition + 1) == '=') {
                    tokens.add(new Token(TokenType.COMPARE, "=", currentPosition));
                    currentPosition += 2;
                } else {
                    tokens.add(new Token(TokenType.EQUAL, "=", currentPosition++));
                }
                continue;
            }

            if (c == '+' || c == '-') {
                tokens.add(new Token(TokenType.OP, String.valueOf(c), currentPosition++));
                continue;
            }

            if (c == '*' || c == '/') {
                tokens.add(new Token(TokenType.HOP, String.valueOf(c), currentPosition++));
                continue;
            }

            if (c == '(') {
                tokens.add(new Token(TokenType.LEFTPAREN, "(", currentPosition++));
                continue;
            }

            if (c == ')') {
                tokens.add(new Token(TokenType.RIGHTPAREN, ")", currentPosition++));
                continue;
            }

            if (c == ',') {
                tokens.add(new Token(TokenType.COMMA, ",", currentPosition++));
                continue;
            }

            if (c == ':') {
                tokens.add(new Token(TokenType.COLON, ":", currentPosition++));
                continue;
            }

            // LOOKAHEAD
            if (c == '>' || c == '<') {
                if (currentPosition + 1 < input.length()) {
                    char next = input.charAt(currentPosition + 1);

                    if (c == '>' && next == '=') {
                        tokens.add(new Token(TokenType.COMPARE, ">=", currentPosition));
                        currentPosition += 2;
                        continue;
                    }
                    if (c == '<' && next == '=') {
                        tokens.add(new Token(TokenType.COMPARE, "<=", currentPosition));
                        currentPosition += 2;
                        continue;
                    }
                    if (c == '<' && next == '>') {
                        tokens.add(new Token(TokenType.COMPARE, "<>", currentPosition));
                        currentPosition += 2;
                        continue;
                    }
                }
                tokens.add(new Token(TokenType.COMPARE, String.valueOf(c), currentPosition++));
                continue;
            }

            // numbers
            if (Character.isDigit(c)) {
                int start = currentPosition;
                while (currentPosition < input.length() &&
                        Character.isDigit(input.charAt(currentPosition))) {
                    currentPosition++;
                }
                tokens.add(new Token(TokenType.INTEGER,
                        input.substring(start, currentPosition), start));
                continue;
            }

            // identifiers
            if (Character.isUpperCase(c)) {
                int start = currentPosition;

                while (currentPosition < input.length() &&
                        Character.isUpperCase(input.charAt(currentPosition))) {
                    currentPosition++;
                }

                String word = input.substring(start, currentPosition);

                TokenType keyword = SpreadsheetLang.getIdentifierType(word);

                if ("IF".equals(word)) {
                    if (currentPosition < input.length()) {
                        char next = input.charAt(currentPosition);
                        if (next == '(' || Character.isWhitespace(next)) {
                            tokens.add(new Token(TokenType.IF, word, start));
                            continue;
                        }
                    }
                }

                if (keyword == TokenType.FUNCTION) {
                    tokens.add(new Token(TokenType.FUNCTION, word, start));
                    continue;
                }

                // CELL
                if (currentPosition < input.length() &&
                        input.charAt(currentPosition) >= '1' &&
                        input.charAt(currentPosition) <= '9') {

                    while (currentPosition < input.length() &&
                            Character.isDigit(input.charAt(currentPosition))) {
                        currentPosition++;
                    }

                    tokens.add(new Token(TokenType.CELL,
                            input.substring(start, currentPosition), start));
                    continue;
                }

                errors.add("Lexical Error at position " + start);
                break;
            }

            errors.add("Lexical Error at " + currentPosition);
            currentPosition++;
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException(String.join("\n", errors));
        }

        return tokens;
    }
}