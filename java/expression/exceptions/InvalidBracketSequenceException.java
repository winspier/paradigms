package expression.exceptions;

public class InvalidBracketSequenceException extends ParserException {
    public InvalidBracketSequenceException(String message, int pos) {
        super(message, pos);
    }
}
