package expression.exceptions;

public class UnsupportedTokenException extends ParserException {
    public UnsupportedTokenException(String token, int pos) {
        super("Unsupported token: " + token, pos);
    }
}
