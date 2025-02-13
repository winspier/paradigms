package expression.exceptions;

public class MissedArgumentsException extends ParserException {
    public MissedArgumentsException(String message, int pos) {
        super(message, pos);
    }
}
