package expression.exceptions;
public class OverflowException extends ArithmeticException {
    public OverflowException(String message) {
        super(message);
    }
    public OverflowException(String type, String operation, Object a, Object b) {
        super("Overflow " + type + " caused " + a + " " + operation + " " + b);
    }
    public OverflowException(String type, String operation, Object a) {
        super("Overflow " + type + " caused " + operation + " " + a);
    }
}
