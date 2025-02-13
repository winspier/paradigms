package expression.parser;

public interface CharSource {
    boolean hasNext();

    char next();

    int getPosition();

    int setPosition(int pos);

    IllegalArgumentException error(String message);
}
