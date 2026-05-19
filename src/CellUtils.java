public class CellUtils {

    public static int getColumnNumber(String col) {
        int result = 0;
        for (char c : col.toCharArray()) {
            result = result * 26 + (c - 'A' + 1);
        }
        return result;
    }

    public static boolean isValidRange(String cell1 ,String cell2) {

        String word1 = cell1.replaceAll("[0-9]", "");
        int num1 = Integer.parseInt(cell1.replaceAll("[A-Z]", ""));

        String word2 = cell2.replaceAll("[0-9]", "");
        int num2 = Integer.parseInt(cell2.replaceAll("[A-Z]", ""));

        int colNum1 = getColumnNumber(word1);
        int colNum2 = getColumnNumber(word2);

        return colNum1 <= colNum2 && num1 <= num2;
    }
}