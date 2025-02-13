package expression.parser.operations;

import expression.parser.types.ParsingType;

import java.util.List;

public class Const<T> extends AbstractValue<T> {
    private final T value;

    public Const(ParsingType<T> type, T value) {
        super(type);
        this.value = value;
    }
    @Override
    public String toString() {
        return value.toString();
    }

    private T evaluate() {
        return value;
    }

    @Override
    public T evaluate(T x) {
        return value;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return value;
    }

    @Override
    public T evaluate(List<T> variables) {
        return evaluate();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Const<?> objValue) {
            return this.value.equals(objValue.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
