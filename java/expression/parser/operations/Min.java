package expression.parser.operations;

public class Min<T> extends AbstractBinary<T> implements HalfAssociative {
    public Min(MyGenericExpression<T> left, MyGenericExpression<T> right) {
        super(left, right);
    }

    @Override
    public String getSign() {
        return "min";
    }

    @Override
    protected int getPriority() {
        return 3;
    }

    @Override
    protected T getResult(T a, T b) {
        return type.min(a, b);
    }
}
