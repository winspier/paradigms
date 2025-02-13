package expression.parser.types;

public interface ParsingType<T> {
    T add(T a, T b);

    T subtract(T a, T b);

    T multiply(T a, T b);

    T divide(T a, T b);

    T min(T a, T b);

    T max(T a, T b);

    T negate(T a);

    T parse(String s);

    T valueOf(int a);

    T bitCount(T a);

    T shiftA(T a, T b);

    T shiftR(T a, T b);

    T shiftL(T a, T b);

    default UnsupportedOperationException unsupportedOperation(String message) {
        return new UnsupportedOperationException(message);
    }
}
