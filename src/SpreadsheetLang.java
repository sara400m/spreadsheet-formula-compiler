import java.util.*;

public class SpreadsheetLang {

    public static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("SUM", TokenType.FUNCTION);
        KEYWORDS.put("MIN", TokenType.FUNCTION);
        KEYWORDS.put("MAX", TokenType.FUNCTION);
        KEYWORDS.put("IF", TokenType.IF);
    }

    public static TokenType getIdentifierType(String identifier) {

        return KEYWORDS.getOrDefault(identifier.toUpperCase(), null);
    }
}