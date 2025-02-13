package expression.parser.operations;

import java.util.List;

public abstract class AbstractBinary<T> extends MyGenericExpression<T> {
    protected final MyGenericExpression<T> left;
    protected final MyGenericExpression<T> right;

    public AbstractBinary(MyGenericExpression<T> left, MyGenericExpression<T> right) {
        super(left.type);
        if (!left.type.equals(right.type)) {
            throw new IllegalArgumentException("Expected equal type of left and right expression");
        }
        this.left = left;
        this.right = right;
    }

    public abstract String getSign();

    protected abstract int getPriority();

    protected abstract T getResult(T a, T b);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }

    @Override
    public void toString(StringBuilder sb) {
        sb.append("(");
        left.toString(sb);
        sb.append(" ").append(getSign()).append(" ");
        right.toString(sb);
        sb.append(")");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractBinary<?> exp) {
            return this.left.equals(exp.left) && this.right.equals(exp.right);
        }
        return false;
    }

    private void appendWithParenthesesIfNecessary(MyGenericExpression<T> expression, StringBuilder sb, boolean isRight) {
        if (expression instanceof AbstractBinary<?> binExp &&
                // :NOTE: instanceof
                !(expression instanceof HalfAssociative && expression.getClass() == this.getClass()) &&
                ((binExp.getPriority() > this.getPriority()) ||
                        isRight &&
                                binExp.getPriority() == this.getPriority() &&
                                !(expression instanceof Associative) &&
                                (!(this instanceof Commutativity) || !(expression instanceof HalfCommutativity))
                )
        ) {
            sb.append("(");
            expression.toMiniString(sb);
            sb.append(")");
        } else {
            expression.toMiniString(sb);
        }
    }

    @Override
    public void toMiniString(StringBuilder sb) {
        appendWithParenthesesIfNecessary(left, sb, false);
        sb.append(" ").append(getSign()).append(" ");
        appendWithParenthesesIfNecessary(right, sb, true);
    }

    @Override
    public String toMiniString() {
        StringBuilder sb = new StringBuilder();
        toMiniString(sb);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return (left.hashCode() * 100019 + right.hashCode()) * 100019 + getSign().hashCode();
    }

    @Override
    public T evaluate(T x) {
        return getResult(left.evaluate(x), right.evaluate(x));
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return getResult(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    @Override
    public T evaluate(List<T> variables) {
        return getResult(left.evaluate(variables), right.evaluate(variables));
    }
}
