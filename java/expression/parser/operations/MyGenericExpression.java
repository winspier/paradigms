package expression.parser.operations;

import expression.parser.types.ParsingType;

import java.util.List;

public abstract class MyGenericExpression<T> {
    protected final ParsingType<T> type;

    public MyGenericExpression(ParsingType<T> type) {
        this.type = type;
    }

    public abstract void toMiniString(StringBuilder sb);

    public abstract void toString(StringBuilder sb);

    public abstract String toMiniString();

    public abstract String toString();

    public abstract T evaluate(T x);

    public abstract T evaluate(T x, T y, T z);

    public abstract T evaluate(List<T> variables);

}
