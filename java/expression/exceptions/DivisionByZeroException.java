package expression.exceptions;

public class DivisionByZeroException extends ArithmeticException {
    public DivisionByZeroException(String message) {
        super(message);
    }
    public DivisionByZeroException(Object a, Object b) {
        super("Division by zero: " + a + " / " + b);
    }
}
