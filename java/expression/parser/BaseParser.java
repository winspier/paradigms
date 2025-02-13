package expression.parser;

public class BaseParser {
    private static final char END = '\0';
    private final CharSource source;
    private char ch = 0xffff;

    protected BaseParser(final CharSource source) {
        this.source = source;
        take();
    }

    protected char take() {
        final char result = ch;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected char current() {
        return ch;
    }

    protected boolean test(final char... expected) {
        for (char expectedChar : expected) {
            if (ch == expectedChar) {
                return true;
            }
        }
        return false;
    }

    protected String take(final String... expectedArray) {
        int result = takeId(expectedArray);
        return result >= 0 ? expectedArray[result] : null;
    }

    protected int takeId(final String... expectedArray) {
        int prevPos = source.getPosition();
        char prevSymbol = ch;
        FirstFor:
        for (int id = 0; id < expectedArray.length; id++) {
            for (int i = 0; i < expectedArray[id].length(); i++) {
                if (!take(expectedArray[id].charAt(i))) {
                    source.setPosition(prevPos);
                    ch = prevSymbol;
                    continue FirstFor;
                }
            }
            return id;
        }
        return -1;
    }

    protected boolean take(final char... expected) {
        for (char expectedChar : expected) {
            if (test(expectedChar)) {
                take();
                return true;
            }
        }
        return false;
    }

    protected void expect(final char expected) {
        if (!take(expected)) {
            throw error("Expected '" + expected + "', found '" + ch + "'");
        }
    }

    protected void expect(final String value) {
        for (final char c : value.toCharArray()) {
            expect(c);
        }
    }

    protected String nextToken() {
        skipWhitespace();
        StringBuilder result = new StringBuilder();
        while (ch != END && !Character.isWhitespace(ch)) {
            result.append(take());
        }
        return result.toString();
    }

    protected int getPosition() {
        return source.getPosition() - 1;
    }

    protected boolean eof() {
        return take(END);
    }

    protected IllegalArgumentException error(final String message) {
        return source.error(message);
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }

    protected boolean isDigit() {
        return Character.isDigit(ch);
    }

    protected boolean isWhitespace() {
        return Character.isWhitespace(ch);
    }

    protected void skipWhitespace() {
        while (Character.isWhitespace(ch) && ch != END) {
            take();
        }
    }
}
