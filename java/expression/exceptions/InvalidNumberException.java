package expression.exceptions;

public class InvalidNumberException extends ParserException {
    public InvalidNumberException(String message, int pos) {
        super(message, pos);
    }
}
