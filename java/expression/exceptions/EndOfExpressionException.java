package expression.exceptions;

public class EndOfExpressionException extends ParserException {
    public EndOfExpressionException(String message, int pos) {
        super(message, pos);
    }
}
