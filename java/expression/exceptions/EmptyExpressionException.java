package expression.exceptions;

public class EmptyExpressionException extends ParserException {
    public EmptyExpressionException(String message, int pos) {
        super(message, pos);
    }
}
