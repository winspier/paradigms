package expression.parser.operations;

import java.util.List;

public abstract class AbstractUnary<T> extends MyGenericExpression<T> {
    protected final MyGenericExpression<T> expression;

    public AbstractUnary(MyGenericExpression<T> expression) {
        super(expression.type);
        this.expression = expression;
    }

    protected abstract String getSign();

    protected abstract T getResult(T a);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }

    @Override
    public void toString(StringBuilder sb) {
        sb.append(getSign()).append("(");
        expression.toString(sb);
        sb.append(")");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractUnary<?> other) {
            return this.expression.equals(other.expression);
        }
        return false;
    }

    @Override
    public void toMiniString(StringBuilder sb) {
        if (expression instanceof AbstractValue || expression instanceof AbstractUnary) {
            sb.append(getSign()).append(" ").append(expression.toMiniString());
        } else {
            sb.append(getSign()).append("(");
            sb.append(expression.toMiniString());
            sb.append(")");
        }
    }

    @Override
    public String toMiniString() {
        StringBuilder sb = new StringBuilder();
        toMiniString(sb);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return expression.hashCode() * 100019 + getSign().hashCode();
    }

    @Override
    public T evaluate(T x) {
        return getResult(expression.evaluate(x));
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return getResult(expression.evaluate(x, y, z));
    }

    @Override
    public T evaluate(List<T> variables) {
        return getResult(expression.evaluate(variables));
    }
}
