package expression.parser.operations;

import expression.parser.types.ParsingType;

import java.util.List;

public abstract class AbstractValue<T> extends MyGenericExpression<T> {
    public AbstractValue(ParsingType<T> type) {
        super(type);
    }

    @Override
    public abstract T evaluate(T x);

    @Override
    public abstract T evaluate(T x, T y, T z);

    @Override
    public abstract T evaluate(List<T> variables);

    @Override
    public void toMiniString(StringBuilder sb) {
        sb.append(this);
    }

    @Override
    public void toString(StringBuilder sb) {
        sb.append(this);
    }

    @Override
    public abstract String toString();

    @Override
    public String toMiniString() {
        return toString();
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

}
