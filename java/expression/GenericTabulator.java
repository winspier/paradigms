package expression;

import expression.exceptions.ArithmeticException;
import expression.parser.ExpressionParser;
import expression.parser.types.*;

public class GenericTabulator implements expression.generic.Tabulator {

    public static void main(String[] args) throws Exception {
        if (args.length != 2 || args[0].length() < 2 || args[0].charAt(0) != '-' || args[1].isEmpty()) {
            System.out.println("Usage: <mode> <expression>");
            System.out.println("modes: -i, -d, -bi, -u, -sat, -b");
            System.exit(0);
        }
        var tabulator = new GenericTabulator();
        try {
            printTable(tabulator.tabulate(
                            args[0].substring(1), args[1],
                            -2, 2, -2, 2, -2, 2
                    ),
                    -2, -2, -2
            );
        } catch (IllegalStateException e) {
            System.err.println("failed: " + e.getMessage());
            System.exit(0);
        }
    }

    private static void printTable(Object[][][] table, int dx, int dy, int dz) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                for (int k = 0; k < table[i][j].length; k++) {
                    System.out.printf("f(%d, %d, %d) = %s%n", i + dx, j + dy, k + dz, table[i][j][k]);
                }
            }
        }
    }

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws IllegalStateException {
        ParsingType<?> type = switch (mode) {
            case "i" -> new CheckedIntegerType();
            case "u" -> new IntegerType();
            case "sat" -> new SaturationIntegerType();
            case "b" -> new ByteType();
            case "d" -> new DoubleType();
            case "bi" -> new BigIntegerType();
            default -> throw new IllegalStateException("Unexpected mode: " + mode);
        };
        return tabulator(type, expression, x1, x2, y1, y2, z1, z2);
    }

    private static <T> Object[][][] tabulator(ParsingType<T> type, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        var expr = new ExpressionParser<T>().parse(type, expression);
        Object[][][] result = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        result[i - x1][j - y1][k - z1] = expr.evaluate(type.valueOf(i), type.valueOf(j), type.valueOf(k));
                    } catch (ArithmeticException ignored) {
                    }
                }
            }
        }
        return result;
    }
}
